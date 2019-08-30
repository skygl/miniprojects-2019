package com.wootecobook.turkey.user.service;

import com.wootecobook.turkey.config.AwsMockConfig;
import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.service.UploadFileService;
import com.wootecobook.turkey.user.domain.User;
import com.wootecobook.turkey.user.service.dto.UploadImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

import static com.wootecobook.turkey.config.AwsMockConfig.S3MOCK_ENDPOINT;
import static org.assertj.core.api.Java6Assertions.assertThat;

@Import(AwsMockConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserImageUploadServiceTest {

    private static final String PROFILE_DIRECTORY_NAME = "profile";
    private static final String COVER_DIRECTORY_NAME = "cover";

    private final UserImageUploadService userImageUploadService;
    private final String bucket;

    private MockMultipartFile mockMultipartFile;
    private User owner;

    @Autowired
    UserImageUploadServiceTest(String bucket, UserService userService, UploadFileService uploadFileService) {
        this.bucket = bucket;
        userImageUploadService = new UserImageUploadService(userService, uploadFileService);
    }

    @BeforeEach
    void setup() {
        mockMultipartFile = new MockMultipartFile(
                "file", "mock1.png", "image/png", "test data".getBytes());
        owner = new User("a@mail.com", "name", "Passw0rd!");
        owner.setId(1L);
    }

    @Test
    void profile_이미지_저장_테스트() {
        // given
        UploadImage testUploadImage = UploadImage.builder()
                .image(mockMultipartFile)
                .type(ImageType.PROFILE)
                .build();

        String storageUrl = String.format("%s/%s/%s/", S3MOCK_ENDPOINT, bucket, PROFILE_DIRECTORY_NAME);

        //when
        FileFeature savedFileFeature = userImageUploadService.uploadImage(testUploadImage, owner.getId(), 1L);

        //then
        assertThat(savedFileFeature.getOriginalName()).isEqualTo(mockMultipartFile.getOriginalFilename());
        assertThat(savedFileFeature.getSize()).isEqualTo(mockMultipartFile.getSize());
        assertThat(savedFileFeature.getPath()).matches("^" + storageUrl + ".*$");
    }

    @Test
    void cover_이미지_저장_테스트() {
        // given
        UploadImage testUploadImage = UploadImage.builder()
                .image(mockMultipartFile)
                .type(ImageType.COVER)
                .build();

        String storageUrl = String.format("%s/%s/%s/", S3MOCK_ENDPOINT, bucket, COVER_DIRECTORY_NAME);

        //when
        FileFeature savedFileFeature = userImageUploadService.uploadImage(testUploadImage, owner.getId(), 1L);

        //then
        assertThat(savedFileFeature.getOriginalName()).isEqualTo(mockMultipartFile.getOriginalFilename());
        assertThat(savedFileFeature.getSize()).isEqualTo(mockMultipartFile.getSize());
        assertThat(savedFileFeature.getPath()).matches("^" + storageUrl + ".*$");
    }
}
