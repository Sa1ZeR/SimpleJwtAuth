package com.sa1zer.simpleauth.api;

import com.sa1zer.simpleauth.facade.UserFacade;
import com.sa1zer.simpleauth.payload.dto.UserDto;
import com.sa1zer.simpleauth.payload.request.EditUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/admin/")
public class AdminController {

    private final UserFacade userFacade;

    @Operation(description = "Редактирование пользователя")
    @PatchMapping("edit")
    public UserDto editUser(@RequestBody EditUserRequest request) {
        return userFacade.editUser(request);
    }
}
