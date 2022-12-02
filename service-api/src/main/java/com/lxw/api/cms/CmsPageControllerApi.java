package com.lxw.api.cms;


import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.request.QueryPageRequest;
import com.lxw.framework.domain.cms.response.CmsPageResult;
import com.lxw.framework.model.response.QueryResponseResult;
import com.lxw.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags = "页面查询，添加，修改，发布等接口")
public interface CmsPageControllerApi {
    @ApiOperation("根据站点ID，模板ID,别名ID，分页查询CmsPge集合下的数据")
    public QueryResponseResult findCmsPageList(Integer page, Integer size, QueryPageRequest queryPageRequest);

    @ApiOperation("添加cmsPage信息")
    @ApiImplicitParam(name = "cmsPage",value = "cmsPage表单信息",dataType = "CmsPage",paramType = "body")
    public CmsPageResult addCmsPage(CmsPage cmsPage);


    @ApiOperation("通过ID查询页面")
    public CmsPage findById(String id);

    @ApiOperation("修改页面")
    public CmsPageResult edit(String id,CmsPage cmsPage);

    @ApiOperation("通过ID删除页面")
    public ResponseResult delete(String id);

    @ApiOperation("页面发布")
    @ApiImplicitParam(name = "id",value = "cmsPageId",dataType = "String",paramType = "path")
    public ResponseResult postPage(String id);

    @ApiOperation("添加或更新cmsPage信息")
    @ApiImplicitParam(name = "cmsPage",value = "cmsPage信息",dataType = "cmsPage",paramType = "body")
    public CmsPageResult saveCmsPage(CmsPage cmsPage);

    @ApiOperation("一键发布")
    @ApiImplicitParam(name = "cmsPage",value = "cmsPage信息",dataType = "cmsPage",paramType = "body")
    public ResponseResult quickPostPage(CmsPage cmsPage);


}
