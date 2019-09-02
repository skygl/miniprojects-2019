package com.wootecobook.turkey.login.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.login.service.dto.LoginRequest;
import com.wootecobook.turkey.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.wootecobook.turkey.login.service.exception.LoginFailException.LOGIN_FAIL_MESSAGE;
import static com.wootecobook.turkey.user.domain.User.INVALID_PASSWORD_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

class LoginApiControllerTests extends BaseControllerTests {

    @LocalServerPort
    private String port;

    private Long userId;


    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .filter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        userId = addUser(VALID_USER_NAME, VALID_USER_EMAIL, VALID_USER_PASSWORD);
    }

    @Test
    void 로그인() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .build();

        //when
        UserSession userSession = webTestClient.post()
                .uri("/login")
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserSession.class)
                .consumeWith(document("login",
                        requestFields(
                                fieldWithPath("email").description("로그인 시 필요한 이메일"),
                                fieldWithPath("password").description("영문 대소문자, 특수문자, 숫자로 구성된 8자 이상 20자 이하의 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("id").description("유저의 고유 id"),
                                fieldWithPath("email").description("유저가 로그인 시 사용한 이메일"),
                                fieldWithPath("name").description("유저의 name"),
                                fieldWithPath("login").description("현재 로그인 유무"),
                                subsectionWithPath("profile").description("유저의 등록 프로필")
                        )
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(userSession.getName()).isEqualTo(VALID_USER_NAME);
        assertThat(userSession.getEmail()).isEqualTo(VALID_USER_EMAIL);
    }

    @Test
    void 없는_email로_로그인() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("invalid@invalid.invalid")
                .password(VALID_USER_PASSWORD)
                .build();

        //when
        ErrorMessage errorMessage = webTestClient.post()
                .uri("/login")
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).contains(LOGIN_FAIL_MESSAGE, UserService.NOT_FOUND_MESSAGE);
    }

    @Test
    void 잘못된_비밀번호로_로그인() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_USER_EMAIL)
                .password("invalid")
                .build();

        //when
        ErrorMessage errorMessage = webTestClient.post()
                .uri("/login")
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).contains(LOGIN_FAIL_MESSAGE, INVALID_PASSWORD_MESSAGE);
    }

    @Test
    void 로그아웃() {
        //when & then
        webTestClient.post()
                .uri("/logout")
                .cookie(JSESSIONID, logIn(VALID_USER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("logout"));
    }

    @Test
    void 로그인_안된_상태에서_로그아웃() {
        //when & then
        webTestClient.post()
                .uri("/logout")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueMatches("Location", ".*/users/form");
    }

    @AfterEach
    void tearDown() {
        deleteUser(userId, VALID_USER_EMAIL, VALID_USER_PASSWORD);
    }

}