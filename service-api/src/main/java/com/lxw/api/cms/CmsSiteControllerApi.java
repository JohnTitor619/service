package com.lxw.api.cms;


import com.lxw.framework.domain.cms.CmsSite;
import com.lxw.framework.domain.cms.response.CmsPageResult;
import com.lxw.framework.domain.cms.response.CmsSiteResult;
import com.lxw.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(tags = "页面查询，添加，修改，发布等接口")
public interface CmsSiteControllerApi {
    @ApiOperation("根据站点ID，模板ID,别名ID，分页查询CmsSite集合下的数据")

    public List<CmsSite> findCmsSiteList();

    @ApiOperation("添加cmsSite信息")
    @ApiImplicitParam(name = "cmsSite",value = "cmsSite表单信息",dataType = "CmsSite",paramType = "body")
    public CmsSiteResult addCmsSite(CmsSite cmsSite);


    @ApiOperation("通过ID查询页面")
    public CmsSite findById(String id);

    @ApiOperation("修改页面")
    public CmsSiteResult edit(String id, CmsSite cmsSite);

    @ApiOperation("通过ID删除页面")
    public ResponseResult delete(String id);




}
