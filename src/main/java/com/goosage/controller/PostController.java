package com.goosage.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.goosage.dto.PostCreateRequest;
import com.goosage.dto.PostUpdateRequest;
import com.goosage.dto.PostResponse;
import com.goosage.service.PostService;

/**
 * ✅ 컨트롤러는 "HTTP 입출력"만 담당
 * - Request DTO 받고
 * - Response DTO 반환
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostResponse> findAll() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public PostResponse findOne(@PathVariable long id) {
        return postService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@RequestBody PostCreateRequest req) {
        return postService.create(req.getTitle(), req.getContent());
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable long id, @RequestBody PostUpdateRequest req) {
        return postService.update(id, req.getTitle(), req.getContent());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        postService.delete(id);
    }
}
