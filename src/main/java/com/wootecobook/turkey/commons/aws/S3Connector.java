package com.wootecobook.turkey.commons.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class S3Connector {

    public static final String PROFILE_SAVE_DIRECTORY = "profile";
    public static final String COVER_SAVE_DIRECTORY = "cover";
    private static final Logger log = LoggerFactory.getLogger(S3Connector.class);

    private final AmazonS3 amazonS3Client;
    private final String bucket;

    public S3Connector(final AmazonS3 amazonS3Client, final String bucket) {
        this.amazonS3Client = amazonS3Client;
        this.bucket = bucket;
    }

    //directoryName을 따로 받지 않고 fileName과 합쳐서 외부에서 전달하기
    public String upload(final MultipartFile multipartFile, final String directoryName, final String fileName) throws IOException {
        File uploadFile = convert(multipartFile, fileName);
        String filePath = joinDirectoryAndFileName(directoryName, fileName);

        return upload(uploadFile, filePath);
    }

    private File convert(final MultipartFile multipartFile, final String fileName) throws IOException {
        File convertFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream((convertFile))) {
            fos.write(multipartFile.getBytes());
        }

        return convertFile;
    }

    private String joinDirectoryAndFileName(final String directoryName, final String fileName) {
        return String.format("%s/%s", directoryName, fileName);
    }

    private String upload(final File file, final String filePath) {
        String uploadImageUrl = putS3(file, filePath);
        removeTmpFile(file);
        return uploadImageUrl;
    }

    private void removeTmpFile(final File file) {
        if (!file.delete()) {
            log.info("임시파일이 삭제되지 못했습니다");
        }
    }

    private String putS3(final File file, final String filePath) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, filePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, filePath).toString();
    }

    public void delete(final String filePath) {
        amazonS3Client.deleteObject(bucket, filePath);
    }
}