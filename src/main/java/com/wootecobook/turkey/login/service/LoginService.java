package com.wootecobook.turkey.login.service;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.login.service.dto.LoginRequest;
import com.wootecobook.turkey.login.service.exception.LoginFailException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class LoginService {

    private final UserService userService;

    public LoginService(final UserService userService) {
        this.userService = userService;
    }

    public UserSession login(final LoginRequest loginRequest) {
        try {
            User user = userService.findByEmail(loginRequest.getEmail());
            user.matchPassword(loginRequest.getPassword());
            checkLoginAndUpdateLogoutAt(user);
            user.updateLoginAt(LocalDateTime.now());
            return UserSession.from(user);
        } catch (Exception e) {
            throw new LoginFailException(e.getMessage());
        }
    }

    public void logout(final Long userId) {
        User user = userService.findById(userId);
        checkLoginAndUpdateLogoutAt(user);
    }

    private void checkLoginAndUpdateLogoutAt(User user) {
        if (user.isLogin()) {
            user.updateLogoutAt(LocalDateTime.now());
        }
    }
}
