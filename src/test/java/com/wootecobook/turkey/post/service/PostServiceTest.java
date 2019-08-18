package com.wootecobook.turkey.post.service;

import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.domain.Post;
import com.wootecobook.turkey.post.domain.PostRepository;
import com.wootecobook.turkey.post.service.dto.PostRequest;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import com.wootecobook.turkey.post.service.exception.NotFoundPostException;
import com.wootecobook.turkey.post.service.exception.NotPostOwnerException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class PostServiceTest {

    private static final long NOT_FOUND_POST_ID = Long.MAX_VALUE;
    private final PostService postService;

    @Autowired
    private TestEntityManager testEntityManager;

    private Contents testContents;
    private PostRequest postRequest;

    private User author;
    private User other;

    @Autowired
    public PostServiceTest(PostRepository postRepository, UserRepository userRepository) {
        this.postService = new PostService(postRepository, new UserService(userRepository));
    }


    @BeforeEach
    void setUp() {
        author = testEntityManager.persist(new User("pobi@woowa.com", "pobi", "Passw0rd!"));
        other = testEntityManager.persist(new User("olaf@woowa.com", "olaf", "Passw0rd!"));

        String contentsText = "hello";
        postRequest = new PostRequest(contentsText);
        testContents = new Contents(contentsText);
    }

    @Test
    void post_생성_테스트() {
        PostResponse result = postService.save(postRequest, author.getId());

        assertThat(result.getContents()).isEqualTo(testContents);
    }

    @Test
    void post_조회_테스트() {
        Long postId = addPost();
        Post testPost = postService.findById(postId);

        assertThat(testPost.getContents()).isEqualTo(testContents);
    }

    @Test
    void 존재하지_않는_post_조회_예외_테스트() {
        assertThrows(NotFoundPostException.class, () -> postService.findById(NOT_FOUND_POST_ID));
    }

    @Test
    void post_페이징_테스트() {
        int pageNum = 0;
        IntStream.rangeClosed(1, 100).forEach(i ->
                postService.save(new PostRequest("hello" + i), author.getId()));

        Page<PostResponse> pageResponse = postService.findPostResponses(PageRequest.of(pageNum, 10));

        assertThat(pageResponse.getTotalElements()).isEqualTo(100);
        assertThat(pageResponse.getTotalPages()).isEqualTo(10);
        assertThat(pageResponse.getNumber()).isEqualTo(pageNum);

        List<PostResponse> postResponses = pageResponse.getContent();

        for (int i = 1; i <= 10; i++) {
            PostResponse postResponse = postResponses.get(i - 1);
            assertThat(postResponse.getContents()).isEqualTo(new Contents("hello" + i));
        }
    }

    @Test
    void post_수정_테스트() {
        Long postId = addPost();
        PostRequest postRequest = new PostRequest("world!");

        PostResponse updateResult = postService.update(postRequest, postId, author.getId());

        assertThat(updateResult.getContents()).isEqualTo(new Contents("world!"));
    }

    @Test
    void 없는_게시글_수정_예외_테스트() {
        assertThrows(NotFoundPostException.class, () ->
                postService.update(postRequest, NOT_FOUND_POST_ID, author.getId()));
    }

    @Test
    void 수정_권한이_없는_유저_게시글_수정_예외_테스트() {
        Long postId = addPost();
        PostRequest postRequest = new PostRequest("world!");

        assertThrows(NotPostOwnerException.class, () -> postService.update(postRequest, postId, other.getId()));
    }

    @Test
    void post_삭제_테스트() {
        Long postId = addPost();

        assertDoesNotThrow(() -> postService.delete(postId, author.getId()));
    }

    @Test
    void 없는_post_삭제_예외_테스트() {
        assertThrows(NotFoundPostException.class, () -> postService.delete(NOT_FOUND_POST_ID, author.getId()));
    }

    @Test
    void 삭제_권한이_없는_유저_게시글_삭제_예외_테스트() {
        Long postId = addPost();

        assertThrows(NotPostOwnerException.class, () -> postService.delete(postId, other.getId()));
    }

    private Long addPost() {
        return postService.save(postRequest, author.getId()).getId();
    }

}