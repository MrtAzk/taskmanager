package com.mert.taskmanager.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Spring Konfigürasyon sınıfı olduğunu belirtir
public class SecurityConfig {

    @Bean // Bean olarak tanımlanır
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Cross-Site Request Forgery korumasını kapat
                .authorizeHttpRequests(auth -> auth
                        // 🔥 TÜM URL'lere (/**) izin ver (permitAll) 🔥
                        .requestMatchers("/**").permitAll()
                        // Bu, /v1/projects, /v1/tasks vb. tüm yolları kapsar
                        .anyRequest().authenticated() // Diğer tüm istekler için kimlik doğrulaması iste
                );

        return http.build();
    }
}