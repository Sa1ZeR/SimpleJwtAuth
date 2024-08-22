package com.sa1zer.simpleauth.service;

import com.sa1zer.simpleauth.domain.User;
import com.sa1zer.simpleauth.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        return userRepo.findByLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("User with login %s not found", login)));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("User with email %s not found", email)));
    }

    @Transactional(readOnly = true)
    public User findByLoginOrEmail(String login, String email) {
        return userRepo.findByLoginOrEmail(login, email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("User with login or email %s not found", email)));
    }

    @Transactional(readOnly = true)
    public Optional<User> getByLoginOrEmail(String login, String email) {
        return userRepo.findByLoginOrEmail(login, email);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("User with id %s not found", id)));
    }

    @Transactional()
    public User save(User user) {
        return userRepo.save(user);
    }
}
