package com.yh.manage.cms.controller;

import com.lxw.api.cms.CmsPagePreviewControllerApi;
import com.lxw.framework.web.BaseController;
import com.yh.manage.cms.service.CmsPageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
@Slf4j
public class CmsPagePreviewController extends BaseController implements CmsPagePreviewControllerApi{

    @Autowired
    private CmsPageService cmsPageService;
    @Override
    @RequestMapping(value="/cms/page/preview/{pageId}",method= RequestMethod.GET)
    public void preview(@PathVariable String pageId) {
        String pageHtml = cmsPageService.getPageHtml(pageId);
        try {
            response.setContentType("text/html;charset=utf8");
            response.getWriter().write(pageHtml);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
