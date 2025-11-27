package com.mert.taskmanager.service.abstracts;

import com.mert.taskmanager.dto.request.User.UserLoginRequest;
import com.mert.taskmanager.dto.request.User.UserSignupRequest;
import com.mert.taskmanager.dto.response.UserAuthResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    UserAuthResponse signup(UserSignupRequest signupRequest);
    UserAuthResponse login(UserLoginRequest loginRequest);
}
