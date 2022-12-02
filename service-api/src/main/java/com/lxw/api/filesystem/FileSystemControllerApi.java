package com.lxw.api.filesystem;

import com.lxw.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "文件模块处理")
public interface FileSystemControllerApi {

    @ApiOperation("上传图片")
    public UploadFileResult upload(MultipartFile file,String businessKey,String fileteg,String metadata);
}
