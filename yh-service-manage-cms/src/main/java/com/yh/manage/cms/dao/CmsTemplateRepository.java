package com.yh.manage.cms.dao;


import com.lxw.framework.domain.cms.CmsSite;
import com.lxw.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {

}
