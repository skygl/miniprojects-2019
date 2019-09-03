package com.wootecobook.turkey.file.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.wootecobook.turkey.commons.storage.aws.S3Connector;
import com.wootecobook.turkey.file.domain.FileFeature;
import com.wootecobook.turkey.file.domain.UploadFile;
import com.wootecobook.turkey.file.domain.UploadFileRepository;
import com.wootecobook.turkey.user.domain.User;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Java6Assertions.assertThat;

@DataJpaTest
class UploadFileServiceTest {

    private static final String DIRECTORY_NAME = "testDir";

    @Autowired
    private UploadFileRepository uploadFileRepository;

    private S3Connector s3Connector;
    private S3Mock s3Mock;

    private String bucket;
    private String region;
    private String storageUrl;
    private MockMultipartFile mockMultipartFile;
    private User owner;

    private UploadFileService uploadFileService;

    @BeforeEach
    void setup() {
        bucket = "woowa-turkey";
        region = "ap-northeast-2";

        s3Mock = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
        s3Mock.start();
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration("http://localhost:8001", region);
        AmazonS3 client = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
        client.createBucket(bucket);

        storageUrl = String.format("%s/%s/%s/", "http://localhost:8001", bucket, DIRECTORY_NAME);
        mockMultipartFile = new MockMultipartFile(
                "file", "mock1.png", "image/png", "test data".getBytes());
        owner = new User("a@mail.com", "name", "Passw0rd!");
        owner.setId(1L);

        s3Connector = new S3Connector(client, bucket);
        uploadFileService = new UploadFileService(s3Connector, uploadFileRepository);
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

    @AfterEach
    void tearDown() {
        s3Mock.stop();
    }
}