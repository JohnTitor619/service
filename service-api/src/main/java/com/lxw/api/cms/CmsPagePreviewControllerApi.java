package com.lxw.api.cms;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags = "页面预览")
public interface CmsPagePreviewControllerApi {
    @ApiOperation("页面预览")
    @ApiImplicitParam(name = "pageId",dataType = "String",paramType = "path")
    public void preview(String pageId);
}
