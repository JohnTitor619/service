package com.yh.manage.course.controller;

import com.lxw.api.course.CourseControllerApi;
import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.response.CoursePreviewResult;
import com.lxw.framework.domain.course.ext.CourseView;
import com.lxw.framework.model.response.ResponseResult;
import com.yh.manage.course.client.CmsPageClient;
import com.yh.manage.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CmsPageClient cmsPageClient;

    @GetMapping("/find")
    public CmsPage findById(String id){
        CmsPage cmsPage = cmsPageClient.findById(id);
        return cmsPage;
    }

    @Override
    @GetMapping("/courseview/{courseId}")
    public CourseView findCourseById(@PathVariable String courseId) {
        return courseService.findCourseById(courseId);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePreviewResult preview(@PathVariable String id) {
        return courseService.preview(id);
    }

    @Override
    @PostMapping("/publish/{id}")
    public ResponseResult quickPost(@PathVariable String id) {
        return courseService.quickPost(id);
    }
}
