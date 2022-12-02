package com.yh.manage.cms.service;

import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.CmsSite;
import com.lxw.framework.domain.cms.response.CmsPageResult;
import com.lxw.framework.domain.cms.response.CmsSiteResult;
import com.lxw.framework.model.response.CommonCode;
import com.lxw.framework.model.response.QueryResponseResult;
import com.lxw.framework.model.response.QueryResult;
import com.lxw.framework.model.response.ResponseResult;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.yh.manage.cms.dao.CmsSiteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CmsSiteService {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    public List<CmsSite> findCmsSiteAll(){
        List<CmsSite> cmsSiteList= cmsSiteRepository.findAll();
        return cmsSiteList;
    }


    public CmsSiteResult addCmsSite(CmsSite cmsSite) {
        if (cmsSite == null){
            return new CmsSiteResult(CommonCode.FAIL,cmsSite);
        }
        CmsSite insert = cmsSiteRepository.insert(cmsSite);
        return new CmsSiteResult(CommonCode.SUCCESS,cmsSite);
    }

    public CmsSite getById(String id) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(id); if(optional.isPresent()){
            return optional.get();
        }
        return null;
        //返回空return null;
    }

    public CmsSiteResult update(String id, CmsSite cmsSite) {
        CmsSite one = this.getById(id);
        if (one != null){
            one.setSiteId(cmsSite.getSiteId());
            one.setSiteName(cmsSite.getSiteName());
            one.setSiteDomain(cmsSite.getSiteDomain());
            one.setSitePort(cmsSite.getSitePort());
            one.setSiteWebPath(cmsSite.getSiteWebPath());
            one.setSiteCreateTime(cmsSite.getSiteCreateTime());
            CmsSite save = cmsSiteRepository.save(one);
            if (save!=null){
                CmsSiteResult cmsSiteResult = new CmsSiteResult(CommonCode.SUCCESS, save);
                return cmsSiteResult;
            }
        }


        return new CmsSiteResult(CommonCode.FAIL,null);

    }

    public ResponseResult del(String id) {
        CmsSite one = this.getById(id);
        if(one!=null){
            //删除页面
            cmsSiteRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
}
