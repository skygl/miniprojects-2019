package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.service.UploadFileService;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.dto.UploadImage;
import com.wootecobook.turkey.user.service.exception.UserMismatchException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserImageUploadService {

    private final UserService userService;
    private final UploadFileService uploadFileService;

    public UserImageUploadService(final UserService userService, final UploadFileService uploadFileService) {
        this.userService = userService;
        this.uploadFileService = uploadFileService;
    }

    public FileFeature uploadImage(UploadImage profile, Long userId, Long loginUserId) {
        if (!userId.equals(loginUserId)) {
            throw new UserMismatchException();
        }

        User user = userService.findById(userId);
        ImageType imageType = profile.getType();

        return imageType.getImageUploader()
                .upload(uploadFileService, profile.getImage(), user).getFileFeature();
    }
}
