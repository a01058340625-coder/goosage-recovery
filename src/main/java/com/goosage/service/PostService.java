package com.goosage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.goosage.common.NotFoundException;
import com.goosage.dto.PostCreateRequest;
import com.goosage.dto.PostResponse;
import com.goosage.entity.PostEntity;
import com.goosage.exception.ForbiddenException;
import com.goosage.repository.PostRepository;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;
import com.goosage.exception.ForbiddenException;

/**
 * ✅ 서비스는 "비즈니스 로직 + 변환"의 중심. - 컨트롤러는 얇게 두고, - 여기서 Entity -> DTO 변환까지 정리한다.
 *
 * ✅ GooSage 룰(오늘 고정) - 없는 리소스는 RuntimeException(500) 말고 NotFoundException(404)로
 * 통일 - 404 응답 JSON은 GlobalExceptionHandler(@RestControllerAdvice)가
 * ApiResponse.fail(...)로 내려준다
 */
@Service
public class PostService {

	private final PostRepository postRepository;

	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	public Page<PostResponse> findAll(int page, int size, String keyword) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

		Page<PostEntity> pageResult = (keyword == null || keyword.isBlank()) ? postRepository.findAll(pageable)
				: postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);

		return pageResult.map(PostResponse::from);
	}

	public List<PostResponse> findAll() {
		return postRepository.findAll().stream().map(PostResponse::from) // Entity -> DTO
				.collect(Collectors.toList());
	}

	public PostResponse findById(long id) {
		PostEntity e = postRepository.findById(id)
				// ✅ 없으면 404로 떨어지게(서비스 레벨에서 의미를 고정)
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
		e.setUserId(userId); // ✅ 핵심: 서버가 주입한 userId 저장

		PostEntity saved = postRepository.save(e);
		return PostResponse.from(saved);
	}

	public PostResponse update(long id, String title, String content) {
		PostEntity e = postRepository.findById(id)
				// ✅ update도 똑같이 404로 통일
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


}
