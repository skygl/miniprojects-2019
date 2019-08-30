package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.file.domain.UploadFile;

import static com.wootecobook.turkey.commons.aws.S3Connector.COVER_SAVE_DIRECTORY;
import static com.wootecobook.turkey.commons.aws.S3Connector.PROFILE_SAVE_DIRECTORY;

public enum ImageType {

    PROFILE((uploadFileService, image, user) -> {
        UploadFile profile = uploadFileService.save(image, PROFILE_SAVE_DIRECTORY, user);
        user.uploadProfile(profile);

        return profile;
    }),
    COVER((uploadFileService, image, user) -> {
        UploadFile cover = uploadFileService.save(image, COVER_SAVE_DIRECTORY, user);
        user.uploadCover(cover);

        return cover;
    });

    private final ImageUploader imageUploader;

    ImageType(final ImageUploader imageUploader) {
        this.imageUploader = imageUploader;
    }

    public ImageUploader getImageUploader() {
        return imageUploader;
    }

    public boolean equals(String type) {
        return this.toString().equals(type.toUpperCase());
    }
}
