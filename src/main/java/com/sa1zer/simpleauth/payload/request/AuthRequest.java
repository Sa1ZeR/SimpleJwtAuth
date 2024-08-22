package com.sa1zer.simpleauth.payload.request;

import io.swagger.v3.oas.annotations.Parameter;

public record AuthRequest(
        @Parameter(description = "Login/Email пользователя", required = true) String login,
        @Parameter(description = "Пароль пользователя", required = true) String password) {
}
