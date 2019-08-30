package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.user.domain.Introduction;
import com.wootecobook.turkey.user.domain.IntroductionRepository;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.dto.IntroductionRequest;
import com.wootecobook.turkey.user.service.dto.IntroductionResponse;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.exception.UserMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class IntroductionServiceTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";
    private static final String COMPANY = "company";
    private static final String EDUCATION = "education";
    private static final String HOMETOWN = "hometown";
    private static final String CURRENT_CITY = "current city";

    private final IntroductionService introductionService;
    private final UserService userService;

    private User user;

    @Autowired
    public IntroductionServiceTest(UserRepository userRepository, IntroductionRepository introductionRepository) {
        userService = new UserService(userRepository);
        introductionService = new IntroductionService(introductionRepository, userService);
    }

    @BeforeEach
    void setUp() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        user = userService.save(userRequest);
    }

    @Test
    void Introduction_생성() {
        // when
        Introduction introduction = introductionService.save(user.getId());

        // then
        assertThat(introduction.getUserId()).isEqualTo(user.getId());
        assertThat(introduction.getCompany()).isEqualTo(null);
        assertThat(introduction.getCurrentCity()).isEqualTo(null);
        assertThat(introduction.getEducation()).isEqualTo(null);
        assertThat(introduction.getHometown()).isEqualTo(null);
    }

    @Test
    void 유저가_없을_시_Introduction_생성_에러() {
        // when&then
        assertThrows(EntityNotFoundException.class, () -> introductionService.save(Long.MAX_VALUE));
    }

    @Test
    void Introduction_수정() {
        // given
        introductionService.save(user.getId());
        IntroductionRequest introductionRequest = IntroductionRequest.builder()
                .company(COMPANY)
                .currentCity(CURRENT_CITY)
                .education(EDUCATION)
                .hometown(HOMETOWN)
                .userId(user.getId())
                .build();

        // when
        IntroductionResponse introductionResponse = introductionService.update(introductionRequest, user.getId());

        // then
        assertThat(introductionResponse.getCompany()).isEqualTo(COMPANY);
        assertThat(introductionResponse.getCurrentCity()).isEqualTo(CURRENT_CITY);
        assertThat(introductionResponse.getEducation()).isEqualTo(EDUCATION);
        assertThat(introductionResponse.getHometown()).isEqualTo(HOMETOWN);
    }

    @Test
    void 세션_아이디가_NULL일경우_수정_에러() {
        // given
        introductionService.save(user.getId());
        IntroductionRequest introductionRequest = IntroductionRequest.builder()
                .company(COMPANY)
                .currentCity(CURRENT_CITY)
                .education(EDUCATION)
                .hometown(HOMETOWN)
                .userId(user.getId())
                .build();

        // when&then
        assertThrows(UserMismatchException.class,
                () -> introductionService.update(introductionRequest, null));
    }

    @Test
    void IntroductionRequest의_유저_아이디가_NULL일경우_수정_에러() {
        // given
        introductionService.save(user.getId());
        IntroductionRequest introductionRequest = IntroductionRequest.builder()
                .company(COMPANY)
                .currentCity(CURRENT_CITY)
                .education(EDUCATION)
                .hometown(HOMETOWN)
                .build();

        // when&then
        assertThrows(UserMismatchException.class,
                () -> introductionService.update(introductionRequest, user.getId()));
    }

    @Test
    void IntroductionRequest의_유저_아이디가_세션_아이디와_다를경우_수정_에러() {
        // given
        introductionService.save(user.getId());
        IntroductionRequest introductionRequest = IntroductionRequest.builder()
                .company(COMPANY)
                .currentCity(CURRENT_CITY)
                .education(EDUCATION)
                .hometown(HOMETOWN)
                .userId(user.getId() + 1)
                .build();

        // when&then
        assertThrows(UserMismatchException.class,
                () -> introductionService.update(introductionRequest, user.getId()));
    }

    @Test
    void 소개_삭제() {
        // given
        introductionService.save(user.getId());

        // when&then
        assertDoesNotThrow(() -> introductionService.delete(user.getId()));
    }
}