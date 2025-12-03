package com.mert.taskmanager.controller;

import com.mert.taskmanager.dto.request.User.UserLoginRequest;
import com.mert.taskmanager.dto.request.User.UserSignupRequest;
import com.mert.taskmanager.dto.response.UserAuthResponse;
import com.mert.taskmanager.service.abstracts.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserAuthResponse> login(@Valid @RequestBody UserLoginRequest loginRequest, HttpServletResponse response) {
        UserAuthResponse responseBody = userService.login(loginRequest,response);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/me")
    public ResponseEntity<UserAuthResponse> getCurrentUser(HttpServletRequest request) {
        UserAuthResponse userAuthResponse = userService.getCurrentUser(request);
        return ResponseEntity.ok(userAuthResponse);
    }

}

