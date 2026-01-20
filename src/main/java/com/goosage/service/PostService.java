package com.goosage.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.goosage.common.NotFoundException;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.PostCreateRequest;
import com.goosage.dto.PostResponse;
import com.goosage.entity.PostEntity;
import com.goosage.exception.ForbiddenException;
import com.goosage.repository.KnowledgeRepository;
import com.goosage.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final KnowledgeRepository knowledgeRepository;

    public PostService(PostRepository postRepository, KnowledgeRepository knowledgeRepository) {
        this.postRepository = postRepository;
        this.knowledgeRepository = knowledgeRepository;
    }

    public Page<PostResponse> findAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<PostEntity> pageResult = (keyword == null || keyword.isBlank())
                ? postRepository.findAll(pageable)
                : postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);

        return pageResult.map(PostResponse::from);
    }

    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }

    public PostResponse findById(long id) {
        PostEntity e = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("post not found: id=" + id));
        return PostResponse.from(e);
    }

    public PostResponse create(String title, String content) {
        PostEntity e = new PostEntity();
        e.setTitle(title);
        e.setContent(content);

        PostEntity saved = postRepository.save(e);
        return PostResponse.from(saved);
    }

    public PostResponse create(Long userId, PostCreateRequest req) {
        PostEntity e = new PostEntity();
        e.setTitle(req.getTitle());
        e.setContent(req.getContent());
        e.setUserId(userId);

        PostEntity saved = postRepository.save(e);
        return PostResponse.from(saved);
    }

    public PostResponse update(long id, String title, String content) {
        PostEntity e = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("post not found: id=" + id));

        e.setTitle(title);
        e.setContent(content);

        PostEntity saved = postRepository.save(e);
        return PostResponse.from(saved);
    }

    public void delete(Long postId, Long userId) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        if (!post.getUserId().equals(userId)) {
            throw new ForbiddenException("FORBIDDEN");
        }

        postRepository.deleteById(postId);
    }

    /**
     * posts -> knowledge 변환 (중복 방지: source/sourceId)
     */
    public long convertToKnowledge(long postId) {

        // 1) post 존재 확인
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND: id=" + postId));

        // 2) 이미 변환된 적 있으면 재사용
        Optional<KnowledgeDto> existing = knowledgeRepository.findBySourceAndSourceId("POST", postId);
        if (existing.isPresent() && existing.get().getId() != null) {
            return existing.get().getId();
        }

        // 3) knowledge 생성 후 저장
        KnowledgeDto k = new KnowledgeDto();
        k.setSource("POST");
        k.setSourceId(postId);
        k.setTitle(post.getTitle());
        k.setContent(post.getContent());
        // tags/createdAt 등은 KnowledgeDto/DB 스키마에 맞춰 다음 단계에서 확장

        KnowledgeDto saved = knowledgeRepository.save(k);

        if (saved.getId() == null) {
            throw new RuntimeException("KNOWLEDGE_SAVE_FAILED");
        }
        return saved.getId();
    }
}
