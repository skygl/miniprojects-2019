package com.wootecobook.turkey.friend.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.friend.service.FriendAskService;
import com.wootecobook.turkey.friend.service.dto.FriendAskCreate;
import com.wootecobook.turkey.friend.service.dto.FriendAskResponse;
import com.wootecobook.turkey.friend.service.dto.FriendCreate;
import com.wootecobook.turkey.friend.service.dto.FriendResponse;
import com.wootecobook.turkey.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.wootecobook.turkey.friend.service.FriendService.ALREADY_FRIEND_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

class FriendApiControllerTests extends BaseControllerTests {


    private static final String FRIEND_ASK_API_URI = "/api/friends/asks";
    private static final String FRIEND_API_URI = "/api/friends";
    private static final String SENDER_NAME = "sender";
    private static final String SENDER_EMAIL = "sender@abc.abc";
    private static final String RECEIVER_NAME = "receiver";
    private static final String RECEIVER_EMAIL = "receiver@abc.abc";
    private static final String MISMATCH_NAME = "mismatch";
    private static final String MISMATCH_EMAIL = "mismatch@abc.abc";

    @LocalServerPort
    private String port;

    private Long senderId;
    private Long receiverId;

    private final RequestFieldsSnippet friendAskRequestFieldsSnippet = requestFields(
            fieldWithPath("receiverId").description("요청을 받은 유저의 고유 식별자")
    );

    private final FieldDescriptor[] friendAskResponseFields = {
            fieldWithPath("friendAskId").description("친구 요청 고유 식별자"),
            fieldWithPath("senderId").description("친구 요청 보낸 유저의 고유 식별자"),
            fieldWithPath("senderName").description("친구 요청 보낸 유저의 이름"),
            fieldWithPath("receiverId").description("친구 요청 받은 유저의 고유 식별자"),
            fieldWithPath("receiverName").description("친구 요청 받은 유저의 이름")
    };

    private final FieldDescriptor[] friendResponseFields = {
            fieldWithPath("friendId").description("친구 정보 고유 식별자"),
            fieldWithPath("relatedUserId").description("친구 유저의 고유 식별자"),
            fieldWithPath("relatedUserName").description("친구 유저의 이름"),
            fieldWithPath("login").description("친구의 접속 여부")
    };

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .filter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        senderId = addUser(SENDER_NAME, SENDER_EMAIL, VALID_USER_PASSWORD);
        receiverId = addUser(RECEIVER_NAME, RECEIVER_EMAIL, VALID_USER_PASSWORD);
    }


    @Test
    void 친구_요청_테스트() {
        // given
        final FriendAskCreate friendAskCreate = new FriendAskCreate(receiverId);

        // when & then
        webTestClient.post().uri(FRIEND_ASK_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(SENDER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendAskCreate), FriendAskCreate.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("friend/ask/201/create",
                        friendAskRequestFieldsSnippet,
                        responseFields(
                                fieldWithPath("friendAskId").description("친구 요청 고유 식별자"),
                                fieldWithPath("senderId").description("친구 요청 보낸 유저의 고유 식별자"),
                                fieldWithPath("senderName").description("친구 요청 보낸 유저의 이름"),
                                fieldWithPath("receiverId").description("친구 요청 받은 유저의 고유 식별자"),
                                fieldWithPath("receiverName").description("친구 요청 받은 유저의 이름")
                        )
                ));
    }

    @Test
    void 이미_친구인_경우_친구_요청_실패() {
        // given
        Long friendAskId = createFriendAsk();
        FriendCreate friendCreate = FriendCreate.builder()
                .friendAskId(friendAskId)
                .build();
        createFriend(friendCreate);
        FriendAskCreate friendAskCreate = new FriendAskCreate(receiverId);

        // when & then
        ErrorMessage errorMessage = webTestClient.post().uri(FRIEND_ASK_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(SENDER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendAskCreate), FriendAskCreate.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .consumeWith(document("friend/ask/400/create/already-friend",
                        friendAskRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        assertThat(errorMessage.getMessage()).isEqualTo(ALREADY_FRIEND_MESSAGE);
    }

    @Test
    void 친구_요청_받는_유저_ID가_없는_경우() {
        // given
        final FriendAskCreate friendAskCreate = new FriendAskCreate(Long.MAX_VALUE);

        // when
        ErrorMessage errorMessage = webTestClient.post().uri(FRIEND_ASK_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(SENDER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendAskCreate), FriendAskCreate.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .consumeWith(document("friend/ask/400/create/none",
                        friendAskRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(UserService.NOT_FOUND_MESSAGE).isEqualTo(errorMessage.getMessage());
    }

    @Test
    void 친구_요청_조회() {
        // given
        createFriendAsk();

        // when & then
        List<FriendAskResponse> friendAskResponse = webTestClient.get().uri(FRIEND_ASK_API_URI)
                .cookie(JSESSIONID, logIn(RECEIVER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FriendAskResponse.class)
                .consumeWith(document("friend/ask/200/read",
                        responseFields(
                                fieldWithPath("[]").description("친구 요청 목록")).andWithPrefix("[].", friendAskResponseFields)
                        )
                )
                .returnResult()
                .getResponseBody();

        FriendAskResponse sender = friendAskResponse.get(0);
        assertThat(friendAskResponse.size()).isEqualTo(1);
        assertThat(sender.getSenderName()).isEqualTo(SENDER_NAME);
        assertThat(sender.getSenderId()).isEqualTo(senderId);
    }

    @Test
    void 이미_친구_요청을_보낸_경우() {
        // given
        final FriendAskCreate friendAskCreate = new FriendAskCreate(receiverId);

        webTestClient.post().uri(FRIEND_ASK_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(SENDER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendAskCreate), FriendAskCreate.class)
                .exchange()
                .expectStatus().isCreated();

        //when
        ErrorMessage errorMessage = webTestClient.post().uri(FRIEND_ASK_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(SENDER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendAskCreate), FriendAskCreate.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("friend/ask/400/already-exist",
                        friendAskRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).isEqualTo(FriendAskService.ALREADY_FRIEND_ASK_EXIST_MESSAGE);
    }

    @Test
    void 상대방이_친구_요청을_보낸_경우() {
        // given
        final FriendAskCreate friendAskCreate = new FriendAskCreate(receiverId);
        final FriendAskCreate friendAskCreateReverse = new FriendAskCreate(senderId);

        webTestClient.post().uri(FRIEND_ASK_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(SENDER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendAskCreate), FriendAskCreate.class)
                .exchange()
                .expectStatus().isCreated();

        //when
        ErrorMessage errorMessage = webTestClient.post().uri(FRIEND_ASK_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(RECEIVER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendAskCreateReverse), FriendAskCreate.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MEDIA_TYPE)
                .expectBody(ErrorMessage.class)
                .consumeWith(document("friend/ask/400/already-receive",
                        friendAskRequestFieldsSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).isEqualTo(FriendAskService.ALREADY_OTHER_FRIEND_ASK_EXIST_MESSAGE);
    }

    @Test
    void 친구_요청_수락_후_친구_목록에_등록됐는지_확인() {
        // given
        Long friendAskId = createFriendAsk();
        FriendCreate friendCreate = FriendCreate.builder().friendAskId(friendAskId).build();

        // when
        webTestClient.post().uri(FRIEND_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(RECEIVER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendCreate), FriendCreate.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("friend/201/create",
                        requestFields(
                                fieldWithPath("friendAskId").description("친구 요청 정보의 고유 식별자")
                        )
                ));

        List<FriendResponse> friendResponses = webTestClient.get().uri(FRIEND_API_URI)
                .cookie(JSESSIONID, logIn(RECEIVER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FriendResponse.class)
                .consumeWith(document("friend/200/read",
                        relaxedResponseFields(
                                fieldWithPath("[]").description("친구 정보")).andWithPrefix("[].", friendResponseFields)
                        )
                )
                .returnResult()
                .getResponseBody();

        boolean isFriend = friendResponses.stream().anyMatch(friendResponse -> friendResponse.getRelatedUserId().equals(senderId));

        //then
        assertThat(isFriend).isTrue();
    }

    @Test
    void sender_친구_요청_삭제() {
        //given
        Long friendAskId = createFriendAsk();

        //when & then
        webTestClient.delete()
                .uri(FRIEND_ASK_API_URI + "/{id}", friendAskId)
                .cookie(JSESSIONID, logIn(SENDER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("friend/ask/204/delete",
                        pathParameters(
                                parameterWithName("id").description("친구 요청 고유 식별자")
                        )
                ));
    }

    @Test
    void receiver_친구_요청_삭제() {
        //given
        Long friendAskId = createFriendAsk();

        //when & then
        webTestClient.delete()
                .uri(FRIEND_ASK_API_URI + "/" + friendAskId)
                .cookie(JSESSIONID, logIn(RECEIVER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void 잘못된_유저_친구_요청_삭제() {
        //given
        Long friendAskId = createFriendAsk();
        addUser(MISMATCH_NAME, MISMATCH_EMAIL, VALID_USER_PASSWORD);

        //when
        ErrorMessage errorMessage = webTestClient.delete()
                .uri(FRIEND_ASK_API_URI + "/{id}", friendAskId)
                .cookie(JSESSIONID, logIn(MISMATCH_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .consumeWith(document("friend/ask/400/delete",
                        pathParameters(
                                parameterWithName("id").description("친구 요청 고유 식별자")
                        ),
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        //then
        assertThat(errorMessage.getMessage()).isEqualTo(FriendAskService.MISMATCHED_USER_MESSAGE);
    }

    @Test
    void 친구_삭제() {
        // given
        Long friendAskId = createFriendAsk();
        FriendCreate friendCreate = FriendCreate.builder()
                .friendAskId(friendAskId)
                .build();

        Long friendId = createFriend(friendCreate);

        //when & then
        webTestClient.delete().uri(FRIEND_API_URI + "/{id}", friendId)
                .cookie(JSESSIONID, logIn(RECEIVER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("friend/204/delete",
                        pathParameters(
                                parameterWithName("id").description("삭제할 친구 정보의 고유 식별자")
                        )
                ));
    }

    private Long createFriendAsk() {
        final FriendAskCreate friendAskCreate = new FriendAskCreate(receiverId);
        FriendAskResponse response = webTestClient.post()
                .uri(FRIEND_ASK_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(SENDER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendAskCreate), FriendAskCreate.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FriendAskResponse.class)
                .returnResult()
                .getResponseBody();
        return response.getFriendAskId();
    }

    private Long createFriend(FriendCreate friendCreate) {
        webTestClient.post().uri(FRIEND_API_URI)
                .contentType(MEDIA_TYPE)
                .cookie(JSESSIONID, logIn(RECEIVER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(friendCreate), FriendCreate.class)
                .exchange()
                .expectStatus().isCreated();

        List<FriendResponse> friendResponses = webTestClient.get().uri(FRIEND_API_URI)
                .cookie(JSESSIONID, logIn(RECEIVER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FriendResponse.class)
                .returnResult()
                .getResponseBody();

        return friendResponses.stream()
                .filter(friendResponse -> friendResponse.getRelatedUserId().equals(senderId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getFriendId();
    }

    @AfterEach
    void tearDown() {
        deleteUser(senderId, SENDER_EMAIL, VALID_USER_PASSWORD);
        deleteUser(receiverId, RECEIVER_EMAIL, VALID_USER_PASSWORD);
    }
}