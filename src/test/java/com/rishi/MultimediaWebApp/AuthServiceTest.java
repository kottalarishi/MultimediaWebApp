package com.rishi.MultimediaWebApp;

import com.rishi.MultimediaWebApp.Repository.UserRepository;
import com.rishi.MultimediaWebApp.ServiceImplementation.AuthService;
import com.rishi.MultimediaWebApp.dto.AuthResponse;
import com.rishi.MultimediaWebApp.dto.LoginRequest;
import com.rishi.MultimediaWebApp.dto.RegisterRequest;
import com.rishi.MultimediaWebApp.entity.User;
import com.rishi.MultimediaWebApp.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_success() {

        RegisterRequest request =
                new RegisterRequest("rahul", "rahul@gmail.com", "pass123");

        when(passwordEncoder.encode("pass123"))
                .thenReturn("encodedPassword");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("rahul", response.getName());
        assertEquals("rahul@gmail.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_success() {

        LoginRequest request =
                new LoginRequest(
                        "rishi@test.com",
                        "pass123"
                );

        User user = User.builder()
                .name("Rishi")
                .email("rishi@test.com")
                .password("encoded")
                .build();

        when(userRepository.findByEmail("rishi@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "pass123",
                "encoded"
        )).thenReturn(true);

        when(jwtUtil.generateToken("rishi@test.com"))
                .thenReturn("token123");

        AuthResponse response =
                authService.login(request);

        assertEquals(
                "token123",
                response.getToken()
        );
    }

    @Test
    void login_userNotFound_throwsException() {

        LoginRequest request =
                new LoginRequest(
                        "wrong@test.com",
                        "pass123"
                );

        when(userRepository.findByEmail("wrong@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );
    }
}