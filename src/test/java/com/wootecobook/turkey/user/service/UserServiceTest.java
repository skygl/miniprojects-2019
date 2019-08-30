package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import com.wootecobook.turkey.user.service.exception.SignUpException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        //when
        User user = userService.save(userRequest);

        //then
        assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(user.getName()).isEqualTo(VALID_NAME);
    }

    @Test
    void 유저_생성_이메일_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email("INVALID_EMAIL")
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        //when & then
        assertThrows(SignUpException.class, () -> userService.save(userRequest));
    }

    @Test
    void 유저_생성_중복_이메일_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();
        userService.save(userRequest);

        //when & then
        assertThrows(SignUpException.class, () -> userService.save(userRequest));
    }

    @Test
    void 유저_생성_이름_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name("1")
                .password(VALID_PASSWORD)
                .build();

        //when & then
        assertThrows(SignUpException.class, () -> userService.save(userRequest));
    }

    @Test
    void 유저_생성_비밀번호_에러() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password("1")
                .build();

        //when & then
        assertThrows(SignUpException.class, () -> userService.save(userRequest));
    }

    @Test
    void 유저_id로_조회() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        User user = userService.save(userRequest);

        //when
        UserResponse found = userService.findUserResponseById(user.getId());

        //then
        assertThat(user.getId()).isEqualTo(found.getId());
        assertThat(user.getEmail()).isEqualTo(found.getEmail());
        assertThat(user.getName()).isEqualTo(found.getName());
    }

    @Test
    void 유저_email로_조회() {
        //given
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        User user = userService.save(userRequest);

        //when
        User found = userService.findByEmail(user.getEmail());

        //then
        assertThat(user.getId()).isEqualTo(found.getId());
        assertThat(user.getEmail()).isEqualTo(found.getEmail());
        assertThat(user.getName()).isEqualTo(found.getName());
    }

    @Test
    void 없는_유저_id로_조회() {
        //when & then
        assertThrows(EntityNotFoundException.class, () -> userService.findUserResponseById(Long.MAX_VALUE));
    }

    @Test
    void 없는_유저_email로_조회() {
        //when & then
        assertThrows(EntityNotFoundException.class, () -> userService.findByEmail("invalid@invalid.invalid"));
    }

    @Test
    void 자기_자신을_제외한_유저_조회() {
        // given
        Long currentUserId = createUser(VALID_EMAIL, VALID_NAME).getId();
        createUser("abc@abc.abc", "abc");
        createUser("abcd@abc.abc", "abcd");

        // when
        List<UserResponse> userResponses = userService.findAllUsersWithoutCurrentUser(currentUserId);

        // then
        userResponses.forEach(userResponse -> {
            assertThat(userResponse.getId()).isNotEqualTo(currentUserId);
        });
    }

    private User createUser(String email, String name) {
        UserRequest userRequest = UserRequest.builder()
                .email(email)
                .name(name)
                .password(VALID_PASSWORD)
                .build();

        return userService.save(userRequest);
    }
}