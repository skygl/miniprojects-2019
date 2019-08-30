package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.file.service.UploadFileService;
import com.wootecobook.turkey.user.service.dto.MyPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MyPageService {

    private final UserService userService;
    private final UploadFileService uploadFileService;

    public MyPageService(final UserService userService, final UploadFileService uploadFileService) {
        this.userService = userService;
        this.uploadFileService = uploadFileService;
    }

    @Transactional(readOnly = true)
    public MyPageResponse findUserResponseById(final Long id) {
        return MyPageResponse.from(
                userService.findById(id),
                uploadFileService.findFileFeaturesByUserId(id),
                userService.findUserResponseOfFriendsById(id));
    }
}
