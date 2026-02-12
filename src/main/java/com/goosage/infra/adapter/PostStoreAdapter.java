package com.goosage.infra.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.goosage.domain.post.PostStorePort;
import com.goosage.dto.post.PostDto;
import com.goosage.entity.PostEntity;
import com.goosage.infra.repository.PostRepository;

@Component
public class PostStoreAdapter implements PostStorePort {

    private final PostRepository postRepository;

    public PostStoreAdapter(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Optional<PostDto> findById(long id) {
        return postRepository.findById(id).map(PostMapper::toDto);
    }

    @Override
    public List<PostDto> findAll() {
        return postRepository.findAll().stream().map(PostMapper::toDto).toList();
    }

    @Override
    public Page<PostDto> findAll(int page, int size, String keyword) {
        // ✅ 잠금버전(리포지토리에 paging/search 없을 때): 메모리 필터링
        List<PostDto> all = findAll();

        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        if (!k.isEmpty()) {
            all = all.stream()
                    .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase().contains(k))
                              || (p.getContent() != null && p.getContent().toLowerCase().contains(k)))
                    .toList();
        }

        int safeSize = Math.max(1, size);
        int safePage = Math.max(0, page);
        int from = safePage * safeSize;
        int to = Math.min(all.size(), from + safeSize);

        List<PostDto> content = (from >= all.size()) ? List.of() : all.subList(from, to);

        return new PageImpl<>(content, PageRequest.of(safePage, safeSize), all.size());
    }

    @Override
    public PostDto save(PostDto dto, Long userId) {
        PostEntity e = PostMapper.toEntity(dto);
        e.setUserId(userId);

        PostEntity saved = postRepository.save(e);
        return PostMapper.toDto(saved);
    }

    @Override
    public void deleteById(long id) {
        postRepository.deleteById(id);
    }
}
