package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.messenger.service.dto.MessengerRequest;
import com.wootecobook.turkey.messenger.service.dto.MessengerRoomResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Java6Assertions.assertThat;

class MessengerApiControllerTest extends BaseControllerTests {

    @LocalServerPort
    private String port;

    private String userJSessionId;
    private Long memberUserId;
    private MessengerRequest messengerRequest;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .build();

        String userEmail = "u1@mail.com";
        addUser("name", userEmail, VALID_USER_PASSWORD);
        userJSessionId = logIn(userEmail, VALID_USER_PASSWORD);

        memberUserId = 1L;
        messengerRequest = new MessengerRequest(new HashSet<>(Arrays.asList(memberUserId)));
    }

    @Test
    void 메신저룸_생성_테스트() {
        MessengerRoomResponse messengerRoomResponse = requestCreateMessengerRoom(userJSessionId, messengerRequest);

        assertThat(messengerRoomResponse.getId()).isPositive();
    }

    @Test
    void 메신저룸_존재시_새로_생성안하고_존재하던_메신저룸_응답_테스트() {
        MessengerRoomResponse messengerRoomResponse1 = requestCreateMessengerRoom(userJSessionId, messengerRequest);
        MessengerRoomResponse messengerRoomResponse2 = requestCreateMessengerRoom(userJSessionId, messengerRequest);
        assertThat(messengerRoomResponse1.getId()).isEqualTo(messengerRoomResponse2.getId());
    }

    @Test
    void 메신저룸에_포함된_유저가_메세지들_조회_성공() {
        Long messengerRoomId = requestCreateMessengerRoom(userJSessionId, messengerRequest).getId();
        webTestClient.get().uri(MESSENGER_API_URI + "/{roomId}", messengerRoomId)
                .cookie(JSESSIONID, userJSessionId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void 메신저룸에_포함되지_않은_유저가_메세지들_조회_실패() {
        Long messengerRoomId = requestCreateMessengerRoom(userJSessionId, messengerRequest).getId();
        addUser("other", "other@mail.com", VALID_USER_PASSWORD);
        String otherJSessionId = logIn("other@mail.com", VALID_USER_PASSWORD);

        webTestClient.get().uri(MESSENGER_API_URI + "/{roomId}", messengerRoomId)
                .cookie(JSESSIONID, otherJSessionId)
                .exchange()
                .expectStatus().isForbidden();
    }

}