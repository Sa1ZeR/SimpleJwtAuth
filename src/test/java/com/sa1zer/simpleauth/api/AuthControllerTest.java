package com.sa1zer.simpleauth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sa1zer.simpleauth.domain.User;
import com.sa1zer.simpleauth.domain.UserRole;
import com.sa1zer.simpleauth.payload.request.AuthRequest;
import com.sa1zer.simpleauth.payload.request.SignupRequest;
import com.sa1zer.simpleauth.payload.response.AuthResponse;
import com.sa1zer.simpleauth.repo.UserRepo;
import com.sa1zer.simpleauth.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void beforeAll() {
        userRepo.deleteAll();
    }

    @Test
    void auth() throws Exception {
        var user = userRepo.save(User.builder()
                .email("test@mail.ru")
                .login("Sa1ZeR_")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());

        userRepo.save(user);

        AuthRequest authRequest = new AuthRequest("Sa1ZeR_", "123456");

        var responseJson = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authRequest)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        AuthResponse authResponse = mapper.readValue(responseJson, AuthResponse.class);

        assertTrue(!authResponse.token().isEmpty() && authResponse.token().startsWith(JwtService.TOKEN_PREFIX));
    }

    @Test
    void signup() throws Exception {
        SignupRequest request = new SignupRequest("Sa1ZeR_", "test@mail.ru", "123456");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
}