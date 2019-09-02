package com.wootecobook.turkey.search.controller;

import com.wootecobook.turkey.BaseControllerTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

class SearchControllerTests extends BaseControllerTests {

    private final String uri = linkTo(SearchController.class).toString();

    @LocalServerPort
    private String port;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .build();
    }

    @Test
    void 검색_요청() {
        String email = "deleteerror@delete.del";
        String name = "delete";

        addUser(name, email, VALID_USER_PASSWORD);

        // when & then
        webTestClient.get().uri(uri + "?keyword=dpu")
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk();
    }
}