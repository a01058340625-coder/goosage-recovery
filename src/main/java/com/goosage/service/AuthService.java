package com.goosage.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.goosage.entity.User;
import com.goosage.exception.UnauthorizedException;
import com.goosage.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }
    public User mustFindById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
    }



    // ✅ 회원가입
    public User signup(String email, String password) {

        // (선택) 중복 이메일 방지
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            throw new RuntimeException("EMAIL_ALREADY_EXISTS");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));

        return userRepository.save(user);
    }

    // ✅ 로그인
 // AuthService.java

    public User login(String email, String password) {

        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }
        if (password == null || password.isBlank()) {
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }

        return user;
    }
}
