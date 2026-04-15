package com.example.project_datn_sd08_baseballcapsales.Configuration;

import com.example.project_datn_sd08_baseballcapsales.Service.jwt.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/auth/login",
                                "/auth/register/**",
                                "/auth/forgot-password/**",
                                "/css/**",
                                "/images/**",
                                "/api/**"
                        ).permitAll()

                        // POS chỉ staff/admin
                        .requestMatchers("/api/pos/**").hasAnyRole("ADMIN", "STAFF")

                        // account API: chỉ cần đăng nhập
                        .requestMatchers("/api/account/**").authenticated()

                        // admin/user route backend
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/staff/reports/**").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN", "STAFF")

                        // nếu có API public khác thì khai báo rõ từng nhóm ở đây
                        // ví dụ:
                        // .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        // .requestMatchers(HttpMethod.GET, "/api/brands/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}