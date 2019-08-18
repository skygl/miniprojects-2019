package com.wootecobook.turkey.login.controller.api;

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

    private LoginService loginService;

    public LoginApiController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest, HttpSession httpSession) {
        httpSession.setAttribute(USER_SESSION_KEY, loginService.login(loginRequest));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity logout(UserSession userSession, HttpSession httpSession) {
        httpSession.removeAttribute(USER_SESSION_KEY);
        return ResponseEntity.ok().build();
    }

}
