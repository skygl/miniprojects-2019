package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import com.wootecobook.turkey.user.service.exception.NotFoundUserException;
import com.wootecobook.turkey.user.service.exception.SignUpException;
import com.wootecobook.turkey.user.service.exception.UserDeleteException;
import com.wootecobook.turkey.user.service.exception.UserMismatchException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserServiceTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";

    private UserService userService;

    @Autowired
    public UserServiceTest(UserRepository userRepository) {
        userService = new UserService(userRepository);
    }

    @Test
    void 유저_생성() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        UserResponse userResponse = userService.save(userRequest);

        assertThat(userResponse.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(userResponse.getName()).isEqualTo(VALID_NAME);
    }

    @Test
    void 유저_생성_이메일_에러() {
        UserRequest userRequest = UserRequest.builder()
                .email("INVALID_EMAIL")
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        assertThrows(SignUpException.class, () -> userService.save(userRequest));
    }

    @Test
    void 유저_생성_이름_에러() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name("1")
                .password(VALID_PASSWORD)
                .build();

        assertThrows(SignUpException.class, () -> userService.save(userRequest));
    }

    @Test
    void 유저_생성_비밀번호_에러() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password("1")
                .build();

        assertThrows(SignUpException.class, () -> userService.save(userRequest));
    }

    @Test
    void 유저_id로_조회() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        UserResponse userResponse = userService.save(userRequest);

        UserResponse found = userService.findUserResponseById(userResponse.getId());

        assertThat(userResponse.getId()).isEqualTo(found.getId());
        assertThat(userResponse.getEmail()).isEqualTo(found.getEmail());
        assertThat(userResponse.getName()).isEqualTo(found.getName());
    }

    @Test
    void 유저_email로_조회() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        UserResponse userResponse = userService.save(userRequest);

        User found = userService.findByEmail(userResponse.getEmail());

        assertThat(userResponse.getId()).isEqualTo(found.getId());
        assertThat(userResponse.getEmail()).isEqualTo(found.getEmail());
        assertThat(userResponse.getName()).isEqualTo(found.getName());
    }

    @Test
    void 없는_유저_id로_조회() {
        assertThrows(NotFoundUserException.class, () -> userService.findUserResponseById(Long.MAX_VALUE));
    }

    @Test
    void 없는_유저_email로_조회() {
        assertThrows(NotFoundUserException.class, () -> userService.findByEmail("invalid@invalid.invalid"));
    }

    @Test
    void 유저_삭제() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        UserResponse userResponse = userService.save(userRequest);

        assertDoesNotThrow(() -> userService.delete(userResponse.getId(), userResponse.getId()));
    }

    @Test
    void 없는_유저_삭제() {
        assertThrows(UserDeleteException.class, () -> userService.delete(Long.MAX_VALUE, Long.MAX_VALUE));
    }

    @Test
    void null_유저_삭제() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        UserResponse userResponse = userService.save(userRequest);

        assertThrows(UserMismatchException.class, () -> userService.delete(null, userResponse.getId()));
    }

    @Test
    void 다른_id_유저_삭제() {
        assertThrows(UserMismatchException.class, () -> userService.delete(1L, 2L));
    }
}