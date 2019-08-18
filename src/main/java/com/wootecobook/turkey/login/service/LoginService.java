package com.wootecobook.turkey.login.service;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.login.service.dto.LoginRequest;
import com.wootecobook.turkey.login.service.exception.LoginFailException;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginService {

    private UserService userService;

    public LoginService(UserService userService) {
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public UserSession login(LoginRequest loginRequest) {
        try {
            User user = userService.findByEmail(loginRequest.getEmail());
            user.matchPassword(loginRequest.getPassword());
            return UserSession.from(user);
        } catch (Exception e) {
            throw new LoginFailException(e.getMessage());
        }
    }
}
