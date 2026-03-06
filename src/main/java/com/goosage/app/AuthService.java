package com.goosage.app;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.goosage.domain.auth.UserPort;
import com.goosage.entity.User;
import com.goosage.support.web.UnauthorizedException;

@Service
public class AuthService {

    private final UserPort userPort;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserPort userPort, BCryptPasswordEncoder encoder) {
        this.userPort = userPort;
        this.encoder = encoder;
    }

    public User mustFindById(long userId) {
        return userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
    }

    public User signup(String email, String password) {
        Optional<User> existing = userPort.findByEmail(email);
        if (existing.isPresent()) {
            throw new RuntimeException("EMAIL_ALREADY_EXISTS");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));

        return userPort.save(user);
    }

    public User login(String email, String password) {

        System.out.println("[LOGIN] email=" + email + ", password=" + password);

        if (email == null || email.isBlank()) {
            System.out.println("[LOGIN] email blank");
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }
        if (password == null || password.isBlank()) {
            System.out.println("[LOGIN] password blank");
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }

        User user = userPort.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[LOGIN] user not found");
                    return new UnauthorizedException("INVALID_CREDENTIALS");
                });

        System.out.println("[LOGIN] hash=" + user.getPasswordHash());

        boolean ok = encoder.matches(password, user.getPasswordHash());
        System.out.println("[LOGIN] matches=" + ok);

        if (!ok) {
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }

        return user;
    }
}