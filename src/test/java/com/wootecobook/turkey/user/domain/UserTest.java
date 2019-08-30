package com.wootecobook.turkey.user.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    private static final String VALID_EMAIL = "email@test.test";
    private static final String VALID_NAME = "name";
    private static final String VALID_PASSWORD = "passWORD1!";

    @Test
    void 비밀번호_일치() {
        //given
        User user = User.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        //when & then
        assertDoesNotThrow(() -> user.matchPassword(VALID_PASSWORD));
    }

    @Test
    void 비밀번호_불일치() {
        //given
        User user = User.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        //when & then
        assertThrows(IllegalArgumentException.class, () -> user.matchPassword("invalid"));
    }

    @Test
    void 이메일_길이_초과() {
        //when & then
        assertThrows(IllegalArgumentException.class, () -> User.builder()
                .email("a@a.aaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build());
    }

    @Test
    void 유저_생성시_로그아웃_상태() {
        //given
        User user = User.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        //then
        assertThat(user.isLogin()).isFalse();
    }

    @Test
    void 로그아웃_시간이_로그인_시간_이전이면_로그인_상태() {
        //given
        User user = User.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        //when
        LocalDateTime localDateTime = LocalDateTime.now();
        user.updateLoginAt(localDateTime);
        user.updateLogoutAt(localDateTime.minusNanos(1L));

        //then
        assertThat(user.isLogin()).isTrue();
    }

    @Test
    void 로그인_시간과_로그아웃_시간이_같으면_로그아웃_상태() {
        //given
        User user = User.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        //when
        LocalDateTime localDateTime = LocalDateTime.now();
        user.updateLoginAt(localDateTime);
        user.updateLogoutAt(localDateTime);

        //then
        assertThat(user.isLogin()).isFalse();
    }

    @Test
    void 로그인_시간이_로그아웃_시간_이전이면_로그아웃_상태() {
        //given
        User user = User.builder()
                .email(VALID_EMAIL)
                .name(VALID_NAME)
                .password(VALID_PASSWORD)
                .build();

        //when
        LocalDateTime localDateTime = LocalDateTime.now();
        user.updateLoginAt(localDateTime.minusNanos(1L));
        user.updateLogoutAt(localDateTime);

        //then
        assertThat(user.isLogin()).isFalse();
    }
}