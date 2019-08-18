package com.wootecobook.turkey.post.controller;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.post.controller.exception.PostBadRequestException;
import com.wootecobook.turkey.post.service.PostService;
import com.wootecobook.turkey.post.service.dto.PostRequest;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostApiController {

    private final PostService postService;

    public PostApiController(final PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@RequestBody @Valid PostRequest postRequest,
                                               BindingResult bindingResult, UserSession userSession) {
        if (bindingResult.hasErrors()) {
            throw new PostBadRequestException();
        }

        PostResponse postResponse = postService.save(postRequest, userSession.getId());
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> list(@PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponse> postResponses = postService.findPostResponses(pageable);
        return ResponseEntity.ok(postResponses);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> update(@PathVariable Long postId, @RequestBody @Valid PostRequest postRequest,
                                               BindingResult bindingResult, UserSession userSession) {
        if (bindingResult.hasErrors()) {
            throw new PostBadRequestException();
        }

        PostResponse postResponse = postService.update(postRequest, postId, userSession.getId());

        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity delete(@PathVariable Long postId, UserSession userSession) {
        postService.delete(postId, userSession.getId());
        return ResponseEntity.noContent().build();
    }
}
