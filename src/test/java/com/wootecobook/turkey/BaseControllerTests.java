package com.wootecobook.turkey;

import com.wootecobook.turkey.login.service.dto.LoginRequest;
import com.wootecobook.turkey.messenger.service.dto.MessengerRequest;
import com.wootecobook.turkey.messenger.service.dto.MessengerRoomResponse;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTests {
    protected static final String DOMAIN = "http://localhost:";
    protected static final String JSESSIONID = "JSESSIONID";
    protected static final String VALID_USER_PASSWORD = "P@ssw0rd";
    protected static final String VALID_USER_EMAIL = "email@gmail.com";
    protected static final String VALID_USER_NAME = "name";
    protected static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON_UTF8;
    protected static final String MESSENGER_API_URI = "/api/messenger";

    private static final String USER_API_URI = "/api/users";
    private static final String USER_API_URI_WITH_SLASH = USER_API_URI + "/";

    protected WebTestClient webTestClient;

    protected final ResponseFieldsSnippet pageResponseSnippets = responseFields(
            fieldWithPath("totalElements").description("총 Post 갯수"),
            fieldWithPath("numberOfElements").description("현재 페이지에서 조회한 Post 갯수"),
            fieldWithPath("totalPages").description("총 페이지 갯수"),
            fieldWithPath("size").description("조회하려는 데이터의 갯수"),
            fieldWithPath("number").description("현재 페이지 번호"),
            fieldWithPath("first").description("첫번째 페이지 여부"),
            fieldWithPath("last").description("마지막 페이지 여부"),
            fieldWithPath("empty").description("현재 페이지 Post 정보 유무 여부")
    );

    protected final ResponseFieldsSnippet badRequestSnippets = responseFields(
            fieldWithPath("message").description("에러 메세지")
    );

    protected Long addUser(String name, String email, String password) {
        UserRequest userRequest = UserRequest.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();

        UserResponse result = webTestClient.post()
                .uri(USER_API_URI)
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        return result.getId();
    }

    protected void deleteUser(Long id, String email, String password) {
        webTestClient.delete()
                .uri(USER_API_URI_WITH_SLASH + id)
                .cookie(JSESSIONID, logIn(email, password))
                .exchange();
    }

    protected String logIn(String email, String password) {
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        return webTestClient.post().uri("/login")
                .contentType(MEDIA_TYPE)
                .accept(MEDIA_TYPE)
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchange()
                .returnResult(String.class)
                .getResponseCookies().get(JSESSIONID).get(0).getValue();
    }

    protected MessengerRoomResponse requestCreateMessengerRoom(String userJSessionId, MessengerRequest messengerRequest) {
        return webTestClient.post().uri(MESSENGER_API_URI)
                .cookie(JSESSIONID, userJSessionId)
                .contentType(MEDIA_TYPE)
                .body(Mono.just(messengerRequest), MessengerRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MessengerRoomResponse.class)
                .returnResult()
                .getResponseBody();
    }
}
