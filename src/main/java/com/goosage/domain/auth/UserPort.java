package com.goosage.domain.auth;

import java.util.Optional;

import com.goosage.entity.User;

public interface UserPort {

    Optional<User> findById(long id);   // ⭐ 이 줄 추가

    Optional<User> findByEmail(String email);

    User save(User user);
}
