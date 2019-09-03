package com.wootecobook.turkey.post.service;

import com.wootecobook.turkey.comment.domain.CommentRepository;
import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.file.service.UploadFileService;
import com.wootecobook.turkey.good.service.PostGoodService;
import com.wootecobook.turkey.good.service.dto.GoodResponse;
import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.post.domain.PostRepository;
import com.wootecobook.turkey.post.service.dto.PostRequest;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import com.wootecobook.turkey.post.service.exception.NotPostOwnerException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    public static final String POST_DIRECTORY_NAME = "post";
    private static final String NOT_FOUND_MESSAGE = "존재하지 않는 게시글입니다.";

    private final PostRepository postRepository;
    private final PostGoodService postGoodService;
    private final UserService userService;
    private final UploadFileService uploadFileService;
    private final CommentRepository commentRepository;

    public PostService(final PostRepository postRepository, final PostGoodService postGoodService,
                       final UserService userService, final UploadFileService uploadFileService, final CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postGoodService = postGoodService;
        this.userService = userService;
        this.uploadFileService = uploadFileService;
    }

    public PostResponse save(final PostRequest postRequest, final Long userId) {
        User user = userService.findById(userId);
        User receiver = findReceiverIfExist(postRequest.getReceiver());
        List<UploadFile> savedFiles = saveAttachments(postRequest.getFiles(), user);

        Post savedPost = postRepository.save(postRequest.toEntity(user, receiver, savedFiles));

        return PostResponse.getPostResponse(savedPost);
    }

    private User findReceiverIfExist(final Long receiverId) {
        return receiverId == null ? null : userService.findById(receiverId);
    }

    private List<UploadFile> saveAttachments(List<MultipartFile> attachments, User owner) {
        if (attachments == null) {
            return new ArrayList<>();
        }
        return attachments.stream()
                .map(file -> uploadFileService.save(file, POST_DIRECTORY_NAME, owner))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Post findById(final Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> findPostResponses(final Pageable pageable, final Long userId) {
        User user = userService.findById(userId);

        return postRepository.findByUserId(pageable, userId)
                .map(post -> {
                    GoodResponse goodResponse = GoodResponse.of(postGoodService.countBy(post),
                            postGoodService.existsByPostAndUser(post, user));
                    int totalComment = commentRepository.countByPost(post);
                    return PostResponse.from(post, goodResponse, totalComment);
                });
    }

    public PostResponse update(final PostRequest postRequest, final Long postId, final Long userId) {
        Post post = findById(postId);
        User user = userService.findById(userId);

        if (post.isWrittenBy(userId)) {
            Post updatePost = Post.builder()
                    .id(postId)
                    .contents(new Contents(postRequest.getContents()))
                    .build();

            post.update(updatePost);
            GoodResponse goodResponse = GoodResponse.of(
                    postGoodService.countBy(post),
                    postGoodService.existsByPostAndUser(post, user));
            int totalComment = commentRepository.countByPost(post);

            return PostResponse.from(post, goodResponse, totalComment);
        }

        throw new NotPostOwnerException();
    }

    public void delete(final Long postId, final Long userId) {
        Post post = findById(postId);

        if (!post.isWrittenBy(userId)) {
            throw new NotPostOwnerException();
        }

        postRepository.delete(post);
    }

    public GoodResponse toggleGood(final Long postId, final Long userId) {
        Post post = findById(postId);
        User user = userService.findById(userId);

        return GoodResponse.of(
                postGoodService.toggleGood(post, user),
                postGoodService.existsByPostAndUser(post, user));
    }

    @Transactional(readOnly = true)
    public GoodResponse countPostGoodByPost(Long postId, Long userId) {
        Post post = findById(postId);
        User user = userService.findById(userId);

        return GoodResponse.of(
                postGoodService.countBy(post),
                postGoodService.existsByPostAndUser(post, user));
    }
}
