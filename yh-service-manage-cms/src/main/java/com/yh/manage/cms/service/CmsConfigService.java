package com.yh.manage.cms.service;

import com.lxw.framework.domain.cms.CmsConfig;
import com.lxw.framework.exception.CustomException;
import com.lxw.framework.model.response.CommonCode;
import com.yh.manage.cms.dao.CmsConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CmsConfigService {
    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    public CmsConfig findCmsConfigById(String id){
        //数据校验
        if (StringUtils.isEmpty(id)){
            log.error("cmsConfig id为空");
            throw new CustomException(CommonCode.VALIDATA);
        }
        Optional<CmsConfig> cmsConfigOptional = cmsConfigRepository.findById(id);
        CmsConfig cmsConfig = cmsConfigOptional.orElse(null);
        return cmsConfig;
    }

}
