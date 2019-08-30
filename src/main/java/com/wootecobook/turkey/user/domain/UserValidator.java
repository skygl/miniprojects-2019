package com.wootecobook.turkey.user.domain;

import java.util.regex.Pattern;

public class UserValidator {

    public static final String NAME_PATTERN = "^[^ \\-!@#$%^&*(),.?\\\":{}|<>0-9]{2,10}$";
    public static final String EMAIL_PATTERN = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}$";

    public static final String NAME_CONSTRAINT_MESSAGE = "이름은 2~10자, 숫자나 특수문자가 포함될 수 없습니다.";
    public static final String EMAIL_CONSTRAINT_MESSAGE = "이메일 양식을 지켜주세요.";
    public static final String EMAIL_LENGTH_CONSTRAINT_MESSAGE = "이메일 길이를 지켜주세요.(최대 30자)";
    public static final String PASSWORD_CONSTRAINT_MESSAGE = "비밀번호는 8자 이상, 소문자, 대문자, 숫자, 특수문자의 조합으로 입력하세요.";

    public static final int MAX_EMAIL_LENGTH = 30;

    public static void validateName(String name) {
        if (isEmpty(name) || !Pattern.matches(NAME_PATTERN, name)) {
            throw new IllegalArgumentException(NAME_CONSTRAINT_MESSAGE);
        }
    }

    public static void validateEmail(String email) {
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException(EMAIL_LENGTH_CONSTRAINT_MESSAGE);
        }
        if (isEmpty(email) || !Pattern.matches(EMAIL_PATTERN, email)) {
            throw new IllegalArgumentException(EMAIL_CONSTRAINT_MESSAGE);
        }
    }

    public static void validatePassword(String password) {
        if (isEmpty(password) || !Pattern.matches(PASSWORD_PATTERN, password)) {
            throw new IllegalArgumentException(PASSWORD_CONSTRAINT_MESSAGE);
        }
    }

    private static boolean isEmpty(String text) {
        return (text == null) || ("".equals(text));
    }
}
