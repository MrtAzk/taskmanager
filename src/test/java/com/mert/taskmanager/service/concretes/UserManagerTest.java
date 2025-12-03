package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.core.mapper.UserMapper;
import com.mert.taskmanager.dto.request.User.UserLoginRequest;
import com.mert.taskmanager.dto.request.User.UserSignupRequest;
import com.mert.taskmanager.dto.response.UserAuthResponse;
import com.mert.taskmanager.entity.User;
import com.mert.taskmanager.repository.UserRepo;
import com.mert.taskmanager.service.abstracts.IJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {
    private UserManager userManager;
    private   UserRepo userRepo;
    private  UserMapper userMapper;
    private  PasswordEncoder passwordEncoder;
    private  AuthenticationManager authenticationManager;
    private  IJwtService jwtService;

    @BeforeEach
    void setUp() {
        userRepo = Mockito.mock(UserRepo.class);
        userMapper = Mockito.mock(UserMapper.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtService = Mockito.mock(IJwtService.class);
        userManager = new UserManager(userRepo,userMapper,passwordEncoder,authenticationManager,jwtService);

    }
    @Test
    public  void signUp_WhenValidRequest_ShouldReturnUserAuthResponse() {

        UserSignupRequest  userSignupRequest = new UserSignupRequest();
        userSignupRequest.setName("name");
        userSignupRequest.setPassword("password");
        userSignupRequest.setEmail("email@email");


        User user = new User();
        user.setEmail(userSignupRequest.getEmail());
        user.setId(1L);
        user.setName(userSignupRequest.getName());
        user.setHashedPassword("hashedPassword");

        UserAuthResponse userAuthResponse = new UserAuthResponse();
        userAuthResponse.setEmail(user.getEmail());
        userAuthResponse.setUsername(user.getName());

        Mockito.when(userMapper.toEntity(userSignupRequest)).thenReturn(user);
        Mockito.when(userMapper.toAuthResponse(user)).thenReturn(userAuthResponse);
        Mockito.when(userRepo.save(user)).thenReturn(user);
        Mockito.when(passwordEncoder.encode(userSignupRequest.getPassword())).thenReturn("hashedPassword");

        UserAuthResponse response =userManager.signup(userSignupRequest);
        assertNotNull(response);
        assertEquals(userAuthResponse.getEmail(),response.getEmail());
        assertEquals(userAuthResponse.getUsername(),response.getUsername());

        Mockito.verify(userMapper).toEntity(userSignupRequest);
        Mockito.verify(userMapper).toAuthResponse(user);
        Mockito.verify(userRepo).save(user);
        Mockito.verify(passwordEncoder).encode(userSignupRequest.getPassword());

    }
    @Test
    public  void login_WhenValidRequest_ShouldReturnUserAuthResponse() {

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setPassword("password");
        userLoginRequest.setEmail("email@email");

        User user = new User();
        user.setEmail("email@email");
        user.setId(1L);
        user.setName("name");
        user.setHashedPassword("hashedPassword");

        String token ="FakeToken";


        UserAuthResponse userAuthResponse = new UserAuthResponse();
        userAuthResponse.setEmail(userLoginRequest.getEmail());
        userAuthResponse.setToken(token);
        //servis kodundaki getPrincipal() beklentisi User o yüzden ilk paraemtre user at direk
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,userLoginRequest.getPassword());

        Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
                .thenReturn(authentication);
        Mockito.when(jwtService.generateToken(user)).thenReturn(token);
        Mockito.when(userMapper.toAuthResponse(user)).thenReturn(userAuthResponse);

        UserAuthResponse response = userManager.login(userLoginRequest);
        assertNotNull(response);
        assertEquals(userAuthResponse.getEmail(),response.getEmail());
        assertEquals(userAuthResponse.getToken(),response.getToken());

        Mockito.verify(userMapper).toAuthResponse(user);
        Mockito.verify(jwtService).generateToken(user);

    }
}