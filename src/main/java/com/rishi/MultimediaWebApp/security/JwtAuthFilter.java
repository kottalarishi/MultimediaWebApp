package com.rishi.MultimediaWebApp.security;

import com.rishi.MultimediaWebApp.Repository.UserRepository;
import com.rishi.MultimediaWebApp.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter  extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");


        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {

                if (jwtUtil.isValid(token)) {

                    String email = jwtUtil.extractEmail(token);

                    User user = userRepository.findByEmail(email).orElse(null);

                    if (user != null) {

                        request.setAttribute("user", user);
                    }
                }

            } catch (Exception e) {
                // Optional: log error
                // log.warn("Invalid JWT: {}", e.getMessage());
            }
        }


        filterChain.doFilter(request, response);
    }
}
