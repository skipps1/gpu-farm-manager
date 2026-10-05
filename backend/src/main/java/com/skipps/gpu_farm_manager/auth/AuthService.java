package com.skipps.gpu_farm_manager.auth;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skipps.gpu_farm_manager.auth.dto.AuthResponse;
import com.skipps.gpu_farm_manager.auth.dto.LoginRequest;
import com.skipps.gpu_farm_manager.auth.dto.RegisterRequest;
import com.skipps.gpu_farm_manager.exception.UserAlreadyExistsException;
import com.skipps.gpu_farm_manager.user.Role;
import com.skipps.gpu_farm_manager.user.UserModel;
import com.skipps.gpu_farm_manager.user.UserRepository;

@Service
public class AuthService
{
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthService(
        JwtService jwtService,
        PasswordEncoder passwordEncoder,
        UserRepository userRepository,
        AuthenticationManager authenticationManager
    )
    {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw UserAlreadyExistsException.withUsername(request.username());
        }

        UserModel user = UserModel.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(/*request.role() != null ? request.role() : */Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        LocalDateTime expiration = jwtService.extractExpirationAsLocalDateTime(token);

        return new AuthResponse(token, user.getUsername(), user.getRole().name(), expiration);
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticates against DaoAuthenticationProvider (checks hashed password in DB)
        // Throws BadCredentialsException automatically if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserModel user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        String token = jwtService.generateToken(user);
        LocalDateTime expiration = jwtService.extractExpirationAsLocalDateTime(token);

        return new AuthResponse(token, user.getUsername(), user.getRole().name(), expiration);
    }
}
