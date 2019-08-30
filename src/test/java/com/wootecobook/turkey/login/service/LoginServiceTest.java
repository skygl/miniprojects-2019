package com.wootecobook.turkey.login.service;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.login.service.dto.LoginRequest;
import com.wootecobook.turkey.login.service.exception.LoginFailException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.UserService;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class LoginServiceTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";

    private final LoginService loginService;
    private final UserService userService;

    private Long userId;

    @Autowired
    public LoginServiceTest(final UserRepository userRepository) {
        userService = new UserService(userRepository);
        loginService = new LoginService(userService);
    }

    @BeforeEach
    void setUp() {
        UserRequest userRequest = UserRequest.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        User user = userService.save(userRequest);
        userId = user.getId();
    }

    @Test
    void 로그인() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        //when
        UserSession userSession = loginService.login(loginRequest);

        //then
        assertThat(userSession.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(userSession.getName()).isEqualTo(VALID_NAME);
    }

    @Test
    void 로그인_없는_이메일() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("invalid@invalid.invalid")
                .password(VALID_PASSWORD)
                .build();

        //when & then
        assertThrows(LoginFailException.class, () -> loginService.login(loginRequest));
    }

    @Test
    void 로그인_비밀번호_불일치() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password("invalid")
                .build();

        //when & then
        assertThrows(LoginFailException.class, () -> loginService.login(loginRequest));
    }

    @Test
    void 유저_생성시_로그아웃_상태() {
        // given
        User user = userService.findById(userId);

        // when & then
        assertThat(user.isLogin()).isFalse();
    }

    @Test
    void 로그인할_때_loginAt_갱신() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        User user = userService.findById(userId);
        LocalDateTime firstLoginAt = user.getLoginAt();

        //when
        loginService.login(loginRequest);
        LocalDateTime secondLoginAt = user.getLoginAt();

        //then
        assertThat(firstLoginAt.isBefore(secondLoginAt)).isTrue();
        assertThat(user.isLogin()).isTrue();
    }

    @Test
    void 로그아웃할_때_logoutAt_갱신() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        User user = userService.findById(userId);
        UserSession userSession = loginService.login(loginRequest);
        LocalDateTime firstLogoutAt = user.getLogoutAt();

        //when
        loginService.logout(userSession.getId());
        LocalDateTime secondLogoutAt = user.getLoginAt();

        //then
        assertThat(firstLogoutAt.isBefore(secondLogoutAt)).isTrue();
    }

    @Test
    void 유저가_로그인_상태이면_logoutAt을_갱신하고_로그인() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        UserSession userSession = loginService.login(loginRequest);
        User user = userService.findById(userSession.getId());
        LocalDateTime firstLogoutAt = user.getLogoutAt();

        //when
        userSession = loginService.login(loginRequest);
        user = userService.findById(userSession.getId());
        LocalDateTime secondLogoutAt = user.getLogoutAt();

        //then
        assertThat(firstLogoutAt.isBefore(secondLogoutAt)).isTrue();
    }

    @Test
    void 로그아웃할_때_로그인_상태이면_logoutAt을_갱신한다() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        UserSession userSession = loginService.login(loginRequest);
        User user = userService.findById(userSession.getId());
        LocalDateTime firstLogoutAt = user.getLogoutAt();

        // when
        loginService.logout(userSession.getId());
        user = userService.findById(userSession.getId());
        LocalDateTime secondLogoutAt = user.getLogoutAt();

        // then
        assertThat(firstLogoutAt.isBefore(secondLogoutAt)).isTrue();
    }

    @Test
    void 초기_상태에서_로그아웃할_때_logoutAt을_갱신하지_않는다() {
        // given
        User user = userService.findById(userId);
        LocalDateTime firstLogoutAt = user.getLogoutAt();

        // when
        loginService.logout(userId);
        user = userService.findById(userId);
        LocalDateTime secondLogoutAt = user.getLogoutAt();

        // then
        assertThat(firstLogoutAt.isEqual(secondLogoutAt)).isTrue();
    }

    @Test
    void 로그아웃할_때_로그아웃_상태이면_logoutAt을_갱신하지_않는다() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        UserSession userSession = loginService.login(loginRequest);
        loginService.logout(userSession.getId());
        User user = userService.findById(userSession.getId());
        LocalDateTime firstLogoutAt = user.getLogoutAt();

        // when
        loginService.logout(userSession.getId());
        user = userService.findById(userSession.getId());
        LocalDateTime secondLogoutAt = user.getLogoutAt();

        // then
        assertThat(firstLogoutAt.isEqual(secondLogoutAt)).isTrue();
    }
}