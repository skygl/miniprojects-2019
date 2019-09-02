package com.wootecobook.turkey.messenger.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.messenger.service.dto.MessageRequest;
import com.wootecobook.turkey.messenger.service.dto.MessageResponse;
import com.wootecobook.turkey.messenger.service.dto.MessengerRequest;
import com.wootecobook.turkey.messenger.service.dto.MessengerRoomResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebSocketMessengerControllerTest extends BaseControllerTests {

    public static final String SUBSCRIBE_MESSENGER_ENDPOINT = "/topic/messenger/";
    public static final String SEND_MESSAGE_ENDPOINT = "/app/messenger/";

    @LocalServerPort
    private String port;

    private String userJSessionId;
    private Long userId;
    private Long roomId;
    private MessengerRequest messengerRequest;
    private MessageRequest messageRequest;
    private WebSocketStompClient stompClient;
    private CompletableFuture<MessageResponse> completableFuture;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .build();

        String userEmail = "messenger@mail.com";
        userId = userId == null ? addUser("name", userEmail, VALID_USER_PASSWORD) : userId;
        userJSessionId = logIn(userEmail, VALID_USER_PASSWORD);

        Long memberUserId = 1L;
        messengerRequest = new MessengerRequest(new HashSet<>(Arrays.asList(memberUserId)));

        roomId = getMessengerRoomId();
        messageRequest = new MessageRequest("test message");

        completableFuture = new CompletableFuture<>();
        stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void 메세지_전송_성공_테스트() throws Exception {
        //given
        WebSocketHttpHeaders headers = createWebSocketHeaderWithJSessionId(userJSessionId);
        StompSession stompSession = getStompSession(headers);
        stompSession.subscribe(SUBSCRIBE_MESSENGER_ENDPOINT + roomId, getStompFrameHandler());
        //when
        stompSession.send(SEND_MESSAGE_ENDPOINT + roomId, messageRequest);
        MessageResponse messageResponse = completableFuture.get(1, SECONDS);
        //then
        assertThat(messageResponse.getContent()).isEqualTo(messageRequest.getMessage());
        assertThat(messageResponse.getSender().getId()).isEqualTo(userId);
    }

    @Test
    public void 메신저룸에_없는_유저_메세지_전송_실패_테스트() throws Exception {
        //given
        String otherEmail = "other@mail.com";
        addUser("name", otherEmail, VALID_USER_PASSWORD);
        String otherJSessionId = logIn(otherEmail, VALID_USER_PASSWORD);

        WebSocketHttpHeaders headers = createWebSocketHeaderWithJSessionId(otherJSessionId);
        StompSession stompSession = getStompSession(headers);
        stompSession.subscribe(SUBSCRIBE_MESSENGER_ENDPOINT + roomId, getStompFrameHandler());

        //when & then
        stompSession.send(SEND_MESSAGE_ENDPOINT + roomId, messageRequest);
        assertThrows(TimeoutException.class, () -> completableFuture.get(1, SECONDS));
    }

    private Long getMessengerRoomId() {
        MessengerRoomResponse messengerRoomResponse = requestCreateMessengerRoom(userJSessionId, messengerRequest);
        return messengerRoomResponse.getId();
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private WebSocketHttpHeaders createWebSocketHeaderWithJSessionId(String otherJSessionId) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.set(HttpHeaders.COOKIE, JSESSIONID + " = " + otherJSessionId);
        return headers;
    }

    private StompSession getStompSession(WebSocketHttpHeaders headers) throws InterruptedException, java.util.concurrent.ExecutionException, TimeoutException {
        return stompClient
                .connect("ws://localhost:" + port + "/websocket", headers, new StompSessionHandlerAdapter() {})
                .get(5, SECONDS);
    }

    private StompFrameHandler getStompFrameHandler() {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete((MessageResponse) payload);
            }
        };
    }
}