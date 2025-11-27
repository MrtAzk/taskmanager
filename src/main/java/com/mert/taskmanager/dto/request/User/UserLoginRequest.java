package com.mert.taskmanager.dto.request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    @NotEmpty(message = "Email alanı boş bırakılamaz.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    private String email;

    @NotEmpty(message = "Şifre alanı boş bırakılamaz.")
    private String password;
}
