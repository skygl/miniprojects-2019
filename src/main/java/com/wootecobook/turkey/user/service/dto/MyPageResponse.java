package com.wootecobook.turkey.user.service.dto;

import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.user.domain.User;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageResponse {

    private UserResponse user;
    private FileFeature cover;
    private List<FileFeature> images;
    private List<UserResponse> friends;

    @Builder
    private MyPageResponse(final UserResponse user, final FileFeature cover, final List<FileFeature> images, final List<UserResponse> friends) {
        this.user = user;
        this.cover = cover;
        this.images = images;
        this.friends = friends;
    }

    public static MyPageResponse from(User user, List<FileFeature> images, List<UserResponse> friends) {
        MyPageResponse myPageResponse = MyPageResponse.builder()
                .user(UserResponse.from(user))
                .images(images)
                .friends(friends)
                .build();

        Optional<UploadFile> maybeCover = getFileFeatureOfCover(user.getCover());
        maybeCover.ifPresent(cover -> myPageResponse.cover = cover.getFileFeature());

        return myPageResponse;
    }

    private static Optional<UploadFile> getFileFeatureOfCover(UploadFile cover) {
        return Optional.ofNullable(cover);
    }
}