package com.wootecobook.turkey.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void 회원가입_폼으로_이동() {
        //when & then
        webTestClient.get()
                .uri("/users/form")
                .exchange()
                .expectStatus().isOk();
    }
}