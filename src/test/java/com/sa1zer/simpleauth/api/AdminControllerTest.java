package com.sa1zer.simpleauth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sa1zer.simpleauth.domain.User;
import com.sa1zer.simpleauth.domain.UserRole;
import com.sa1zer.simpleauth.payload.dto.UserDto;
import com.sa1zer.simpleauth.payload.request.EditUserRequest;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
    }

    @Test
    @WithMockUser(username = "USER", authorities = "USER")
    void editUser_no_perm() throws Exception {
        var user = userRepo.save(User.builder()
                .email("test@mail.ru")
                .login("Sa1ZeR_")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.ADMIN))
                .build());
        var test = userRepo.save(User.builder()
                .email("test1@mail.ru")
                .login("test")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());


        userRepo.save(user);
        userRepo.save(test);

        EditUserRequest request = new EditUserRequest(2L, "newTest", null, null,
                List.of(UserRole.USER, UserRole.ADMIN));

        var responseJson = mockMvc.perform(patch("/api/admin/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "Sa1ZeR_", authorities = "ADMIN")
    void editUser_not_found() throws Exception {
        var user = userRepo.save(User.builder()
                .email("test@mail.ru")
                .login("Sa1ZeR_")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.ADMIN))
                .build());
        var test = userRepo.save(User.builder()
                .email("test1@mail.ru")
                .login("test")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());


        userRepo.save(user);
        var user1 = userRepo.save(test);

        EditUserRequest request = new EditUserRequest(0L, "newTest", null, null,
                List.of(UserRole.USER, UserRole.ADMIN));

        var responseJson = mockMvc.perform(patch("/api/admin/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @WithMockUser(username = "Sa1ZeR_", authorities = "ADMIN")
    void editUser() throws Exception {
        var user = userRepo.save(User.builder()
                .email("test@mail.ru")
                .login("Sa1ZeR_")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.ADMIN))
                .build());
        var test = userRepo.save(User.builder()
                .email("test1@mail.ru")
                .login("test")
                .password(passwordEncoder.encode("123456"))
                .roles(Collections.singleton(UserRole.USER))
                .build());


        userRepo.save(user);
        var user1 = userRepo.save(test);

        EditUserRequest request = new EditUserRequest(user1.getId(), "newTest", null, null,
                List.of(UserRole.USER, UserRole.ADMIN));

        var responseJson = mockMvc.perform(patch("/api/admin/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        var userDto = mapper.readValue(responseJson, UserDto.class);

        assertAll(() -> {
            assertEquals("newTest", userDto.login());
            assertEquals(2, userDto.roles().size());
        });
    }
}