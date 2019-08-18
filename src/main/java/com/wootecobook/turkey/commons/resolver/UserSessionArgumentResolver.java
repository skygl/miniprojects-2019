package com.wootecobook.turkey.commons.resolver;

import com.wootecobook.turkey.commons.exception.NotLoginException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.wootecobook.turkey.commons.resolver.UserSession.USER_SESSION_KEY;

public class UserSessionArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return UserSession.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public UserSession resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                       NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession httpSession = httpRequest.getSession();
        UserSession user = (UserSession) httpSession.getAttribute(USER_SESSION_KEY);
        if (user == null) {
            throw new NotLoginException();
        }
        return user;
    }

}
