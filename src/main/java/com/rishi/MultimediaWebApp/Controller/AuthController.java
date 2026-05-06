package com.rishi.MultimediaWebApp.Controller;

import com.rishi.MultimediaWebApp.ServiceImplementation.AuthService;
import com.rishi.MultimediaWebApp.Util.ApiResponse;
import com.rishi.MultimediaWebApp.dto.AuthResponse;
import com.rishi.MultimediaWebApp.dto.LoginRequest;
import com.rishi.MultimediaWebApp.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @RequestBody @Valid RegisterRequest request) {

        AuthResponse user = authService.register(request);

        ApiResponse<AuthResponse> response =
                new ApiResponse<>(201, "User registered successfully", user);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody @Valid LoginRequest request) {

        AuthResponse auth = authService.login(request);

        ApiResponse<AuthResponse> response =
                new ApiResponse<>(200, "Login successful", auth);

        return ResponseEntity.ok(response);
    }
}