package com.goosage.domain.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.goosage.dto.post.PostDto;

public interface PostStorePort {

    Optional<PostDto> findById(long id);

    List<PostDto> findAll();

    Page<PostDto> findAll(int page, int size, String keyword);

    PostDto save(PostDto dto, Long userId);

    void deleteById(long id);
}
