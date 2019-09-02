package com.wootecobook.turkey.friend.service.dto;

import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.friend.domain.Friend;
import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class FriendResponse {

    private Long friendId;
    private Long relatedUserId;
    private String relatedUserName;
    private FileFeature profile;
    private boolean isLogin;

    @Builder
    private FriendResponse(Long friendId, Long relatedUserId, String relatedUserName, FileFeature profile, boolean isLogin) {
        this.friendId = friendId;
        this.relatedUserId = relatedUserId;
        this.relatedUserName = relatedUserName;
        this.profile = profile;
        this.isLogin = isLogin;
    }

    public static FriendResponse from(Friend friend, User relatedUser) {
        FriendResponse friendResponse = FriendResponse.builder()
                .friendId(friend.getId())
                .relatedUserId(relatedUser.getId())
                .relatedUserName(relatedUser.getName())
                .isLogin(relatedUser.isLogin())
                .build();

        Optional<UploadFile> maybeProfile = Optional.ofNullable(relatedUser.getProfile());
        maybeProfile.ifPresent(profile -> friendResponse.profile = profile.getFileFeature());

        return friendResponse;
    }
}
