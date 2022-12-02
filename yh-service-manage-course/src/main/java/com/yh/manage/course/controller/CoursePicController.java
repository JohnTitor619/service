package com.yh.manage.course.controller;

import com.lxw.api.course.CoursePicControllerApi;
import com.lxw.framework.domain.course.CoursePic;
import com.lxw.framework.model.response.ResponseResult;
import com.yh.manage.course.service.CoursePicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CoursePicController implements CoursePicControllerApi {

    @Autowired
    private CoursePicService coursePicService;

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(CoursePic coursePic) {
        return coursePicService.addCoursePic(coursePic);
    }

    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePicByCourseId(@PathVariable String courseId) {
        return coursePicService.findCoursePicByCourseId(courseId);
    }

    @Override
    @PostMapping("/coursepic/delete/{courseId}")
    public ResponseResult deleteCoursePic(@PathVariable String courseId) {
        return coursePicService.deleteCoursePic(courseId);
    }
}
