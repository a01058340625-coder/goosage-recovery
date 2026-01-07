package com.goosage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.goosage.entity.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
