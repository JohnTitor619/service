package com.yh.manage.cms.controller;

import com.lxw.api.cms.CmsSiteControllerApi;
import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.CmsSite;

import com.lxw.framework.domain.cms.response.CmsPageResult;
import com.lxw.framework.domain.cms.response.CmsSiteResult;
import com.lxw.framework.model.response.ResponseResult;
import com.yh.manage.cms.service.CmsSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cms/site")
public class CmsSiteController implements CmsSiteControllerApi {
    @Autowired
    private CmsSiteService cmsSiteService;
    @Override
    @GetMapping("/list/site")
    public List<CmsSite> findCmsSiteList() {
        return cmsSiteService.findCmsSiteAll();
    }

    @Override
    @PostMapping("add")
    public CmsSiteResult addCmsSite(@RequestBody CmsSite cmsSite) {
        return cmsSiteService.addCmsSite(cmsSite);
    }

    @Override
    @GetMapping("/get/{id}")
    public CmsSite findById(@PathVariable("id") String id) {
        return cmsSiteService.getById(id);
    }

    @Override
    @PutMapping("/edit/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsSiteResult edit(@PathVariable("id") String id, @RequestBody CmsSite cmsSite) {
        return cmsSiteService.update(id,cmsSite);
    }

    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return cmsSiteService.del(id);
    }
}
