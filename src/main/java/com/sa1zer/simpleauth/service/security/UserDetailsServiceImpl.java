package com.sa1zer.simpleauth.service.security;

import com.sa1zer.simpleauth.domain.User;
import com.sa1zer.simpleauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByLoginOrEmail(username, username);

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        return UserDetailsImpl.builder()
                .login(user.getLogin())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name())).toList())
                .build();
    }
}
