package com.wootecobook.turkey.search.controller;

import com.wootecobook.turkey.BaseControllerTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchControllerTests extends BaseControllerTests {

    private final String uri = linkTo(SearchController.class).toString();
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void 검색_요청() {
        String email = "deleteerror@delete.del";
        String name = "delete";

        addUser(name, email, VALID_USER_PASSWORD);

        // when & then
        webTestClient.get().uri(uri + "/dpu")
                .cookie(JSESSIONID, logIn(email, VALID_USER_PASSWORD))
                .exchange()
                .expectStatus().isOk();
    }
}