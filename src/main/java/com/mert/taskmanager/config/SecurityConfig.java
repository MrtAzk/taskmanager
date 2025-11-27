package com.mert.taskmanager.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private  final  JwtAuthFilter jwtAuthFilter;


    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;

    }

    // UserDetailsService ve PasswordEncoder'ı birleştirir.
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // 2. Setters: Bağımlılıkları manuel olarak set et (Bu, her iki Bean'i de kullanmanın tek yolu)
        // Uyarılar burada devam edecektir, ancak bu, kütüphane tasarımının bir sonucudur.
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);     // BCrypt şifreleyici
        return authProvider;
    }

    // 💡 ÖNEMLİ: HttpSecurity'e CORS konfigürasyonunu tanıtıyoruz.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,AuthenticationProvider authenticationProvider) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // ⚠️ KRİTİK DEĞİŞİKLİK: Yetkilendirme Kuralları
                .authorizeHttpRequests(auth -> auth
                        // 🚀 1. Login/Kayıt API'ları halka açık olmalı
                        // '/api/v1/auth/login' ve '/api/v1/users/signup' gibi
                        .requestMatchers("/v1/auth/**").permitAll()

                        // 2. Diğer tüm istekler kimlik doğrulaması gerektirir (Token ister)
                        .anyRequest().authenticated()
                )

                // 🚀 3. Oturum Yönetimi: JWT Stateless (durumsuz) olduğu için Session tutmayız
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Authentication Provider'ı set et
                .authenticationProvider(authenticationProvider)


                // 🚀 5. JWT Filtresini akışa ekle (Her request'ten önce token kontrolü yap)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //Bu, Login API'ında kullanıcı kimlik bilgilerini doğrulamak için kullanılacaktır.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}