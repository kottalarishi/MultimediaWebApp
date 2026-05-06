package com.rishi.MultimediaWebApp.ServiceInterface;

import com.rishi.MultimediaWebApp.dto.AuthResponse;
import com.rishi.MultimediaWebApp.dto.LoginRequest;
import com.rishi.MultimediaWebApp.dto.RegisterRequest;

public interface AuthInterface {

    AuthResponse register(RegisterRequest request);


    AuthResponse login(LoginRequest request);
}
