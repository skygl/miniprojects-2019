package com.wootecobook.turkey.comment.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.comment.domain.exception.NotCommentOwnerException;
import com.wootecobook.turkey.comment.service.dto.CommentCreate;
import com.wootecobook.turkey.comment.service.dto.CommentResponse;
import com.wootecobook.turkey.comment.service.dto.CommentUpdate;
import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.config.AwsMockConfig;
import com.wootecobook.turkey.good.service.dto.GoodResponse;
import com.wootecobook.turkey.post.service.dto.PostResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@Import(AwsMockConfig.class)
class CommentApiControllerTests extends BaseControllerTests {

    private static final String COMMENT_API_URI = "/api/posts/{postId}/comments";

    @LocalServerPort
    private String port;

    private Long userId;
    private Long postId;
    private Long commentId;
    private String jSessionId;
    private String uri;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .filter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        userId = addUser("name", VALID_USER_EMAIL, VALID_USER_PASSWORD);
        jSessionId = logIn(VALID_USER_EMAIL, VALID_USER_PASSWORD);

        //given
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("contents", "contents");

        //when
        PostResponse postResponse = webTestClient.post().uri("/api/posts")
                .cookie(JSESSIONID, jSessionId)
                .syncBody(bodyBuilder.build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PostResponse.class)
                .returnResult()
                .getResponseBody();

        postId = postResponse.getId();
        uri = linkTo(CommentApiController.class, postId).toUri().toString();

        // 댓글 작성
        final CommentCreate commentCreate = new CommentCreate("contents", null);
        commentId = addComment(commentCreate);

        // 답글 작성
        commentCreate.setParentId(commentId);
        addComment(commentCreate);
        addComment(commentCreate);
    }

    @Test
    void 댓글_목록_조회() {
        // given
        final int size = 20;
        final int page = 0;

        // when & then
        webTestClient.get().uri(COMMENT_API_URI + "?size={size}&page={page}", postId, size, page)
                .accept(MEDIA_TYPE)
                .cookie(JSESSIONID, jSessionId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody()
                .consumeWith(document("comment/200/read",
                        pathParameters(
                                parameterWithName("postId").description("댓글을 작성할 Post 고유 식별자")
                        ),
                        requestParameters(
                                parameterWithName("size").description("조회할 댓글의 갯수"),
                                parameterWithName("page").description("조회할 페이지의 번호")
                        ),
                        pageResponseSnippets.and(
                                subsectionWithPath("pageable").description("페이지 정보"),
                                subsectionWithPath("content").description("조회하고자 하는 해당 페이지의 Post 목록"),
                                subsectionWithPath("sort").description("조회 정렬 정보")
                        )
                ))
                .jsonPath("$.size").isEqualTo(size)
                .jsonPath("$.number").isEqualTo(page)
                .jsonPath("$.totalElements").isEqualTo(1);
    }

    @Test
    void 답글만_목록_조회() {
        // given
        final int size = 20;
        final int page = 0;

        // when & then
        webTestClient.get().uri(COMMENT_API_URI + "/{id}/children?size={size}&page={page}", postId, commentId, size, page)
                .accept(MEDIA_TYPE)
                .cookie(JSESSIONID, jSessionId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody()
                .consumeWith(document("comment/reply/200/read",
                        pathParameters(
                                parameterWithName("postId").description("답글을 작성할 Post 고유 식별자"),
                                parameterWithName("id").description("답글을 작성할 Comment 고유 식별자")
                        ),
                        requestParameters(
                                parameterWithName("size").description("조회할 댓글의 갯수"),
                                parameterWithName("page").description("조회할 페이지의 번호")
                        ),
                        pageResponseSnippets.and(
                                subsectionWithPath("pageable").description("페이지 정보"),
                                subsectionWithPath("content").description("조회하고자 하는 해당 페이지의 Post 목록"),
                                subsectionWithPath("sort").description("조회 정렬 정보")
                        )
                ))
                .jsonPath("$.size").isEqualTo(size)
                .jsonPath("$.number").isEqualTo(page)
                .jsonPath("$.totalElements").isEqualTo(2);
    }

    @Test
    void 댓글_삭제_성공() {
        // when & then
        webTestClient.delete().uri(COMMENT_API_URI + "/{id}", postId, commentId)
                .cookie(JSESSIONID, jSessionId)
                .exchange()
                .expectStatus().isNoContent()
                .expectHeader().valueMatches("Location", ".*" + uri)
                .expectBody()
                .consumeWith(document("comment/204/delete",
                        pathParameters(
                                parameterWithName("postId").description("Comment가 작성된 Post 고유 식별자"),
                                parameterWithName("id").description("삭제하려는 Comment의 고유 식별자")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("삭제한 댓글의 Post 조회 URI")
                        )
                ));
    }

    @Test
    void 댓글_수정_성공() {
        // given
        final CommentUpdate commentUpdate = new CommentUpdate();
        commentUpdate.setContents("댓글_수정_성공");

        // when
        final CommentResponse commentResponse = webTestClient.put().uri(COMMENT_API_URI + "/{id}", postId, commentId)
                .cookie(JSESSIONID, jSessionId)
                .body(Mono.just(commentUpdate), CommentUpdate.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(CommentResponse.class)
                .consumeWith(document("comment/200/update",
                        pathParameters(
                                parameterWithName("postId").description("Comment가 작성된 Post 고유 식별자"),
                                parameterWithName("id").description("삭제하려는 Comment의 고유 식별자")
                        ),
                        requestFields(
                                fieldWithPath("contents").description("수정하려는 Comment의 내용")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Comment 고유 식별자"),
                                fieldWithPath("parentId").type(JsonFieldType.NUMBER).optional().description("해당 Comment의 상위 댓글 고유 식별자"),
                                fieldWithPath("contents").description("Comment 내용"),
                                fieldWithPath("countOfChildren").description("답글의 갯수"),
                                fieldWithPath("createdAt").description("댓글 생성 날짜"),
                                fieldWithPath("updatedAt").description("댓글 수정 날짜"),
                                subsectionWithPath("userResponse").description("댓글 작성한 유저의 정보"),
                                subsectionWithPath("goodResponse").description("댓글의 좋아요 정보")
                        )
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(commentUpdate.getContents()).isEqualTo(commentResponse.getContents());
    }

    @Test
    void 다른_작성자_댓글_수정_예외처리() {
        // given
        final String email = "otheremail@gmail.com";
        final String password = "P@ssw0rd";
        addUser("name", email, password);
        String otherJsessionId = logIn(email, password);

        final CommentUpdate commentUpdate = new CommentUpdate();
        commentUpdate.setContents("다른_작성자_댓글_수정_예외처리");

        // when
        final ErrorMessage errorMessage = webTestClient.put().uri(COMMENT_API_URI + "/{id}", postId, commentId)
                .cookie(JSESSIONID, otherJsessionId)
                .body(Mono.just(commentUpdate), CommentUpdate.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .consumeWith(document("comment/400/update/no-auth",
                        pathParameters(
                                parameterWithName("postId").description("Comment가 작성된 Post 고유 식별자"),
                                parameterWithName("id").description("삭제하려는 Comment의 고유 식별자")
                        ),
                        requestFields(
                                fieldWithPath("contents").description("수정하려는 Comment의 내용")
                        ),
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(NotCommentOwnerException.DEFAULT_MESSAGE).isEqualTo(errorMessage.getMessage());
    }

    @Test
    void 답글_저장_성공() {
        // given
        final CommentCreate commentCreate = new CommentCreate("contents", commentId);

        // when
        final CommentResponse commentResponse = webTestClient.post().uri(uri)
                .accept(MEDIA_TYPE)
                .cookie(JSESSIONID, jSessionId)
                .body(Mono.just(commentCreate), CommentCreate.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectHeader().valueMatches("Location", ".*" + uri)
                .expectBody(CommentResponse.class)
                .returnResult()
                .getResponseBody();

        // then
        assertThat(commentCreate.getContents()).isEqualTo(commentResponse.getContents());
        assertThat(commentId).isEqualTo(commentResponse.getParentId());
    }

    @Test
    void 댓글_좋아요_및_좋아요_취소_정상_로직() {
        // given & when
        GoodResponse goodResponse = webTestClient.post().uri(COMMENT_API_URI + "/{id}/good", postId, commentId)
                .cookie(JSESSIONID, jSessionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GoodResponse.class)
                .consumeWith(document("comment/good/200/create",
                        pathParameters(
                                parameterWithName("postId").description("Comment가 작성된 Post 고유 식별자"),
                                parameterWithName("id").description("삭제하려는 Comment의 고유 식별자")
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

        // given & when
        GoodResponse goodCancelResponse = webTestClient.post().uri(uri + "/{id}/good", commentId)
                .cookie(JSESSIONID, jSessionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GoodResponse.class)
                .returnResult()
                .getResponseBody();

        // then
        assertThat(goodCancelResponse.getTotalGood()).isEqualTo(0);
    }

    private Long addComment(CommentCreate commentCreate) {
        final CommentResponse commentResponse = webTestClient.post().uri(COMMENT_API_URI, postId)
                .accept(MEDIA_TYPE)
                .cookie(JSESSIONID, jSessionId)
                .body(Mono.just(commentCreate), CommentCreate.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectHeader().valueMatches("Location", ".*" + uri)
                .expectBody(CommentResponse.class)
                .consumeWith(document("comment/201/create",
                        pathParameters(
                                parameterWithName("postId").description("댓글을 작성하는 게시글의 고유 식별자")
                        ),
                        requestFields(
                                fieldWithPath("contents").description("댓글 내용"),
                                fieldWithPath("parentId").optional().type(JsonFieldType.NUMBER).description("답글인 경우 상위 댓글 고유 식별자")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Comment 고유 식별자"),
                                fieldWithPath("parentId").type(JsonFieldType.NUMBER).optional().description("해당 Comment의 상위 댓글 고유 식별자"),
                                fieldWithPath("contents").description("Comment 내용"),
                                fieldWithPath("countOfChildren").description("답글의 갯수"),
                                fieldWithPath("createdAt").description("댓글 생성 날짜"),
                                fieldWithPath("updatedAt").description("댓글 수정 날짜"),
                                subsectionWithPath("userResponse").description("댓글 작성한 유저의 정보"),
                                subsectionWithPath("goodResponse").description("댓글의 좋아요 정보")
                        )
                ))
                .returnResult()
                .getResponseBody();

        return commentResponse.getId();
    }

    @AfterEach
    void tearDown() {
        deleteUser(userId, VALID_USER_EMAIL, VALID_USER_PASSWORD);
    }
}
