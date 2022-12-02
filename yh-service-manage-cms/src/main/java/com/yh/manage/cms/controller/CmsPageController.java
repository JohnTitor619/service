package com.yh.manage.cms.controller;

import com.lxw.api.cms.CmsPageControllerApi;
import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.request.QueryPageRequest;
import com.lxw.framework.domain.cms.response.CmsPageResult;
import com.lxw.framework.model.response.QueryResponseResult;
import com.lxw.framework.model.response.ResponseResult;
import com.yh.manage.cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    private CmsPageService cmsPageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findCmsPageList(@PathVariable Integer page,
                                               @PathVariable Integer size,
                                               QueryPageRequest queryPageRequest) {
        return cmsPageService.findCmsPageList(page,size,queryPageRequest);
    }

    @Override
    @PostMapping("add")
    public CmsPageResult addCmsPage(@RequestBody CmsPage cmsPage) {
        return cmsPageService.addCmsPage(cmsPage);
    }

    @Override
    @PostMapping("/save")
    public CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage) {
        return cmsPageService.saveCmsPage(cmsPage);
    }

    @Override
    @PostMapping("/postPageQuick")
    public ResponseResult quickPostPage(@RequestBody CmsPage cmsPage) {
        return cmsPageService.quickPostPage(cmsPage);
    }


    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return cmsPageService.getById(id);
    }

    @Override
    @PutMapping("/edit/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return cmsPageService.update(id,cmsPage);
    }

    @Override
    @DeleteMapping("/del/{id}")
//使用http的delete方法完成岗位操作
    public ResponseResult delete(@PathVariable("id") String id) {
        return cmsPageService.delete(id);
    }

    @Override
    @PostMapping("/postPage/{id}")
    public ResponseResult postPage(@PathVariable String id) {
        return this.cmsPageService.postPage(id);
    }

}
