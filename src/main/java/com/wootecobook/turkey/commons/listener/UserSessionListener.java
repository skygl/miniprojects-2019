package com.wootecobook.turkey.commons.listener;

import com.wootecobook.turkey.commons.resolver.UserSession;
import com.wootecobook.turkey.login.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import static com.wootecobook.turkey.commons.resolver.UserSession.USER_SESSION_KEY;

@WebListener
public class UserSessionListener implements HttpSessionListener {

    private static final Logger log = LoggerFactory.getLogger(UserSessionListener.class);

    private final LoginService loginService;

    public UserSessionListener(final LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession httpSession = se.getSession();
        log.info("Session Created, session ID : {}, time-out : {}",
                httpSession.getId(), httpSession.getMaxInactiveInterval());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession httpSession = se.getSession();
        log.info("Session Destroyed, session ID : {}, user : {}",
                httpSession.getId(), httpSession.getAttribute(USER_SESSION_KEY));
        logout(httpSession);
    }

    private void logout(HttpSession httpSession) {
        UserSession userSession = (UserSession) httpSession.getAttribute(USER_SESSION_KEY);
        if (userSession != null) {
            loginService.logout(userSession.getId());
        }
    }
}
