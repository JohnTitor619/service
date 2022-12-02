package com.lxw.api.cms;

import com.lxw.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags = "门户首页模型数据，增删改查等功能")
public interface CmsConfigControllerApi {

    @ApiOperation("获取门户首页模型数据")
    @ApiImplicitParam(name = "id",dataType = "String",paramType = "path")
    public CmsConfig findCmsConfigById(String id);
}
