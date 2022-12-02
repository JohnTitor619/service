package com.lxw.api.course;

import com.lxw.framework.domain.course.Teachplan;
import com.lxw.framework.domain.course.ext.TeachplanNode;
import com.lxw.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

@Api(tags = "课程计划模块，增删改查，管理媒资等处理")
public interface TeachPlanControllerApi {

    @ApiOperation("根据课程Id查询课程计划信息")
    public TeachplanNode findList(String courseId);

    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);
}
