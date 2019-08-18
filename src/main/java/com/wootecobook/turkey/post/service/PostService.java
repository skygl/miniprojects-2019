package com.wootecobook.turkey.post.service;

import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.post.domain.PostRepository;
import com.wootecobook.turkey.post.service.dto.PostRequest;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import com.wootecobook.turkey.post.service.exception.NotFoundPostException;
import com.wootecobook.turkey.post.service.exception.NotPostOwnerException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(final PostRepository postRepository, final UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public PostResponse save(PostRequest postRequest, Long userId) {
        User user = userService.findById(userId);
        Post savedPost = postRepository.save(postRequest.toEntity(user));

        return PostResponse.from(savedPost);
    }

    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(NotFoundPostException::new);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> findPostResponses(final Pageable pageable) {
        return postRepository.findAll(pageable).map(PostResponse::from);
    }

    public PostResponse update(PostRequest postRequest, Long postId, Long userId) {
        Post post = findById(postId);

        if (post.isWrittenBy(userId)) {
            Post updatePost = Post.builder()
                    .id(postId)
                    .contents(new Contents(postRequest.getContents()))
                    .build();

            post.update(updatePost);
            return PostResponse.from(post);
        }

        throw new NotPostOwnerException();
    }

    public void delete(Long postId, Long userId) {
        Post post = findById(postId);

        if (!post.isWrittenBy(userId)) {
            throw new NotPostOwnerException();
        }

        postRepository.delete(post);
    }
}
