package com.wootecobook.turkey.commons.listener;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.login.service.LoginService;
import com.wootecobook.turkey.login.service.dto.LoginRequest;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.UserService;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import static com.wootecobook.turkey.commons.resolver.UserSession.USER_SESSION_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class UserSessionListenerTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";

    private final UserService userService;
    private final LoginService loginService;
    private final UserSessionListener userSessionListener;

    @Mock
    private HttpSessionEvent httpSessionEvent;

    private Long userId;

    @Autowired
    public UserSessionListenerTest(final UserRepository userRepository) {
        this.userService = new UserService(userRepository);
        this.loginService = new LoginService(userService);
        this.userSessionListener = new UserSessionListener(loginService);
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
    void 세션_Destroyed될_때_로그인_상태이면_로그아웃() {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();
        UserSession userSession = loginService.login(loginRequest);

        HttpSession httpSession = new MockHttpSession();
        httpSession.setAttribute(USER_SESSION_KEY, userSession);
        when(httpSessionEvent.getSession()).thenReturn(httpSession);

        //when
        userSessionListener.sessionDestroyed(httpSessionEvent);

        //then
        verify(httpSessionEvent).getSession();
        User user = userService.findById(userId);
        assertThat(user.isLogin()).isFalse();
    }
}
