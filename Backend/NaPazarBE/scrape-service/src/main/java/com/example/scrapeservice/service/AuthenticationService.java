package com.example.scrapeservice.service;

import com.example.scrapeservice.dto.AuthenticationRequest;
import com.example.scrapeservice.dto.AuthenticationResponse;
import com.example.scrapeservice.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void refreshToken(HttpServletRequest request,
                      HttpServletResponse response) throws IOException;
    void forgotPassword(String email);
    void resetPassword(String email, String password);
}
