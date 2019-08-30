package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.file.service.UploadFileService;
import com.wootecobook.turkey.user.domain.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@FunctionalInterface
public interface ImageUploader {

    @Transactional
    UploadFile upload(UploadFileService uploadFileService, MultipartFile image, User user);
}
