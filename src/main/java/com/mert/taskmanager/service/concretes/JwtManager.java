package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.service.abstracts.IJwtService;
import io.jsonwebtoken.Claims;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class JwtManager implements IJwtService {

    @Value("${jwt.secret.key}")//Böyle yapınca altaki değişkene otamtik olaral applicatio.prop içindeki jwt.secret.key bu değişkende ne değer varsa buna atar
    private String SECRET_KEY;

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();



        // 1. 🚀 ZAMAN: Modern Java Instant API'ları kullanılıyor.
        Instant now = Instant.now();
        Instant expirationTime = now.plus(1, ChronoUnit.DAYS); // 1 gün geçerlilik

        return Jwts.builder()
                .subject(userDetails.getUsername())      // 1. Token'ın kime ait olduğunu söyler.Benim için email oluyor bu çünkü loadUserByUsername(String email)
                .issuedAt(Date.from(now))                // 2. Token'ın ne zaman üretildiğini söyler.
                .expiration(Date.from(expirationTime))   // 3. Token'ın ne zaman geçersiz olacağını söyler.
                .signWith(getSigningKey())               // 4. Token'ın güvenilirliğini sağlayan imzadır.
                .compact();                              // 5. Token'ı son String formatına dönüştürür.
    }



    @Override
    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Token'ın imzasını doğrular ve içindeki tüm verileri (Claims) çeker.//Eski methodlar ama yapcak bişey yok bende parserBuidler çıkmıyor
    @Override
    public Claims extractAllClaims(String token) {
        try {
            // 🚀 KORUNAN ZİNCİR: Senin projenin gerektirdiği tek API zinciri budur.
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build() // Build metodunu tutuyoruz (Senin ortamına özel)
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException ex) {
            // 1. Token süresi dolduğunda yakala
            throw new AuthenticationException("Token süresi dolmuştur. Lütfen tekrar giriş yapın.") {};

        } catch (SignatureException | MalformedJwtException ex) {
            // 2. İmza geçersizse veya token formatı bozuksa (Sahte Token)
            throw new AuthenticationException("Geçersiz veya bozuk JWT token'ı.", ex) {};

        } catch (AuthenticationException ex) {
            // 3. Zaten bir AuthenticationException fırlatılmışsa, onu tekrar fırlat.
            throw ex;

        } catch (Exception ex) {
            // 4. Diğer bilinmeyen hatalar (I/O, Parse hatası vb.)
            throw new RuntimeException("JWT işlenirken beklenmeyen hata oluştu.", ex);
        }
    }


    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public boolean isTokenExpired(String token) {
         return extractAllClaims(token).getExpiration().before(new Date());
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Token'daki email, UserDetails'teki ile eşleşmeli VE süresi dolmamış olmalıdır.
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
