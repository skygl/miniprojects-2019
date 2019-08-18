package com.wootecobook.turkey.user.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";

    @Test
    void 비밀번호_일치() {
        User user = User.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        assertDoesNotThrow(() -> user.matchPassword(VALID_PASSWORD));
    }

    @Test
    void 비밀번호_불일치() {
        User user = User.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        assertThrows(IllegalArgumentException.class, () -> user.matchPassword("invalid"));
    }
}