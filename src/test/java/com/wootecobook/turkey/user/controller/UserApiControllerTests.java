package com.wootecobook.turkey.user.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.user.service.UserService;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.wootecobook.turkey.user.domain.UserValidator.*;
import static com.wootecobook.turkey.user.service.UserService.EMAIL_DUPLICATE_MESSAGE;
import static com.wootecobook.turkey.user.service.exception.SignUpException.SIGN_UP_FAIL_MESSAGE;
import static com.wootecobook.turkey.user.service.exception.UserMismatchException.USER_MISMATCH_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

class UserApiControllerTests extends BaseControllerTests {

    private static final String USER_API_URI = "/api/users";
    private static final String USER_API_URI_WITH_SLASH = USER_API_URI + "/";

    @LocalServerPort
    private String port;

    private final RequestFieldsSnippet userRequestFieldsSnippet = requestFields(
            fieldWithPath("email").description("회원의 이메일"),
            fieldWithPath("name").description("회원의 이름"),
            fieldWithPath("password").description("회원의 비밀번호")
    );

    private final ResponseFieldsSnippet userResponseFieldsSnippet = responseFields(
            fieldWithPath("id").description("유저의 고유 식별자"),
            fieldWithPath("email").description("유저의 이메일"),
            fieldWithPath("name").description("유저의 이름"),
            fieldWithPath("login").description("현재 유저의 로그인 여부"),
            subsectionWithPath("profile").type(JsonFieldType.OBJECT).optional().description("회원의 프로필 정보")
    );

    private final PathParametersSnippet userIdPathParametersSnippet = pathParameters(
            parameterWithName("id").description("유저의 고유 식별자")
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
    }

    @Test
    void 유저_생성() {
        //given
        final String email = "UserApiCreate@gmail.com";
        UserRequest userRequest = UserRequest.builder()
                .email(email)
                .name(VALID_USER_NAME)
                .password(VALID_USER_PASSWORD)
                .build();

        //when
        UserResponse userResponse = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(UserResponse.class)
                .consumeWith(document("user/201/create",
                        userRequestFieldsSnippet,
                        responseHeaders(
                            headerWithName("Location").description("생성된 유저의 정보 접근 URI")
                        ),
                        userResponseFieldsSnippet
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getEmail()).isEqualTo(email);
        assertThat(userResponse.getName()).isEqualTo(VALID_USER_NAME);
    }

    @Test
    void 중복_이메일_생성_에러() {
        //given
        String email = "duplicated@dupli.cated";

        addUser(VALID_USER_NAME, email, VALID_USER_PASSWORD);

        UserRequest userRequest = UserRequest.builder()
                .email(email)
                .name(VALID_USER_NAME)
                .password(VALID_USER_PASSWORD)
                .build();

        //when
        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("user/400/create/overlap",
                        userRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, EMAIL_DUPLICATE_MESSAGE);
    }

    @Test
    void 유저_생성_이메일_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email("test")
                .name(VALID_USER_NAME)
                .password(VALID_USER_PASSWORD)
                .build();

        //when
        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("/user/400/create/email",
                        userRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, EMAIL_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_생성_이메일_길이_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email("a@a.aaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .name(VALID_USER_NAME)
                .password(VALID_USER_PASSWORD)
                .build();

        //when
        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, EMAIL_LENGTH_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_생성_비밀번호_길이_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_USER_EMAIL)
                .name(VALID_USER_NAME)
                .password("passW1!")
                .build();

        //when
        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("/user/400/create/password",
                        userRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, PASSWORD_LENGTH_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_생성_이름_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_USER_EMAIL)
                .name("1")
                .password(VALID_USER_PASSWORD)
                .build();

        //when
        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("/user/400/create/name",
                        userRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, NAME_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_생성_비밀번호_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_USER_EMAIL)
                .name(VALID_USER_NAME)
                .password("passWORD1")
                .build();

        //when
        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("/user/400/create/password",
                        userRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, PASSWORD_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_조회() {
        //given
        String email = "show@show.show";
        String name = "show";

        Long id = addUser(name, email, VALID_USER_PASSWORD);

        //when
        UserResponse userResponse = webTestClient.get()
                .uri(USER_API_URI + "/{id}", id)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(UserResponse.class)
                .consumeWith(document("/user/200/read",
                        pathParameters(
                                parameterWithName("id").description("유저의 고유 식별자")
                        ),
                        userResponseFieldsSnippet
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(userResponse.getId()).isEqualTo(id);
        assertThat(userResponse.getEmail()).isEqualTo(email);
        assertThat(userResponse.getName()).isEqualTo(name);
    }

    @Test
    void 없는_유저_조회() {
        //given
        String email = "nonUser@delete.del";
        String name = "nonUser";

        addUser(name, email, VALID_USER_PASSWORD);

        //when
        ErrorMessage errorMessage = webTestClient.get()
                .uri(USER_API_URI_WITH_SLASH + "{id}", Long.MAX_VALUE)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("user/400/read/none",
                        userIdPathParametersSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).isEqualTo(UserService.NOT_FOUND_MESSAGE);
    }


    @Test
    void 유저_삭제() {
        //given
        String email = "delete@delete.del";
        String name = "delete";

        Long id = addUser(name, email, VALID_USER_PASSWORD);

        //when & then
        webTestClient.delete()
                .uri(USER_API_URI_WITH_SLASH + "{id}", id)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("user/204/delete",
                        userIdPathParametersSnippet
                ));
    }

    @Test
    void 없는_유저_삭제() {
        //given
        String email = "deleteerror@delete.del";
        String name = "delete";

        Long id = addUser(name, email, VALID_USER_PASSWORD);

        //when
        ErrorMessage errorMessage = webTestClient.delete()
                .uri(USER_API_URI_WITH_SLASH + "{id}", Long.MAX_VALUE)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("user/400/delete/none",
                        userIdPathParametersSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).isEqualTo(USER_MISMATCH_MESSAGE);
    }

    @Test
    void 이름으로_유저_검색() {
        // given
        String name = "LSD";
        String email = "search@delete.del";

        addUser("AA" + name, email, VALID_USER_PASSWORD);
        addUser(name + "BBD", email + "b", VALID_USER_PASSWORD);
        addUser("AA" + name + "BB", email + "c", VALID_USER_PASSWORD);

        // when & then
        webTestClient.get()
                .uri(USER_API_URI_WITH_SLASH  + "search?name={name}", name)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody()
                .consumeWith(document("post/search/200",
                        pageResponseSnippets.and(
                                subsectionWithPath("pageable").description("페이지 정보"),
                                subsectionWithPath("content").description("조회하고자 하는 해당 페이지의 Post 목록"),
                                subsectionWithPath("sort").description("조회 정렬 정보")
                        )
                ))
                .jsonPath("$.totalElements").isEqualTo(3);
    }
}