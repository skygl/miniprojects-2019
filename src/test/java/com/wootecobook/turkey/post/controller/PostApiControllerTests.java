package com.wootecobook.turkey.post.controller;

import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.service.dto.PostRequest;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import com.wootecobook.turkey.user.controller.BaseControllerTests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostApiControllerTests extends BaseControllerTests {

    public static final String POST_URL = "/api/posts";

    @Autowired
    private WebTestClient webTestClient;

    private Long userId;
    private String jSessionId;

    @BeforeEach
    void setUp() {
        userId = addUser("olaf", VALID_USER_EMAIL, VALID_USER_PASSWORD);
        jSessionId = logIn(VALID_USER_EMAIL, VALID_USER_PASSWORD);
    }

    @Test
    void post_생성_정상_로직_테스트() {
        PostRequest postRequest = new PostRequest("contents");

        PostResponse postResponse = webTestClient.post().uri(POST_URL)
                .cookie(JSESSIONID, jSessionId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(postRequest), PostRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PostResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(postResponse.getContents()).isEqualTo(new Contents("contents"));
    }

    @Test
    void post_생성_contents가_빈값인_경우_예외_테스트() {
        PostRequest postRequest = new PostRequest("");

        ErrorMessage errorMessage = webTestClient.post().uri(POST_URL)
                .cookie(JSESSIONID, jSessionId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(postRequest), PostRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorMessage).isNotNull();
    }

    @Test
    void 페이지_조회_정상_로직_테스트() {
        webTestClient.get().uri(POST_URL)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void 게시글_수정_정상_로직_테스트() {
        PostRequest postRequest = new PostRequest("olaf");
        Long postId = addPost(postRequest);

        PostRequest postUpdateRequest = new PostRequest("chelsea");
        PostResponse postResponse = webTestClient.put().uri(POST_URL + "/{postId}", postId)
                .cookie(JSESSIONID, jSessionId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(postUpdateRequest), PostRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PostResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(postResponse.getContents()).isEqualTo(new Contents("chelsea"));
    }

    @Test
    void 게시글_수정_contents가_빈_내용인_경우_예외_테스트() {
        PostRequest postRequest = new PostRequest("olaf");
        Long postId = addPost(postRequest);

        PostRequest postUpdateRequest = new PostRequest("");
        webTestClient.put().uri(POST_URL + "/{postId}", postId)
                .cookie(JSESSIONID, jSessionId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(postUpdateRequest), PostRequest.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void 게시글_삭제_정상_로직_테스트() {
        PostRequest postRequest = new PostRequest("olaf");
        Long postId = addPost(postRequest);

        webTestClient.delete().uri(POST_URL + "/{postId}", postId)
                .cookie(JSESSIONID, jSessionId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void 게시글_삭제_존재하지_않는_게시글_예외_테스트() {
        PostRequest postRequest = new PostRequest("olaf");

        webTestClient.delete().uri(POST_URL + "/{postId}", Long.MAX_VALUE)
                .cookie(JSESSIONID, jSessionId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private Long addPost(PostRequest postRequest) {
        PostResponse postResponse = webTestClient.post().uri(POST_URL)
                .cookie(JSESSIONID, jSessionId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(postRequest), PostRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PostResponse.class)
                .returnResult()
                .getResponseBody();

        return postResponse.getId();
    }

    @AfterEach
    void tearDown() {
        deleteUser(userId, VALID_USER_EMAIL, VALID_USER_PASSWORD);
    }
}