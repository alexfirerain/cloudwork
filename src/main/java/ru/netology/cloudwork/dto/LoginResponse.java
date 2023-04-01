package ru.netology.cloudwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A transfer object for bringing the generated auth-token from server to client.
 */
@Data
public class LoginResponse {
    @JsonProperty("auth-token")
    private final String token;

    /**
     * @param token a token assigned by the server to this client's session.
     */
    public LoginResponse(@JsonProperty("auth-token") String token) {
        this.token = token;
    }

    @JsonProperty("auth-token")
    public String token() {
        return token;
    }

}
