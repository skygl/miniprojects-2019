package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.user.domain.IntroductionRepository;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
class UserCreateServiceTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";

    private final UserCreateService userCreateService;
    private final UserService userService;
    private final IntroductionService introductionService;

    @Autowired
    public UserCreateServiceTest(final UserRepository userRepository, final IntroductionRepository introductionRepository) {
        userService = new UserService(userRepository);
        introductionService = new IntroductionService(introductionRepository, userService);
        userCreateService = new UserCreateService(userService, introductionService);
    }

    @Test
    void 유저_생성() {
        // given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        // when
        UserResponse userResponse = userCreateService.create(userRequest);

        // then
        assertThat(userResponse.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(userResponse.getName()).isEqualTo(VALID_NAME);
        assertDoesNotThrow(() -> introductionService.findByUserId(userResponse.getId()));
    }
}