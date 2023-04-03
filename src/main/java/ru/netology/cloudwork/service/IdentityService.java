package ru.netology.cloudwork.service;

import org.springframework.stereotype.Service;

@Service
public class IdentityService {

    public String generateTokenFor(String usernameRequested) {
        return "right_token";
    }
}
