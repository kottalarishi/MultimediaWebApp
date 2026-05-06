package com.rishi.MultimediaWebApp.ServiceImplementation;

import com.rishi.MultimediaWebApp.Repository.UserRepository;
import com.rishi.MultimediaWebApp.ServiceInterface.AuthInterface;
import com.rishi.MultimediaWebApp.dto.AuthResponse;
import com.rishi.MultimediaWebApp.dto.LoginRequest;
import com.rishi.MultimediaWebApp.dto.RegisterRequest;
import com.rishi.MultimediaWebApp.entity.User;
import com.rishi.MultimediaWebApp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService implements AuthInterface {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .token(null)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
