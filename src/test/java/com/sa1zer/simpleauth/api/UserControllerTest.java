package com.sa1zer.simpleauth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sa1zer.simpleauth.domain.User;
import com.sa1zer.simpleauth.domain.UserRole;
import com.sa1zer.simpleauth.payload.dto.UserDto;
import com.sa1zer.simpleauth.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final Map<String, String> TOKENS = new HashMap<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
    }

    @Test
    @WithMockUser("Sa1ZeR_")
    void info() throws Exception {
        var user = userRepo.save(User.builder()
                .email("test@mail.ru")
                .login("Sa1ZeR_")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());

        userRepo.save(user);

        var contentAsString = mockMvc.perform(get("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        var userDto = MAPPER.readValue(contentAsString, UserDto.class);

        assertEquals("Sa1ZeR_", userDto.login());
    }

    @Test
    @WithMockUser(username = "Sa1ZeR_")
    void all_no_perm() throws Exception {
        var admin = userRepo.save(User.builder()
                .email("admin@mail.ru")
                .login("admin")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.ADMIN))
                .build());

        var user1 = userRepo.save(User.builder()
                .email("test@mail.ru")
                .login("Sa1ZeR_")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());

        var user2 = userRepo.save(User.builder()
                .email("test2@mail.ru")
                .login("Test2")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());

        var user3 = userRepo.save(User.builder()
                .email("test3@mail.ru")
                .login("Test3")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());

        userRepo.save(user1);
        userRepo.save(user2);
        userRepo.save(user3);
        userRepo.save(admin);

        mockMvc.perform(get("/api/users/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void all() throws Exception {
        var admin = userRepo.save(User.builder()
                .email("admin@mail.ru")
                .login("admin")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.ADMIN))
                .build());

        var user1 = userRepo.save(User.builder()
                .email("test@mail.ru")
                .login("Sa1ZeR_")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());

        var user2 = userRepo.save(User.builder()
                .email("test2@mail.ru")
                .login("Test2")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());

        var user3 = userRepo.save(User.builder()
                .email("test3@mail.ru")
                .login("Test3")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());

        userRepo.save(user1);
        userRepo.save(user2);
        userRepo.save(user3);
        userRepo.save(admin);


        var contentAsString = mockMvc.perform(get("/api/users/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        var userDtos = MAPPER.readValue(contentAsString, UserDto[].class);

        assertEquals(4, userDtos.length);
    }
}