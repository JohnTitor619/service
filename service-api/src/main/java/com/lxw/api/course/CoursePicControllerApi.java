package com.lxw.api.course;

import com.lxw.framework.domain.course.CoursePic;
import com.lxw.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "课程图片处理")
public interface CoursePicControllerApi {

    @ApiOperation("添加课程图片")
    public ResponseResult addCoursePic(CoursePic coursePic);

    @ApiOperation("查询课程图片信息")
    public CoursePic findCoursePicByCourseId(String courseId);

    @ApiOperation("删除课程图片")
    public ResponseResult deleteCoursePic(String courseId);
}
