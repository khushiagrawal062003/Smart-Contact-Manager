package com.smartcontact.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadImage(MultipartFile file, String subDir);
    boolean deleteImage(String fileName, String subDir);
}
