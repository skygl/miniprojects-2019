package com.wootecobook.turkey.user.domain;

import com.wootecobook.turkey.commons.domain.UpdatableEntity;
import com.wootecobook.turkey.file.domain.UploadFile;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class User extends UpdatableEntity {

    public static final String INVALID_PASSWORD_MESSAGE = "비밀번호가 틀렸습니다.";

    @Email
    @Column(unique = true)
    private String email;

    @Column
    private String name;

    @Column
    private String password;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_PROFILE_TO_USER"))
    private UploadFile profile;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_COVER_TO_USER"))
    private UploadFile cover;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime loginAt;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime logoutAt;

    @Builder
    public User(String email, String name, String password) {
        UserValidator.validateEmail(email);
        UserValidator.validateName(name);
        UserValidator.validatePassword(password);

        this.email = email;
        this.name = name;
        this.password = password;
        this.loginAt = LocalDateTime.now();
        this.logoutAt = LocalDateTime.now();
    }

    public void matchPassword(String password) {
        if (!this.password.equals(password)) {
            throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);
        }
    }

    public boolean matchId(Long id) {
        return (id != null) && (id.equals(getId()));
    }

    public void updateLoginAt(LocalDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public void updateLogoutAt(LocalDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }

    public boolean isLogin() {
        return loginAt.isAfter(logoutAt);
    }
    public void uploadProfile(UploadFile profile) {
        this.profile = profile;
    }

    public void uploadCover(UploadFile cover) {
        this.cover = cover;
    }

}
