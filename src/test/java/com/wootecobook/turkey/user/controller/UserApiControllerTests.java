package com.wootecobook.turkey.user.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.user.service.UserService;
import com.wootecobook.turkey.user.service.dto.MyPageResponse;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.wootecobook.turkey.user.domain.UserValidator.*;
import static com.wootecobook.turkey.user.service.UserService.EMAIL_DUPLICATE_MESSAGE;
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
                .password("1")
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
        assertThat(errorMessage.getMessage()).contains(SIGN_UP_FAIL_MESSAGE, PASSWORD_CONSTRAINT_MESSAGE);
    }

    @Test
    void 유저_조회() {
        //given
        String email = "show@show.show";
        String name = "show";

        Long id = addUser(name, email, VALID_USER_PASSWORD);

        //when
        MyPageResponse myPageResponse = webTestClient.get()
                .uri(USER_API_URI + "/{userId}/mypage", id)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(MyPageResponse.class)
                .returnResult()
                .getResponseBody();

        //then
        assertThat(myPageResponse.getUser().getId()).isEqualTo(id);
        assertThat(myPageResponse.getUser().getEmail()).isEqualTo(email);
        assertThat(myPageResponse.getUser().getName()).isEqualTo(name);
    }

    @Test
    void 없는_유저_조회() {
        //given
        String email = "nonUser@delete.del";
        String name = "nonUser";

        addUser(name, email, VALID_USER_PASSWORD);

        //when
        ErrorMessage errorMessage = webTestClient.get()
                .uri(USER_API_URI_WITH_SLASH + Long.MAX_VALUE)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
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
                .uri(USER_API_URI_WITH_SLASH + id)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void 없는_유저_삭제() {
        //given
        String email = "deleteerror@delete.del";
        String name = "delete";

        Long id = addUser(name, email, VALID_USER_PASSWORD);

        //when
        ErrorMessage errorMessage = webTestClient.delete()
                .uri(USER_API_URI_WITH_SLASH + Long.MAX_VALUE)
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
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
                .uri(USER_API_URI_WITH_SLASH + name + "/search")
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(3);
    }
}