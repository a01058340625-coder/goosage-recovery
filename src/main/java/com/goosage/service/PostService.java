package com.goosage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.goosage.dto.PostResponse;
import com.goosage.entity.PostEntity;
import com.goosage.repository.PostRepository;

/**
 * ✅ 서비스는 "비즈니스 로직 + 변환"의 중심.
 * 컨트롤러는 얇게 두고, 여기서 Entity -> DTO 변환까지 정리해준다.
 */
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostResponse> findAll() {
        return postRepository.findAll()
                .stream()
                .map(PostResponse::from) // Entity -> DTO
                .collect(Collectors.toList());
    }

    public PostResponse findById(long id) {
        PostEntity e = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("post not found: id=" + id));
        return PostResponse.from(e);
    }

    public PostResponse create(String title, String content) {
        PostEntity e = new PostEntity();
        e.setTitle(title);
        e.setContent(content);

        PostEntity saved = postRepository.save(e);
        return PostResponse.from(saved);
    }

    public PostResponse update(long id, String title, String content) {
        PostEntity e = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("post not found: id=" + id));

        e.setTitle(title);
        e.setContent(content);

        PostEntity saved = postRepository.save(e);
        return PostResponse.from(saved);
    }

    public void delete(long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("post not found: id=" + id);
        }
        postRepository.deleteById(id);
    }
}
