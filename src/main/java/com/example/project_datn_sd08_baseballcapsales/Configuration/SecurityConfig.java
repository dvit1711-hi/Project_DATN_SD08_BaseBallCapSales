package com.example.project_datn_sd08_baseballcapsales.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {

        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

        // Query lấy user từ bảng Accounts
        manager.setUsersByUsernameQuery(
                "SELECT username AS username, password AS password, 1 AS enabled " +
                        "FROM Accounts WHERE username = ?"
        );

        // Query lấy role từ bảng Roles + AccountRoles
        manager.setAuthoritiesByUsernameQuery(
                "SELECT a.username AS username, r.roleName AS authority " +
                        "FROM Accounts a " +
                        "JOIN AccountRoles ar ON a.accountID = ar.accountID " +
                        "JOIN Roles r ON ar.roleID = r.roleID " +
                        "WHERE a.username = ?"
        );

        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors ->cors.configurationSource(corsConfigurationSource));

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/api/**", "/css/**").permitAll();
            auth.requestMatchers("/admin/**").hasRole("ADMIN");
            auth.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN");
            auth.anyRequest().permitAll();
        });

        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
        );
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        );
        http.rememberMe(config ->{
            config.tokenValiditySeconds(3*24*60*60);
        });
        return http.build();
    }
}


