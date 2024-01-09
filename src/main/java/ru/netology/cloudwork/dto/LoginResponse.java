package ru.netology.cloudwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A transfer object for bringing the generated auth-token from server to client.
 */
public record LoginResponse(@JsonProperty("auth-token") String token) {}
