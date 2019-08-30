package com.wootecobook.turkey.user.service.dto;

import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Getter
@ToString
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private FileFeature profile;
    private boolean isLogin;

    @Builder
    public UserResponse(final Long id, final String email, final String name, final FileFeature profile, final boolean isLogin) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.profile = profile;
        this.isLogin = isLogin;
    }

    public static UserResponse from(User user) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .isLogin(user.isLogin())
                .build();

        Optional<UploadFile> maybeProfile = getFileFeatureOfProfile(user.getProfile());
        maybeProfile.ifPresent(profile -> userResponse.profile = profile.getFileFeature());

        return userResponse;
    }

    private static Optional<UploadFile> getFileFeatureOfProfile(final UploadFile profile) {
        return Optional.ofNullable(profile);
    }
}
