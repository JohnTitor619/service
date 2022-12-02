package com.lxw.framework.domain.cms.request;

import lombok.Data;

@Data
public class QueryPageRequest {
    //站点id
    private String siteId;
    //页面ID
//    private String pageId;
    //页面名称
//    private String pageName;
    private String pageAliase;  //别名
    private String templateId;//模版id
}
