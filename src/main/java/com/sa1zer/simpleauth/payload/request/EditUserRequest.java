package com.sa1zer.simpleauth.payload.request;

import com.sa1zer.simpleauth.domain.UserRole;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

public record EditUserRequest(
        @Parameter(description = "ID пользователя", required = true) Long id,
        @Parameter(description = "Login пользователя") String login,
        @Parameter(description = "Email пользователя") String email,
        @Parameter(description = "Пароль пользователя") String password,
        @Parameter(description = "Список ролей пользователя") List<UserRole> roles) {
}
