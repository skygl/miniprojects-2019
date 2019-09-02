package com.wootecobook.turkey.file.service;

import com.wootecobook.turkey.config.AwsMockConfig;
import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.user.domain.User;
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
class UploadFileServiceTest {

    private static final String DIRECTORY_NAME = "testDir";
    private final String storageUrl;
    private MockMultipartFile mockMultipartFile;
    private User owner;

    @Autowired
    private UploadFileService uploadFileService;

    UploadFileServiceTest(@Autowired String bucket) {
        storageUrl = String.format("%s/%s/%s/", S3MOCK_ENDPOINT, bucket, DIRECTORY_NAME);
    }

    @BeforeEach
    void setup() {
        mockMultipartFile = new MockMultipartFile(
                "file", "mock1.png", "image/png", "test data".getBytes());
        owner = new User("a@mail.com", "name", "Passw0rd!");
        owner.setId(1L);
    }

    @Test
    void 저장_테스트() {
        //when
        UploadFile uploadFile = uploadFileService.save(mockMultipartFile, DIRECTORY_NAME, owner);

        //then
        assertThat(uploadFile.isOwner(owner)).isTrue();

        FileFeature savedFileFeature = uploadFile.getFileFeature();
        assertThat(savedFileFeature.getOriginalName()).isEqualTo(mockMultipartFile.getOriginalFilename());
        assertThat(savedFileFeature.getSize()).isEqualTo(mockMultipartFile.getSize());
        assertThat(savedFileFeature.getPath()).matches("^" + storageUrl + ".*$");
    }
}