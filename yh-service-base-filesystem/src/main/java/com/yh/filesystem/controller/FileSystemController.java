package com.yh.filesystem.controller;

import com.lxw.api.filesystem.FileSystemControllerApi;
import com.lxw.framework.domain.filesystem.response.UploadFileResult;
import com.yh.filesystem.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    private FileSystemService fileSystemService;

    @Override
    @PostMapping("/filesystem/upload")
    public UploadFileResult upload(MultipartFile file, String businessKey, String fileteg, String metadata) {
        return fileSystemService.upload(file, businessKey, fileteg,metadata);
    }
}
