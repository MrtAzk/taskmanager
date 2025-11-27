package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.core.mapper.UserMapper;
import com.mert.taskmanager.dto.request.User.UserLoginRequest;
import com.mert.taskmanager.dto.request.User.UserSignupRequest;
import com.mert.taskmanager.dto.response.UserAuthResponse;
import com.mert.taskmanager.entity.User;
import com.mert.taskmanager.repository.UserRepo;
import com.mert.taskmanager.service.abstracts.IJwtService;
import com.mert.taskmanager.service.abstracts.IUserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserManager implements IUserService {

    private  final UserRepo userRepo;
    private final UserMapper userMapper;
    @Lazy
    private final PasswordEncoder passwordEncoder;
    @Lazy
    private final AuthenticationManager authenticationManager;
    @Lazy
    private final IJwtService jwtService;

    public UserManager(UserRepo userRepo, UserMapper userMapper, @Lazy PasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager, @Lazy IJwtService jwtService) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Kullanıcıyı email ile veritabanında ara.
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found : "+email));
    }

    @Override
    public UserAuthResponse signup(UserSignupRequest signupRequest) {
        User user = userMapper.toEntity(signupRequest);

       // Şifreyi Hash'le ve Entity'ye set et. (Güvenlik Kuralı)
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());
        user.setHashedPassword(hashedPassword);

        // 3. Diğer zorunlu alanları set et (User Entity'nde varsa)
        user.setCreatedDate(LocalDate.now());
        // User'da 'name' alanı varsa, onu da set etmelisin (Örn: email'den).
        user.setName(signupRequest.getName());

        userRepo.save(user);

        // 5. Yanıtı hazırla (Token hariç)
        // Token üretimi Login Controller'da yapılacağı için burada boş bir AuthResponse dönebiliriz.
        UserAuthResponse response = userMapper.toAuthResponse(user);

        return response;
    }
    @Override
    public UserAuthResponse login(UserLoginRequest loginRequest) {


        // 1. AuthenticationManager.authenticate() çağrısı buraya gelir.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. Token üretimi ve response hazırlama buraya taşınır.
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        UserAuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(token);

        return response;
    }
}
