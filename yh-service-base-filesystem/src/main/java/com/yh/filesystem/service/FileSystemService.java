package com.yh.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.lxw.framework.domain.filesystem.FileSystem;
import com.lxw.framework.domain.filesystem.response.FileSystemCode;
import com.lxw.framework.domain.filesystem.response.UploadFileResult;
import com.lxw.framework.exception.CastException;
import com.lxw.framework.model.response.CommonCode;
import com.lxw.framework.utils.QiniuUtil;
import com.yh.filesystem.dao.FilesystemRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class FileSystemService {
    @Value("${qiniu.ak}")
    private String ak;

    @Value("${qiniu.sk}")
    private String sk;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.path}")
    private String path;

    @Autowired
    private FilesystemRepository filesystemRepository;

    public UploadFileResult upload(MultipartFile file,
                                   String businessKey,
                                   String fileteg,
                                   String metadata) {
        //0.数据校验
        if (file == null) {
            CastException.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        String fileKey = null;
        try {
            byte[] bytes = file.getBytes();
            //1.把图片上传到七牛云上
            fileKey = QiniuUtil.uploadImg(ak, sk, bucket, bytes);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        //2.把文件信息保存到MongoDB库中
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileKey);
        fileSystem.setFileName(file.getOriginalFilename());
        fileSystem.setFilePath(path);
        fileSystem.setBusinesskey(businessKey);
        fileSystem.setFiletag(fileteg);
        if (StringUtils.isNotEmpty(metadata)){
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }
        filesystemRepository.insert(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }
    public UploadFileResult delete(MultipartFile file,
                                   String businessKey,
                                   String fileteg,
                                   String metadata) {
        //0.数据校验
        if (file == null) {
            CastException.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        String fileKey = null;
        fileKey = QiniuUtil.delete(ak,sk,bucket);
        return null;
    }
}
