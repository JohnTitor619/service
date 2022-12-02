package com.lxw.api.course;

import com.lxw.framework.domain.cms.response.CoursePreviewResult;
import com.lxw.framework.domain.course.ext.CourseView;
import com.lxw.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "课程处理")
public interface CourseControllerApi {

    @ApiOperation("获取课程详情模型数据")
    public CourseView findCourseById(String courseId);

    @ApiOperation("得到预览课程的Url")
    public CoursePreviewResult preview(String id);

    @ApiOperation("一键发布课程详情页面")
    public ResponseResult quickPost(String courseId);
}
