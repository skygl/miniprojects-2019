package com.wootecobook.turkey.user.controller;

import com.wootecobook.turkey.BaseControllerTests;
import com.wootecobook.turkey.commons.ErrorMessage;
import com.wootecobook.turkey.user.service.dto.IntroductionRequest;
import com.wootecobook.turkey.user.service.dto.IntroductionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.wootecobook.turkey.user.service.IntroductionService.MISMATCH_USER_MESSAGE;
import static com.wootecobook.turkey.user.service.IntroductionService.NOT_FOUND_INTRODUCTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

class IntroductionApiControllerTests extends BaseControllerTests {

    private static final String INTRODUCTION_URI = "/api/users/{userId}/introduction";
    private static final String HOMETOWN = "HOMETOWN";
    private static final String CURRENT_CITY = "CURRENT_CITY";
    private static final String EDUCATION = "EDUCATION";
    private static final String COMPANY = "COMPANY";

    @LocalServerPort
    private String port;

    private final PathParametersSnippet userIdPathParametersSnippet = pathParameters(
            parameterWithName("userId").description("해당 소개 정보를 가지는 유저의 id")
    );

    private final ResponseFieldsSnippet introductionResponseFieldsSnippet = responseFields(
            fieldWithPath("id").description("소개의 고유 식별자"),
            fieldWithPath("currentCity").type(JsonFieldType.STRING).optional().description("유저의 거주 도시"),
            fieldWithPath("hometown").type(JsonFieldType.STRING).optional().description("유저의 출신지"),
            fieldWithPath("company").type(JsonFieldType.STRING).optional().description("유저의 현재 회사"),
            fieldWithPath("education").type(JsonFieldType.STRING).optional().description("유저의 학력"),
            fieldWithPath("userId").description("해당 유저의 고유 식별자")
    );

    private Long userId;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(DOMAIN + port)
                .filter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        userId = addUser(VALID_USER_NAME, VALID_USER_EMAIL, VALID_USER_PASSWORD);
    }

    @Test
    void 소개_조회_초기값() {
        // when
        IntroductionResponse response = webTestClient.get()
                .uri(INTRODUCTION_URI, userId)
                .cookie(JSESSIONID, logIn(VALID_USER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectBody(IntroductionResponse.class)
                .consumeWith(document("introduction/200/create",
                        userIdPathParametersSnippet,
                        introductionResponseFieldsSnippet
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getCompany()).isNull();
        assertThat(response.getHometown()).isNull();
        assertThat(response.getEducation()).isNull();
        assertThat(response.getCurrentCity()).isNull();
    }

    @Test
    void 없는_유저_소개_조회_에러() {
        // when
        ErrorMessage errorMessage = webTestClient.get()
                .uri(INTRODUCTION_URI, Long.MAX_VALUE)
                .cookie(JSESSIONID, logIn(VALID_USER_EMAIL, VALID_USER_PASSWORD))
                .exchange()
                .expectBody(ErrorMessage.class)
                .consumeWith(document("introduction/400/create",
                        userIdPathParametersSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(errorMessage.getMessage()).isEqualTo(NOT_FOUND_INTRODUCTION_MESSAGE);
    }

    @Test
    void 소개_수정() {
        // given
        IntroductionRequest introductionRequest = IntroductionRequest.builder()
                .hometown(HOMETOWN)
                .education(EDUCATION)
                .currentCity(CURRENT_CITY)
                .company(COMPANY)
                .userId(userId)
                .build();

        // when
        IntroductionResponse response = webTestClient.put()
                .uri(INTRODUCTION_URI, userId)
                .cookie(JSESSIONID, logIn(VALID_USER_EMAIL, VALID_USER_PASSWORD))
                .body(Mono.just(introductionRequest), IntroductionRequest.class)
                .exchange()
                .expectBody(IntroductionResponse.class)
                .consumeWith(document("introduction/200/update",
                        userIdPathParametersSnippet,
                        introductionResponseFieldsSnippet
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getCompany()).isEqualTo(COMPANY);
        assertThat(response.getHometown()).isEqualTo(HOMETOWN);
        assertThat(response.getEducation()).isEqualTo(EDUCATION);
        assertThat(response.getCurrentCity()).isEqualTo(CURRENT_CITY);
    }

    @Test
    void 타인이_소개_수정_에러() {
        // given
        Long anotherUserId = addUser(VALID_USER_NAME, VALID_USER_EMAIL + "a", VALID_USER_PASSWORD);
        IntroductionRequest introductionRequest = IntroductionRequest.builder()
                .hometown(HOMETOWN)
                .education(EDUCATION)
                .currentCity(CURRENT_CITY)
                .company(COMPANY)
                .userId(userId)
                .build();

        // when
        ErrorMessage errorMessage = webTestClient.put()
                .uri(INTRODUCTION_URI, userId)
                .cookie(JSESSIONID, logIn(VALID_USER_EMAIL + "a", VALID_USER_PASSWORD))
                .body(Mono.just(introductionRequest), IntroductionRequest.class)
                .exchange()
                .expectBody(ErrorMessage.class)
                .consumeWith(document("introduction/400/update",
                        userIdPathParametersSnippet,
                        badRequestSnippets
                ))
                .returnResult()
                .getResponseBody();

        // then
        assertThat(errorMessage.getMessage()).isEqualTo(MISMATCH_USER_MESSAGE);

        deleteUser(anotherUserId, VALID_USER_EMAIL + "a", VALID_USER_PASSWORD);
    }

    @AfterEach
    void tearDown() {
        deleteUser(userId, VALID_USER_EMAIL, VALID_USER_PASSWORD);
    }
}