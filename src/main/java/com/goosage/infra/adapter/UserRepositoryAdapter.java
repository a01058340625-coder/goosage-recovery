package com.goosage.infra.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.goosage.domain.auth.UserPort;
import com.goosage.entity.User;
import com.goosage.infra.repository.UserRepository;

@Component
public class UserRepositoryAdapter implements UserPort {

    private final UserRepository userRepository;

    public UserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
