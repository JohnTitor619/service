package com.yh.manage.cms.service;

import com.lxw.framework.domain.cms.CmsSite;
import com.lxw.framework.domain.cms.CmsTemplate;
import com.yh.manage.cms.dao.CmsSiteRepository;
import com.yh.manage.cms.dao.CmsTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsTemplateService {
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    public List<CmsTemplate> findTemplateAll(){
        List<CmsTemplate> cmsTemplatesList= cmsTemplateRepository.findAll();
        return cmsTemplatesList;
    }
}
