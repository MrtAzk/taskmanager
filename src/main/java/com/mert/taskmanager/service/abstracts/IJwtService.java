package com.mert.taskmanager.service.abstracts;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;

public interface IJwtService {
    String generateToken(UserDetails userDetails);
    Key getSigningKey();
    Claims extractAllClaims(String token);
    String extractUsername(String token);
    boolean isTokenExpired(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
