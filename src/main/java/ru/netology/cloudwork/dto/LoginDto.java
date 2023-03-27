package ru.netology.cloudwork.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A transfer object for bringing the generated auth-token from server to client.
 * @param token a token assigned by the server to this client's session.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Getter
@Data
public record LoginDto(@JsonProperty("auth-token") String token) {
}
