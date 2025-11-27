package com.mert.taskmanager.controller;

import com.mert.taskmanager.dto.request.User.UserLoginRequest;
import com.mert.taskmanager.dto.request.User.UserSignupRequest;
import com.mert.taskmanager.dto.response.UserAuthResponse;
import com.mert.taskmanager.service.abstracts.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

        // ... AuthenticationManager, jwtService, userService final alanları buraya gelecek ...
        private final IUserService userService;


        public AuthController(IUserService userService) {
            this.userService = userService;
        }

        @PostMapping("/signup")
       // Başarılı kayıtta 201 Created döndür
        public ResponseEntity<UserAuthResponse> signup(@Valid @RequestBody UserSignupRequest signupRequest) {

            // 1. Kullanıcıyı kaydet (Şifre hashleme Servis'te yapıldı)
            UserAuthResponse response = userService.signup(signupRequest);

            // 2. Başarılı kayıt sonrası Token Üretimine gerek yok. Sadece 201 Created dönebiliriz.
            // VEYA: Kayıt sonrası direkt login olmasını istiyorsak, burada token üretebiliriz.
            // Şimdilik sadece başarılı yanıtı döndürelim (Token alanı boş gelir).

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        UserAuthResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    }

