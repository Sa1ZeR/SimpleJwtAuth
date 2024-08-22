package com.sa1zer.simpleauth.service;

import com.sa1zer.simpleauth.domain.User;
import com.sa1zer.simpleauth.domain.UserRole;
import com.sa1zer.simpleauth.payload.request.AuthRequest;
import com.sa1zer.simpleauth.payload.request.SignupRequest;
import com.sa1zer.simpleauth.payload.response.AuthResponse;
import com.sa1zer.simpleauth.payload.response.SimpleResponse;
import com.sa1zer.simpleauth.repo.UserRepo;
import com.sa1zer.simpleauth.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthResponse auth(AuthRequest authRequest) {
        var user = userRepo.findByLoginOrEmail(authRequest.login(), authRequest.login());
        if(user.isEmpty() || !passwordEncoder.matches(authRequest.password(), user.get().getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password or email");

        return new AuthResponse(jwtService.generateToken(user.get()));
    }

    @Transactional
    public SimpleResponse signup(SignupRequest request) {
        var user = userRepo.findByLoginOrEmail(request.login(), request.email());
        if(user.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");

        var newUser = User.builder()
                .login(request.login())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Collections.singleton(UserRole.USER))
                .build();

        userRepo.save(newUser);

        return new SimpleResponse("Successful registration");
    }
}
