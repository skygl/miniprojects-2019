package com.wootecobook.turkey.login.service;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.login.service.dto.LoginRequest;
import com.wootecobook.turkey.login.service.exception.LoginFailException;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.UserService;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class LoginServiceTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";

    private LoginService loginService;
    private UserService userService;

    @Autowired
    public LoginServiceTest(UserRepository userRepository) {
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

        userService.save(userRequest);
    }

    @Test
    void 로그인() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        UserSession userSession = loginService.login(loginRequest);

        assertThat(userSession.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(userSession.getName()).isEqualTo(VALID_NAME);
    }

    @Test
    void 로그인_없는_이메일() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("invalid@invalid.invalid")
                .password(VALID_PASSWORD)
                .build();

        assertThrows(LoginFailException.class, () -> loginService.login(loginRequest));
    }

    @Test
    void 로그인_비밀번호_불일치() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password("invalid")
                .build();

        assertThrows(LoginFailException.class, () -> loginService.login(loginRequest));
    }
}