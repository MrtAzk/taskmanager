package com.mert.taskmanager.service.abstracts;

import com.mert.taskmanager.dto.request.User.UserLoginRequest;
import com.mert.taskmanager.dto.request.User.UserSignupRequest;
import com.mert.taskmanager.dto.response.UserAuthResponse;
import com.mert.taskmanager.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    UserAuthResponse signup(UserSignupRequest signupRequest);
    UserAuthResponse login(UserLoginRequest loginRequest, HttpServletResponse response);
    UserAuthResponse getCurrentUser(HttpServletRequest request);
}
