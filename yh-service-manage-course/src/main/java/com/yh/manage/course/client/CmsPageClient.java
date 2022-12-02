package com.yh.manage.course.client;

import com.lxw.framework.client.ServiceList;
import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.response.CmsPageResult;
import com.lxw.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.ws.Service;

@FeignClient(value = ServiceList.SERVICE_MANAGE_CMS)
public interface CmsPageClient {

    @GetMapping("/cms/page/get/{id}")
    public CmsPage findById(@PathVariable("id") String id);

    @PostMapping("/cms/page/save")
    public CmsPageResult modifyCmsPage(@RequestBody CmsPage cmsPage);

    @PostMapping("/cms/page/postPageQuick")
    public ResponseResult quickPost(@RequestBody CmsPage cmsPage);
}
