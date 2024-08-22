package com.sa1zer.simpleauth.payload.request;

import io.swagger.v3.oas.annotations.Parameter;

public record SignupRequest(
        @Parameter(description = "Логин пользователя", required = true) String login,
        @Parameter(description = "Email пользователя", required = true) String email,
        @Parameter(description = "Пароль пользователя", required = true) String password
) {
}
