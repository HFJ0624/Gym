package com.sau.gym.admin.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    //上传头像功能
    String fileUpload(MultipartFile multipartFile);
}
