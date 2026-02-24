package com.example.project_datn_sd08_baseballcapsales.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/api/**", "/css/**").permitAll();
            auth.requestMatchers("/admin/**").hasRole("ADMIN");
            auth.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN");
            auth.anyRequest().permitAll();
        });

//        http.formLogin(Customizer.withDefaults());
//
//        http.rememberMe(r -> r
//                .tokenValiditySeconds(5 * 24 * 60 * 60)
//                .userDetailsService(userDetailsService)
//        );
//
        return http.build();
    }
}


