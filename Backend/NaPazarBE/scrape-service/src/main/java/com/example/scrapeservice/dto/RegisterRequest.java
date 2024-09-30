package com.example.scrapeservice.dto;

import com.example.scrapeservice.model.Role;

public record RegisterRequest(
        String firstname,
        String lastname,
        String email,
        String password,
        Role role
) {}

