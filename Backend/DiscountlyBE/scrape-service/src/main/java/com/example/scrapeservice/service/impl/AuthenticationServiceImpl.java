package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.dto.AuthenticationRequest;
import com.example.scrapeservice.dto.AuthenticationResponse;
import com.example.scrapeservice.dto.RegisterRequest;
import com.example.scrapeservice.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        return null;
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}
