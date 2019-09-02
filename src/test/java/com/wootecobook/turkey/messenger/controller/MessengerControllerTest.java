package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.messenger.service.dto.MessengerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.HashSet;

class MessengerControllerTest extends BaseControllerTests {

    private static final String MESSENGER_URI = "/messenger/{roomId}";

    @LocalServerPort
    private String port;

    private String userJSessionId;
    private MessengerRequest messengerRequest;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .build();

        String userEmail = "u1@mail.com";
        addUser("name", userEmail, VALID_USER_PASSWORD);
        userJSessionId = logIn(userEmail, VALID_USER_PASSWORD);

        Long memberUserId = 1L;
        messengerRequest = new MessengerRequest(new HashSet<>(Arrays.asList(memberUserId)));
    }

    @Test
    void 메신저룸에_포함된_유저가_메신저페이지_조회_성공() {
        Long messengerRoomId = requestCreateMessengerRoom(userJSessionId, messengerRequest).getId();
        webTestClient.get().uri(MESSENGER_URI, messengerRoomId)
                .cookie(JSESSIONID, userJSessionId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void 메신저룸에_포함되지_않은_유저가_메신저페이지_조회_실패() {
        Long messengerRoomId = requestCreateMessengerRoom(userJSessionId, messengerRequest).getId();
        addUser("other", "other@mail.com", VALID_USER_PASSWORD);
        String otherJSessionId = logIn("other@mail.com", VALID_USER_PASSWORD);

        webTestClient.get().uri(MESSENGER_URI, messengerRoomId)
                .cookie(JSESSIONID, otherJSessionId)
                .exchange()
                .expectStatus().isForbidden();
    }

}