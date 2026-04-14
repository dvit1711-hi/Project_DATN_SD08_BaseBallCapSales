package com.example.project_datn_sd08_baseballcapsales.Service.auth;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.PasswordResetOtp;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.PasswordResetOtpRepository;
import com.example.project_datn_sd08_baseballcapsales.Service.jwt.JWTService;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.LoginResponse;
import com.example.project_datn_sd08_baseballcapsales.payload.request.LoginRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AccountRepository accountRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public AuthService(AuthenticationManager authenticationManager,
                       JWTService jwtService,
                       AccountRepository accountRepository,
                       PasswordResetOtpRepository passwordResetOtpRepository,
                       PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.accountRepository = accountRepository;
        this.passwordResetOtpRepository = passwordResetOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Account account = accountRepository.findWithStatusByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));

            String statusName = "";
            if (account.getStatus() != null && account.getStatus().getStatusName() != null) {
                statusName = account.getStatus().getStatusName().trim();
            }

            if (!"Active".equalsIgnoreCase(statusName)) {
                if ("Locked".equalsIgnoreCase(statusName)) {
                    throw new RuntimeException("Tài khoản đã bị khóa");
                }

                if ("Banned".equalsIgnoreCase(statusName)) {
                    throw new RuntimeException("Tài khoản đã bị cấm");
                }

                if ("Pending".equalsIgnoreCase(statusName)) {
                    throw new RuntimeException("Tài khoản đang chờ duyệt");
                }

                if ("Inactive".equalsIgnoreCase(statusName)) {
                    throw new RuntimeException("Tài khoản chưa kích hoạt");
                }

                throw new RuntimeException("Tài khoản không hợp lệ");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateToken(userDetails);

            Set<String> roles = account.getAccountRoles()
                    .stream()
                    .map(ar -> ar.getRole().getRoleName())
                    .collect(Collectors.toSet());

            return new LoginResponse(
                    accessToken,
                    account.getId(),
                    account.getUsername(),
                    account.getEmail(),
                    roles
            );

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        } catch (LockedException e) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        } catch (DisabledException e) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt");
        }
    }

    public String sendForgotPasswordOtp(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        passwordResetOtpRepository.deleteByAccount_IdAndUsedFalse(account.getId());

        String otp = String.format("%06d", new Random().nextInt(1000000));

        PasswordResetOtp resetOtp = new PasswordResetOtp();
        resetOtp.setAccount(account);
        resetOtp.setOtpHash(passwordEncoder.encode(otp));
        resetOtp.setExpiryAt(LocalDateTime.now().plusMinutes(5));
        resetOtp.setUsed(false);
        resetOtp.setAttemptCount(0);
        resetOtp.setCreatedAt(LocalDateTime.now());

        passwordResetOtpRepository.save(resetOtp);

        sendOtpEmail(account.getEmail(), otp);

        return "Đã gửi OTP về email";
    }

    public String resetPasswordByOtp(String email, String otp, String newPassword, String confirmPassword) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (newPassword == null || confirmPassword == null
                || newPassword.isBlank() || confirmPassword.isBlank()) {
            throw new RuntimeException("Vui lòng nhập đầy đủ mật khẩu");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Mật khẩu nhập lại không khớp");
        }

        PasswordResetOtp resetOtp = passwordResetOtpRepository
                .findTopByAccount_IdAndUsedFalseOrderByCreatedAtDesc(account.getId())
                .orElseThrow(() -> new RuntimeException("OTP không tồn tại hoặc đã được dùng"));

        if (resetOtp.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        if (!passwordEncoder.matches(otp, resetOtp.getOtpHash())) {
            resetOtp.setAttemptCount(resetOtp.getAttemptCount() + 1);
            passwordResetOtpRepository.save(resetOtp);
            throw new RuntimeException("OTP không đúng");
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        resetOtp.setUsed(true);
        resetOtp.setUsedAt(LocalDateTime.now());
        passwordResetOtpRepository.save(resetOtp);

        return "Đổi mật khẩu thành công";
    }

    private void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã OTP đặt lại mật khẩu");
        message.setText("Mã OTP của bạn là: " + otp + "\nOTP có hiệu lực trong 5 phút.");
        mailSender.send(message);
    }
}