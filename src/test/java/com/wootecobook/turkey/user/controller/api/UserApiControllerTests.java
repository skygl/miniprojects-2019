package com.wootecobook.turkey.user.controller.api;

import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.user.controller.BaseControllerTests;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.wootecobook.turkey.user.domain.UserValidator.*;
import static com.wootecobook.turkey.user.service.exception.NotFoundUserException.NOT_FOUND_USER_MESSAGE;
import static com.wootecobook.turkey.user.service.exception.SignUpException.SIGN_UP_FAIL_MESSAGE;
import static com.wootecobook.turkey.user.service.exception.UserMismatchException.USER_MISMATCH_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiControllerTests extends BaseControllerTests {

    private static final String USER_API_URI = "/api/users";
    private static final String USER_API_URI_WITH_SLASH = USER_API_URI + "/";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void 유저_생성() {
        final String email = "UserApiCreate@gmail.com";
        UserRequest userRequest = UserRequest.builder()
                .email(email)
                .name(VALID_USER_NAME)
                .password(VALID_USER_PASSWORD)
                .build();

        UserResponse userResponse = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getEmail()).isEqualTo(email);
        assertThat(userResponse.getName()).isEqualTo(VALID_USER_NAME);
    }

    @Test
    void 중복_이메일_생성_에러() {
        String email = "duplicated@dupli.cated";

        addUser(VALID_USER_NAME, email, VALID_USER_PASSWORD);

        UserRequest userRequest = UserRequest.builder()
                .email(email)
                .name(VALID_USER_NAME)
                .password(VALID_USER_PASSWORD)
                .build();

        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE);
    }

    @Test
    void 유저_생성_이메일_에러() {
        UserRequest userRequest = UserRequest.builder()
                .email("test")
                .name(VALID_USER_NAME)
                .password(VALID_USER_PASSWORD)
                .build();

        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, EMAIL_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_생성_이름_에러() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_USER_EMAIL)
                .name("1")
                .password(VALID_USER_PASSWORD)
                .build();

        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, NAME_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_생성_비밀번호_에러() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_USER_EMAIL)
                .name(VALID_USER_NAME)
                .password("1")
                .build();

        ErrorMessage errorMessage = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, PASSWORD_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_조회() {
        String email = "show@show.show";
        String name = "show";

        Long id = addUser(name, email, VALID_USER_PASSWORD);

        UserResponse userResponse = webTestClient.get()
                .uri(USER_API_URI_WITH_SLASH + id)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(userResponse.getId()).isEqualTo(id);
        assertThat(userResponse.getEmail()).isEqualTo(email);
        assertThat(userResponse.getName()).isEqualTo(name);
    }

    @Test
    void 없는_유저_조회() {
        ErrorMessage errorMessage = webTestClient.get()
                .uri(USER_API_URI_WITH_SLASH + Long.MAX_VALUE)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();

        assertThat(errorMessage.getMessage()).isEqualTo(NOT_FOUND_USER_MESSAGE);
    }


    @Test
    void 유저_삭제() {
        String email = "delete@delete.del";
        String name = "delete";

        Long id = addUser(name, email, VALID_USER_PASSWORD);

        webTestClient.delete()
                .uri(USER_API_URI_WITH_SLASH + id)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void 없는_유저_삭제() {
        String email = "deleteerror@delete.del";
        String name = "delete";

        Long id = addUser(name, email, VALID_USER_PASSWORD);

        ErrorMessage errorMessage = webTestClient.delete()
                .uri(USER_API_URI_WITH_SLASH + Long.MAX_VALUE)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(ErrorMessage.class)
                .returnResult()
                .getResponseBody();
        assertThat(errorMessage.getMessage()).isEqualTo(USER_MISMATCH_MESSAGE);
    }
}