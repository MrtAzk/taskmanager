package com.mert.taskmanager.config;


import com.mert.taskmanager.service.abstracts.IJwtService;
import com.mert.taskmanager.service.abstracts.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

        // 💡 Token'ı tek bir yerde tutacak değişken.
        String jwt = null;
        final String userEmail;

        // 1. 🚀 ÖNCELİK: HttpOnly Çerezleri Kontrol Et (Güvenli Yol)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt-token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // 2. YEDEK KONTROL: Eğer çerezde yoksa, Authorization Header'a bak (Postman/Mobil için)
        if (jwt == null) {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Token'ı al ve boşlukları temizle.
                jwt = authHeader.substring(7).trim();
            }
        }

        // 3. Token bulunamazsa (ne çerezde ne de header'da), filtreden geç.
        // Bu, isteğin Controller'a ulaşmasını sağlar (SecurityConfig'deki permitAll() izin veriyorsa).
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- TOKEN BULUNDU, DOĞRULAMA BAŞLANGIÇ ---

        // 4. Email'i token'dan çek.
        userEmail = jwtService.extractUsername(jwt);

        // 5. Kullanıcı Context'te değilse ve Email çekilebildiyse devam et
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. UserDetails'i DB'den çek
            UserDetails userDetails = this.userService.loadUserByUsername(userEmail);

            // 7. Token'ı doğrula
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Token geçerliyse, kullanıcıyı Context'e yerleştir (Login yap)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Filtre zincirine devam et.
        filterChain.doFilter(request, response);
    }
}