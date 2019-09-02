package com.wootecobook.turkey.post.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.config.AwsMockConfig;
import com.wootecobook.turkey.good.service.dto.GoodResponse;
import com.wootecobook.turkey.post.domain.Contents;
import com.wootecobook.turkey.post.service.dto.PostRequest;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@Import(AwsMockConfig.class)
class PostApiControllerTests extends BaseControllerTests {

    public static final String POST_URL = "/api/posts";

    @LocalServerPort
    private String port;

    private Long authorId;
    private String authorJSessionId;

    private Long otherId;
    private String otherJSessionId;

    private ByteArrayResource testFile;

    private final ResponseFieldsSnippet postResponseSnippets = responseFields(
            fieldWithPath("id").description("글의 고유 id"),
            fieldWithPath("contents.contents").description("글의 내용"),
            fieldWithPath("createdAt").description("글 작성 일자"),
            fieldWithPath("updatedAt").description("글 수정 일자"),
            fieldWithPath("totalComment").description("글에 달린 댓글의 총 갯수"),
            subsectionWithPath("files").description("글과 함께 업로드 된 사진 또는 동영상 정보"),
            subsectionWithPath("author").description("작성자 정보"),
            subsectionWithPath("receiver").optional().description("글 받는 사람 정보"),
            subsectionWithPath("goodResponse").description("해당 글의 좋아요 정보")
    );

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .filter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        authorId = addUser("olaf", VALID_USER_EMAIL, VALID_USER_PASSWORD);
        otherId = addUser("chulsea", "chulsea@mail.com", VALID_USER_PASSWORD);
        authorJSessionId = logIn(VALID_USER_EMAIL, VALID_USER_PASSWORD);
        otherJSessionId = logIn("chulsea@mail.com", VALID_USER_PASSWORD);
        testFile = new ByteArrayResource(new byte[]{1, 2, 3, 4}) {
            @Override
            public String getFilename() {
                return "test_file.jpg";
            }
        };
    }

    @Test
    void 파일_첨부하고_post_생성_정상_로직_테스트() {
        //given
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("files", testFile, MediaType.parseMediaType("image/jpeg"));
        bodyBuilder.part("files", testFile, MediaType.parseMediaType("image/jpeg"));
        bodyBuilder.part("contents", "hello");

        //when
        PostResponse postResponse = webTestClient.post().uri(POST_URL)
                .cookie(JSESSIONID, authorJSessionId)
                .syncBody(bodyBuilder.build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PostResponse.class)
                .consumeWith(document("post/201/create/withfiles",
                        requestParts(
                                partWithName("contents").description("글의 내용"),
                                partWithName("files").optional().description("글과 함께 업로드하는 사진 또는 동영상, 여러장 가능"),
                                partWithName("receiver").optional().description("다른 유저에게 글쓰는 경우, 해당 유저의 id")
                        ),
                        postResponseSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(postResponse.getContents()).isEqualTo(new Contents("hello"));
        assertThat(postResponse.getFiles().size()).isEqualTo(2);
    }

    @Test
    void 파일_첨부없이_post_생성_정상_로직_테스트() {
        //given
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("contents", "hello");

        //when
        PostResponse postResponse = webTestClient.post().uri(POST_URL)
                .cookie(JSESSIONID, authorJSessionId)
                .syncBody(bodyBuilder.build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PostResponse.class)
                .consumeWith(document("post/201/create/nofiles",
                        requestParts(
                                partWithName("contents").description("글의 내용"),
                                partWithName("files").optional().description("글과 함께 업로드하는 사진 또는 동영상, 여러장 가능"),
                                partWithName("receiver").optional().description("다른 유저에게 글쓰는 경우, 해당 유저의 id")
                        ),
                        postResponseSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(postResponse.getContents()).isEqualTo(new Contents("hello"));
        assertThat(postResponse.getFiles().size()).isEqualTo(0);
    }

    @Test
    void post_생성_contents가_빈값인_경우_예외_테스트() {
        //given
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("contents", "");

        //when
        ErrorMessage errorMessage = webTestClient.post().uri(POST_URL)
                .cookie(JSESSIONID, authorJSessionId)
                .syncBody(bodyBuilder.build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .consumeWith(document("post/400/no-content/create",
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage).isNotNull();
    }

    @Test
    void 페이지_조회_정상_로직_테스트() {
        //when & then
        webTestClient.get().uri(POST_URL + "?page=0")
                .cookie(JSESSIONID, authorJSessionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .consumeWith(document("post/200/read",
                        relaxedRequestParameters(
                                parameterWithName("page").description("페이지 번호")
                        ),
                        pageResponseSnippets.and(
                                subsectionWithPath("pageable").description("페이지 정보"),
                                subsectionWithPath("content").description("조회하고자 하는 해당 페이지의 Post 목록"),
                                subsectionWithPath("sort").description("조회 정렬 정보")
                        )
                ));
    }

    @Test
    void 게시글_수정_정상_로직_테스트() {
        //given
        Long postId = addPost("olaf");

        PostRequest postUpdateRequest = PostRequest.builder().contents("chelsea").build();

        //when
        PostResponse postResponse = webTestClient.put().uri(POST_URL + "/{postId}", postId)
                .cookie(JSESSIONID, authorJSessionId)
                .contentType(MEDIA_TYPE)
                .body(Mono.just(postUpdateRequest), PostRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PostResponse.class)
                .consumeWith(document("post/200/update",
                        pathParameters(
                                parameterWithName("postId").description("수정할 Post의 id")
                        ),
                        relaxedRequestFields(
                                fieldWithPath("contents").description("수정할 글의 내용")
                        ),
                        postResponseSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(postResponse.getContents()).isEqualTo(new Contents("chelsea"));
    }

    @Test
    void 게시글_수정_contents가_빈_내용인_경우_예외_테스트() {
        //given
        Long postId = addPost("olaf");

        PostRequest postUpdateRequest = PostRequest.builder().contents("").build();

        //when & then
        webTestClient.put().uri(POST_URL + "/{postId}", postId)
                .cookie(JSESSIONID, authorJSessionId)
                .contentType(MEDIA_TYPE)
                .body(Mono.just(postUpdateRequest), PostRequest.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void 게시글_수정_권한없는_유저인_경우_예외_테스트() {
        //given
        Long postId = addPost("olaf");

        PostRequest postUpdateRequest = PostRequest.builder().contents("chelsea").build();

        //when
        webTestClient.put().uri(POST_URL + "/{postId}", postId)
                .cookie(JSESSIONID, otherJSessionId)
                .contentType(MEDIA_TYPE)
                .body(Mono.just(postUpdateRequest), PostRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("post/400/no-auth/update",
                        badRequestSnippets
                ));;
    }

    @Test
    void 게시글_삭제_정상_로직_테스트() {
        //given
        Long postId = addPost("olaf");

        //when & then
        webTestClient.delete().uri(POST_URL + "/{postId}", postId)
                .cookie(JSESSIONID, authorJSessionId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("post/204/delete",
                        pathParameters(
                                parameterWithName("postId").description("해당 Post의 고유 식별 id")
                        )
                ));
    }

    @Test
    void 게시글_삭제_존재하지_않는_게시글_예외_테스트() {
        //when & then
        webTestClient.delete().uri(POST_URL + "/{postId}", Long.MAX_VALUE)
                .cookie(JSESSIONID, authorJSessionId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("post/400/delete/none",
                        pathParameters(
                                parameterWithName("postId").description("해당 Post의 고유 식별 id")
                        ),
                        badRequestSnippets
                ));
    }

    @Test
    void 게시글_삭제_권한없는_유저인_경우_예외_테스트() {
        //given
        Long postId = addPost("olaf");

        //when & then
        webTestClient.delete().uri(POST_URL + "/{postId}", postId)
                .cookie(JSESSIONID, otherJSessionId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(document("post/400/delete/no-auth",
                        pathParameters(
                                parameterWithName("postId").description("해당 Post의 고유 식별 id")
                        ),
                        badRequestSnippets
                ));
    }

    @Test
    void 게시글_좋아요_생성_삭제_테스트() {
        // given
        Long postId = addPost("olaf");

        // when
        GoodResponse goodResponse = webTestClient.post().uri(POST_URL + "/{postId}/good", postId)
                .cookie(JSESSIONID, authorJSessionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GoodResponse.class)
                .consumeWith(document("post/200/good/create",
                        pathParameters(
                                parameterWithName("postId").description("해당 Post의 고유 식별 id")
                        ),
                        responseFields(
                                fieldWithPath("totalGood").description("해당 게시글의 총 좋아요 갯수"),
                                fieldWithPath("gooded").description("현재 로그인 한 유저의 해당 Post 좋아요 여부")
                        )
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(goodResponse.getTotalGood()).isEqualTo(1);

        // when
        GoodResponse postGoodCancelResponse = webTestClient.post().uri(POST_URL + "/{postId}/good", postId)
                .cookie(JSESSIONID, authorJSessionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GoodResponse.class)
                .returnResult()
                .getResponseBody();

        // then
        assertThat(postGoodCancelResponse.getTotalGood()).isEqualTo(0);
    }

    private Long addPost(String contents) {
        //given
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("contents", contents);

        PostResponse postResponse = webTestClient.post().uri(POST_URL)
                .cookie(JSESSIONID, authorJSessionId)
                .syncBody(bodyBuilder.build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PostResponse.class)
                .returnResult()
                .getResponseBody();

        return postResponse.getId();
    }

    @AfterEach
    void tearDown() {
        deleteUser(authorId, VALID_USER_EMAIL, VALID_USER_PASSWORD);
        deleteUser(otherId, "chulsea@mail.com", VALID_USER_PASSWORD);
    }
}