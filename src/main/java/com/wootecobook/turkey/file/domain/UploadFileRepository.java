package com.wootecobook.turkey.file.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    List<UploadFile> findByOwnerId(Long ownerId);
}
