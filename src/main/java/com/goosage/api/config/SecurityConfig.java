package com.goosage.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/internal/**").permitAll()
                        .requestMatchers("/admin/kpi/**").permitAll()   // ✅ 지금 네 목표
                        .anyRequest().authenticated()
                )
                // 원하면 켜도 되고, 꺼도 됨. (permitAll이면 영향 거의 없음)
                .httpBasic(b -> {})
                .build();
    }
}