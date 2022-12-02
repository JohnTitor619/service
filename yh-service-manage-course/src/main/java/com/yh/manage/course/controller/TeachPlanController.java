package com.yh.manage.course.controller;

import com.lxw.api.course.TeachPlanControllerApi;
import com.lxw.framework.domain.course.Teachplan;
import com.lxw.framework.domain.course.ext.TeachplanNode;
import com.lxw.framework.model.response.ResponseResult;
import com.yh.manage.course.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course/teachplan")
public class TeachPlanController implements TeachPlanControllerApi {

    @Autowired
    private TeachPlanService teachPlanService;

    @GetMapping("/list/{courseId}")
    public TeachplanNode findList(@PathVariable String courseId){
        TeachplanNode listByCourseId = teachPlanService.findListByCourseId(courseId);
        return listByCourseId;
    }

    @Override
    @PostMapping("/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return teachPlanService.addTeachPlan(teachplan);
    }

}
