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

    public AuthService(UserPort userPort) {
        this.userPort = userPort;
        this.encoder = new BCryptPasswordEncoder();
    }

    public User mustFindById(long userId) {
        return userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
    }

    // ✅ 회원가입
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

    // ✅ 로그인
    public User login(String email, String password) {

        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }
        if (password == null || password.isBlank()) {
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }

        User user = userPort.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }

        return user;
    }
}
