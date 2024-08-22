package com.sa1zer.simpleauth.repo;

import com.sa1zer.simpleauth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String s);

    Optional<User> findByLogin(String s);

    Optional<User> findByLoginOrEmail(String login, String email);
}
