package com.wootecobook.turkey.post.controller;

import com.wootecobook.turkey.commons.resolver.LoginUser;
import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.good.service.dto.GoodResponse;
import com.wootecobook.turkey.post.service.PostService;
import com.wootecobook.turkey.post.service.dto.PostRequest;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PostResponse> create(@Valid PostRequest postRequest,
                                               @LoginUser UserSession userSession) {
        PostResponse postResponse = postService.save(postRequest, userSession.getId());
        return ResponseEntity.created(null).body(postResponse);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> list(@PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                   @LoginUser UserSession userSession) {
        Page<PostResponse> postResponses = postService.findPostResponses(pageable, userSession.getId());
        return ResponseEntity.ok(postResponses);
    }

    @GetMapping("/{id}/mypage")
    public ResponseEntity<Page<PostResponse>> myPageList(@PathVariable Long id, @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponse> postResponses = postService.findPostResponses(pageable, id);
        return ResponseEntity.ok(postResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable Long id,
                                               @RequestBody @Valid PostRequest postRequest,
                                               @LoginUser UserSession userSession) {
        PostResponse postResponse = postService.update(postRequest, id, userSession.getId());

        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id, @LoginUser UserSession userSession) {
        postService.delete(id, userSession.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/good")
    public ResponseEntity toggleGood(@PathVariable Long id, @LoginUser UserSession userSession) {
        GoodResponse goodResponse = postService.toggleGood(id, userSession.getId());
        return ResponseEntity.ok(goodResponse);
    }

    @GetMapping("/{postId}/good/count")
    public ResponseEntity countGood(@PathVariable Long postId, @LoginUser UserSession userSession) {
        GoodResponse goodResponse = postService.countPostGoodByPost(postId, userSession.getId());
        return ResponseEntity.ok(goodResponse);
    }
}
