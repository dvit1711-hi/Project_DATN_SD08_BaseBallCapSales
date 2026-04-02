package com.example.project_datn_sd08_baseballcapsales.Service.Impl;

import com.example.project_datn_sd08_baseballcapsales.Service.AdminStaffService;
import com.example.project_datn_sd08_baseballcapsales.payload.request.CreateStaffAccountRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.CreateStaffAccountResponse;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.OptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminStaffServiceImpl implements AdminStaffService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<OptionResponse> getStatuses() {
        String sql = """
                SELECT statusID, statusName
                FROM Status
                ORDER BY statusID
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new OptionResponse(
                        rs.getInt("statusID"),
                        rs.getString("statusName")
                )
        );
    }

    @Override
    public CreateStaffAccountResponse createStaffAccount(CreateStaffAccountRequest request) {
        validateRequest(request);

        String username = request.getUsername().trim();
        String email = request.getEmail().trim();
        String phoneNumber = request.getPhoneNumber().trim();
        String rawPassword = request.getPassword();
        String images = StringUtils.hasText(request.getImages()) ? request.getImages().trim() : null;
        Integer statusID = request.getStatusID() == null ? 1 : request.getStatusID();

        Integer usernameCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM Accounts WHERE username = ?",
                Integer.class,
                username
        );
        if (usernameCount != null && usernameCount > 0) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        Integer emailCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM Accounts WHERE email = ?",
                Integer.class,
                email
        );
        if (emailCount != null && emailCount > 0) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Integer statusCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM Status WHERE statusID = ?",
                Integer.class,
                statusID
        );
        if (statusCount == null || statusCount == 0) {
            throw new RuntimeException("Trạng thái tài khoản không tồn tại");
        }

        Integer roleID = jdbcTemplate.queryForObject(
                "SELECT TOP 1 roleID FROM Roles WHERE roleName = ?",
                Integer.class,
                "ROLE_STAFF"
        );
        if (roleID == null) {
            throw new RuntimeException("Không tìm thấy role ROLE_STAFF");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    INSERT INTO Accounts (username, password, email, phoneNumber, images, statusID)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setString(2, encodedPassword);
            ps.setString(3, email);
            ps.setString(4, phoneNumber);
            ps.setString(5, images);
            ps.setInt(6, statusID);
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new RuntimeException("Không tạo được tài khoản staff");
        }

        Integer accountID = generatedId.intValue();

        jdbcTemplate.update(
                "INSERT INTO AccountRoles (accountID, roleID) VALUES (?, ?)",
                accountID,
                roleID
        );

        String statusName = jdbcTemplate.queryForObject(
                "SELECT statusName FROM Status WHERE statusID = ?",
                String.class,
                statusID
        );

        return new CreateStaffAccountResponse(
                accountID,
                username,
                email,
                phoneNumber,
                statusID,
                statusName,
                "ROLE_STAFF"
        );
    }

    private void validateRequest(CreateStaffAccountRequest request) {
        if (request == null) {
            throw new RuntimeException("Dữ liệu không hợp lệ");
        }
        if (!StringUtils.hasText(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập không được để trống");
        }
        if (request.getUsername().trim().length() < 3) {
            throw new RuntimeException("Tên đăng nhập tối thiểu 3 ký tự");
        }
        if (!StringUtils.hasText(request.getEmail())) {
            throw new RuntimeException("Email không được để trống");
        }
        if (!request.getEmail().matches(".+@.+\\..+")) {
            throw new RuntimeException("Email không hợp lệ");
        }
        if (!StringUtils.hasText(request.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }
        if (!request.getPhoneNumber().trim().matches("^[0-9]{9,11}$")) {
            throw new RuntimeException("Số điện thoại không hợp lệ");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
        if (request.getPassword().length() < 6) {
            throw new RuntimeException("Mật khẩu tối thiểu 6 ký tự");
        }
    }
}