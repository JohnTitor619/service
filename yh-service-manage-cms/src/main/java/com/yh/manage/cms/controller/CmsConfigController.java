package com.yh.manage.cms.controller;

import com.lxw.api.cms.CmsConfigControllerApi;
import com.lxw.framework.domain.cms.CmsConfig;
import com.yh.manage.cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cms/")
public class CmsConfigController implements CmsConfigControllerApi {
    @Autowired
    private CmsConfigService cmsConfigService;

    @GetMapping("/getmodel/{id}")
    @Override
    public CmsConfig findCmsConfigById(@PathVariable String id) {
        return cmsConfigService.findCmsConfigById(id);
    }
}
