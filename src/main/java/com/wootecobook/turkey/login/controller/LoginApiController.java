package com.wootecobook.turkey.login.controller;

import com.wootecobook.turkey.commons.resolver.LoginUser;
import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.login.service.LoginService;
import com.wootecobook.turkey.login.service.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static com.wootecobook.turkey.commons.resolver.UserSession.USER_SESSION_KEY;

@RestController
public class LoginApiController {

    private final LoginService loginService;

    public LoginApiController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserSession> login(@RequestBody LoginRequest loginRequest, HttpSession httpSession) {
        UserSession userSession = loginService.login(loginRequest);
        httpSession.setAttribute(USER_SESSION_KEY, userSession);
        return ResponseEntity.ok(userSession);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@LoginUser UserSession userSession, HttpSession httpSession) {
        loginService.logout(userSession.getId());
        httpSession.removeAttribute(USER_SESSION_KEY);
        return ResponseEntity.ok().build();
    }

}
