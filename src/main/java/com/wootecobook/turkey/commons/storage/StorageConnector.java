package com.wootecobook.turkey.commons.storage;

import com.wootecobook.turkey.commons.storage.aws.FailedUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageConnector {

    String upload(MultipartFile multipartFile, String directoryName, String fileName) throws FailedUploadException;

    void delete(String filePath);
}
