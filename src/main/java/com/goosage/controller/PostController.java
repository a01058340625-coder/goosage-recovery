package com.goosage.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.dto.PostCreateRequest;
import com.goosage.dto.PostResponse;
import com.goosage.dto.PostUpdateRequest;
import com.goosage.service.PostService;

import jakarta.servlet.http.HttpSession;

/**
 * ✅ 컨트롤러는 "HTTP 입출력"만 담당
 * - Request DTO 받고
 * - Response DTO 반환
 *
 * ✅ GooSage 룰(오늘 고정):
 * - 성공 응답은 ApiResponse.ok(...)로 감싸서 항상 같은 형태로 내려준다
 * - (에러 통일은 다음 단계에서 @RestControllerAdvice로 한 방에 정리)
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * ✅ 전체 목록 조회
     * GET /posts
     */
    @GetMapping
    public ApiResponse<List<PostResponse>> findAll() {
        List<PostResponse> posts = postService.findAll();
        return ApiResponse.ok(posts);
    }
    
    @GetMapping("/page")
    public ApiResponse<Page<PostResponse>> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        Page<PostResponse> result = postService.findAll(page, size, keyword);
        return ApiResponse.ok(result);
    }


    /**
     * ✅ 단건 조회
     * GET /posts/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<PostResponse> findOne(@PathVariable long id) {
        PostResponse post = postService.findById(id);
        return ApiResponse.ok(post);
    }

    /**
     * ✅ 생성
     * POST /posts
     *
     * - HTTP 상태코드는 201(CREATED) 유지
     * - 본문은 ApiResponse로 통일
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> create(@RequestBody PostCreateRequest req, HttpSession session) {

        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);

        PostResponse created = postService.create(userId, req);

        return ApiResponse.ok("CREATED", created);
    }

    /**
     * ✅ 수정
     * PUT /posts/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<PostResponse> update(@PathVariable long id, @RequestBody PostUpdateRequest req) {
        PostResponse updated = postService.update(id, req.getTitle(), req.getContent());
        return ApiResponse.ok("UPDATED", updated);
    }

    /**
     * ✅ 삭제
     * DELETE /posts/{id}
     *
     * 선택지 2개 중 하나를 골라야 함:
     * A) 204(NO_CONTENT) 유지 → 바디를 내려줄 수 없어서 ApiResponse 통일이 깨짐
     * B) 200(OK)로 바꾸고 ApiResponse로 통일 → 운영/프론트 입장에서는 이게 더 편함
     *
     * 👉 지금은 "응답 포맷 통일"이 목표니까 B로 간다.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        postService.delete(id, userId);
        return ApiResponse.ok("DELETED", null);
    }

}
