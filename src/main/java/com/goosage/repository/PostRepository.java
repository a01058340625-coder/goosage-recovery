package com.goosage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.goosage.entity.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable
    );
}
