package com.yh.manage.cms.dao;

import com.lxw.framework.domain.cms.CmsConfig;
import com.lxw.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {


}
