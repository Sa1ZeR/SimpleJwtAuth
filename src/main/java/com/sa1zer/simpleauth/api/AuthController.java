package com.sa1zer.simpleauth.api;

import com.sa1zer.simpleauth.payload.request.AuthRequest;
import com.sa1zer.simpleauth.payload.request.SignupRequest;
import com.sa1zer.simpleauth.payload.response.AuthResponse;
import com.sa1zer.simpleauth.payload.response.SimpleResponse;
import com.sa1zer.simpleauth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
public class AuthController {

    private final AuthService authService;

    @Operation(description = "Авторизация пользователя")
    @ApiResponse(description = "jwt токен, который необходим для дальнейших действий в системе")
    @PostMapping("signin")
    public AuthResponse auth(@RequestBody @ParameterObject AuthRequest request) {
        return authService.auth(request);
    }

    @Operation(description = "Регистрация пользователя")
    @PostMapping("signup")
    public SimpleResponse signup(@RequestBody @ParameterObject SignupRequest request) {
        return authService.signup(request);
    }
}
