package com.mert.taskmanager.config;


import com.mert.taskmanager.service.abstracts.IJwtService;
import com.mert.taskmanager.service.abstracts.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Gelen HTTP isteklerini yakalar, Header'daki JWT'yi ayrıştırır ve kullanıcıyı doğrular.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    // Bu, senin UserDetailsService implementasyonunun arayüzü olmalıdır.
    private final IUserService userService;
    private static final List<String> PUBLIC_URLS = List.of("/v1/auth/login", "/v1/auth/signup");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        //Gelen URI'dan Context Path'i çıkararak sadece Controller yolunu al.yani api yolu işte
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        // Context Path'i çıkar
        String path = requestUri.substring(contextPath.length());



        // 1. JWT Kontrolü: Header yoksa veya "Bearer " ile başlamıyorsa devam et
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Token'ı al (7 karakter atla: "Bearer ")
        jwt = authHeader.substring(7);

        // 3. Email'i token'dan çek
        userEmail = jwtService.extractUsername(jwt);

        // 4. Kullanıcı Context'te değilse ve Email çekilebildiyse devam et
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Email ile UserDetails'i DB'den çek (loadUserByUsername)
            UserDetails userDetails = this.userService.loadUserByUsername(userEmail);

            // 6. Token'ı doğrula
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Token geçerliyse, kullanıcıyı Context'e yerleştir (Login yap)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // JWT kullandığımız için parola null bırakılır
                        userDetails.getAuthorities() // Kullanıcı rolleri/yetkileri
                );

                // İstek detaylarını token'a ekle
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Spring Security Context'ini güncelle: Kullanıcı artık kimlik doğrulandı!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Filtre zincirine devam et (İsteği hedefine ulaştır)
        filterChain.doFilter(request, response);
    }
}