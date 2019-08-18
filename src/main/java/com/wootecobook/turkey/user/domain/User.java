package com.wootecobook.turkey.user.domain;

import com.wootecobook.turkey.commons.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    public static final String INVALID_PASSWORD_MESSAGE = "비밀번호가 틀렸습니다.";

    @Email
    @Column(unique = true)
    private String email;

    @Column
    private String name;

    @Column
    private String password;

    @Builder
    public User(String email, String name, String password) {
        UserValidator.validateEmail(email);
        UserValidator.validateName(name);
        UserValidator.validatePassword(password);

        this.email = email;
        this.name = name;
        this.password = password;
    }

    public void matchPassword(String password) {
        if (!this.password.equals(password)) {
            throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);
        }
    }

    public boolean matchId(Long id) {
        return (id != null) && (id.equals(getId()));
    }

}
