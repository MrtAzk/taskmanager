package com.mert.taskmanager.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 💡 ÖNEMLİ: HttpSecurity'e CORS konfigürasyonunu tanıtıyoruz.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CORS ayarların kalsın (bu zaten olması gereken)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF'yi kapattık (JWT/React için uygun)
                .csrf(csrf -> csrf.disable())

                // ⚠️ KRİTİK DEĞİŞİKLİK BURADA!
                .authorizeHttpRequests(auth -> auth
                        // Tüm URL'lere ve Tüm Metotlara kimlik doğrulaması OLMADAN izin ver.
                        .anyRequest().permitAll()
                )
        // Session yönetimini de statik yapabilirsin, JWT kullanıyorsan:
        // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;

        return http.build();
    }

    // 💡 CorsConfigurationSource Bean'ini tanımla.
    // Bu, HTTP güvenlik katmanında kullanılacak olan **gerçek** CORS kurallarını sağlar.
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. İzin Verilen Kökenler (Aynı yukarıdaki gibi front-end adresin)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 2. İzin Verilen Metotlar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Kimlik Bilgilerine İzin Ver
        configuration.setAllowCredentials(true);

        // 4. İzin Verilen Başlıklar
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Tüm yollara ("/**") bu konfigürasyonu uygula
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}