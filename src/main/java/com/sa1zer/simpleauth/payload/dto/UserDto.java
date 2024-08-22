package com.sa1zer.simpleauth.payload.dto;

import com.sa1zer.simpleauth.domain.UserRole;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.sa1zer.simpleauth.domain.User}
 */
public record UserDto(Long id, String email, String login, Set<UserRole> roles) implements Serializable {
}