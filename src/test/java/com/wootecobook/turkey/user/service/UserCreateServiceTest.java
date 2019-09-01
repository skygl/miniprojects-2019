package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.commons.elasticsearch.utils.KoreanJamoParser;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class UserCreateServiceTest {

//    private static final String VALID_EMAIL = "email@test.test";
//    private static final String VALID_NAME = "name";
//    private static final String VALID_PASSWORD = "passWORD1!";
//
//    private final UserCreateService userCreateService;
//    private final UserService userService;
//    private final IntroductionService introductionService;
//    private final ElasticSearchService elasticSearchService;
//
//    public UserCreateServiceTest(final UserCreateService userCreateService, final UserService userService, final IntroductionService introductionService, final ElasticSearchService elasticSearchService) {
//        this.userCreateService = userCreateService;
//        this.userService = userService;
//        this.introductionService = introductionService;
//        this.elasticSearchService = elasticSearchService;
//    }
//
//    @Test
//    void 유저_생성() {
//        // given
//        UserRequest userRequest = UserRequest.builder()
//                .email(VALID_EMAIL)
//                .name(VALID_NAME)
//                .password(VALID_PASSWORD)
//                .build();
//
//        // when
//        UserResponse userResponse = userCreateService.create(userRequest);
//
//        // then
//        assertThat(userResponse.getEmail()).isEqualTo(VALID_EMAIL);
//        assertThat(userResponse.getName()).isEqualTo(VALID_NAME);
//        assertDoesNotThrow(() -> introductionService.findByUserId(userResponse.getId()));
//    }
}