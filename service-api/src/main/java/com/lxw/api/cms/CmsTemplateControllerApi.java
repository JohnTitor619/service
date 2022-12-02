package com.lxw.api.cms;


import com.lxw.framework.domain.cms.CmsSite;
import com.lxw.framework.domain.cms.CmsTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(tags = "页面查询，添加，修改，发布等接口")
public interface CmsTemplateControllerApi {
    @ApiOperation("根据站点ID，模板ID,别名ID，分页查询CmsTemplate集合下的数据")

    public List<CmsTemplate> findCmsTemplateList();


}
