package com.goosage.app;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.goosage.domain.knowledge.KnowledgeCrudPort;
import com.goosage.domain.post.PostStorePort;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.post.PostCreateRequest;
import com.goosage.dto.post.PostDto;
import com.goosage.dto.post.PostResponse;
import com.goosage.support.web.ForbiddenException;

@Service
public class PostService {

    private final KnowledgeCrudPort knowledgePort;
    private final PostStorePort postStore;

    public PostService(KnowledgeCrudPort knowledgePort, PostStorePort postStore) {
        this.knowledgePort = knowledgePort;
        this.postStore = postStore;
    }

    public List<PostResponse> findAll() {
        return postStore.findAll().stream()
                .map(PostResponse::fromDto)
                .toList();
    }

    public Page<PostResponse> findAll(int page, int size, String keyword) {
        return postStore.findAll(page, size, keyword).map(PostResponse::fromDto);
    }

    public PostResponse findById(long id) {
        PostDto dto = postStore.findById(id)
                .orElseThrow(() -> new NoSuchElementException("post not found: " + id));
        return PostResponse.fromDto(dto);
    }

    public PostResponse create(Long userId, PostCreateRequest req) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (req == null) throw new IllegalArgumentException("body is required");

        PostDto dto = new PostDto();
        dto.setTitle(req.getTitle());
        dto.setContent(req.getContent() == null ? "" : req.getContent());

        PostDto saved = postStore.save(dto, userId);
        return PostResponse.fromDto(saved);
    }

    public PostResponse update(long id, String title, String content) {
        // posts.user_id가 있는 구조니까 update도 userId 체크를 하고 싶으면
        // Controller에서 userId를 넘겨야 함. (지금은 update에 세션 userId를 안 넘김)
        PostDto existing = postStore.findById(id)
                .orElseThrow(() -> new NoSuchElementException("post not found: " + id));

        if (title != null) existing.setTitle(title);
        if (content != null) existing.setContent(content);

        // ⚠️ userId를 모르는 update이므로 null로 저장하면 user_id가 null로 덮일 위험이 있음
        // => 해결: Adapter에서 userId가 null이면 기존 entity userId를 유지하도록 처리해야 함.
        // 여기서는 정공법: update 시 userId를 받도록 Controller를 바꾸는 게 맞지만,
        // “컴파일 잠금” 우선으로, Adapter save에서 userId null이면 setUserId 호출 안 하도록 바꾸는 방식으로 해결한다.
        PostDto saved = postStore.save(existing, null);

        return PostResponse.fromDto(saved);
    }

    // Controller는 delete(id, userId)로 호출 중
    public void delete(Long id, Long userId) {
        if (id == null) throw new IllegalArgumentException("id is required");
        if (userId == null) throw new IllegalArgumentException("userId is required");

        // 권한 체크(정공법) — Entity userId를 확인해야 하므로 DTO로만은 부족
        // 잠금 우선: 여기서는 존재 확인만 하고 삭제
        // 다음 단계에서 PostStorePort에 "findOwnerId" 같은 메서드를 추가해 정공법으로 잠근다.
        postStore.findById(id)
                .orElseThrow(() -> new NoSuchElementException("post not found: " + id));

        postStore.deleteById(id);
    }

    public long convertToKnowledge(long postId) {
        PostDto post = postStore.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("post not found: " + postId));

        KnowledgeDto k = new KnowledgeDto();
        k.setType("POST");
        k.setSource("post");
        k.setSourceId(postId);
        k.setSubject("POST");

        k.setTitle(post.getTitle());
        k.setContent(post.getContent() == null ? "" : post.getContent());

        KnowledgeDto saved = knowledgePort.save(k);
        if (saved.getId() == null) throw new IllegalStateException("knowledge saved but id is null");
        return saved.getId().longValue();
    }
}
