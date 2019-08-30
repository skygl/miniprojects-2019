package com.wootecobook.turkey.user.service.dto;

import com.wootecobook.turkey.user.service.ImageType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UploadImage {

    private ImageType type;

    @NotNull
    private MultipartFile image;

    @Builder
    public UploadImage(final ImageType type, final MultipartFile image) {
        this.type = type;
        this.image = image;
    }
}
