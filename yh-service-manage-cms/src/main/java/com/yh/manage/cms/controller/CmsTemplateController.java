package com.yh.manage.cms.controller;

import com.lxw.api.cms.CmsTemplateControllerApi;
import com.lxw.framework.domain.cms.CmsSite;
import com.lxw.framework.domain.cms.CmsTemplate;
import com.yh.manage.cms.service.CmsSiteService;
import com.yh.manage.cms.service.CmsTemplateService;
import org.hibernate.sql.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cms/page")
public class CmsTemplateController implements CmsTemplateControllerApi {
    @Autowired
    private CmsTemplateService cmsTemplateService;
    @Override
    @GetMapping("/list/Template")
    public List<CmsTemplate> findCmsTemplateList() {
        return cmsTemplateService.findTemplateAll();
    }
}
