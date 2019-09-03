package com.wootecobook.turkey.commons.storage.aws;

public class FailedUploadException extends RuntimeException {

    public FailedUploadException(String message) {
        super(message);
    }
}
