package com.sa1zer.simpleauth.facade;

import com.sa1zer.simpleauth.domain.User;
import com.sa1zer.simpleauth.payload.dto.UserDto;
import com.sa1zer.simpleauth.payload.mapper.UserMapper;
import com.sa1zer.simpleauth.payload.request.EditUserRequest;
import com.sa1zer.simpleauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto user(Principal principal) {
        var user = userService.findByLoginOrEmail(principal.getName(), principal.getName());

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        var users = userService.findAll();

        return users.stream().map(userMapper::toDto).toList();
    }

    @Transactional()
    public UserDto editUser(EditUserRequest request) {
        var user = userService.findById(request.id());

        if(!ObjectUtils.isEmpty(request.email()))
            user.setEmail(request.email());
        if(!ObjectUtils.isEmpty(request.password()))
            user.setPassword(passwordEncoder.encode(request.password()));
        if(!ObjectUtils.isEmpty(request.login()))
            user.setLogin(request.login());
        if(!ObjectUtils.isEmpty(request.roles()))
            user.setRoles(new HashSet<>(request.roles()));

        user = userService.save(user);

        return userMapper.toDto(user);
    }
}
