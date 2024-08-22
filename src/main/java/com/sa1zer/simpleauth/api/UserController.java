package com.sa1zer.simpleauth.api;

import com.sa1zer.simpleauth.facade.UserFacade;
import com.sa1zer.simpleauth.payload.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/")
public class UserController {

    private final UserFacade userFacade;

    @Operation(description = "Получение информации о себе")
    @GetMapping("")
    public UserDto info(Principal principal) {
        return userFacade.user(principal);
    }

    @Operation(description = "Список пользователей")
    @GetMapping("all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> all() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        return userFacade.findAllUsers();
    }
}
