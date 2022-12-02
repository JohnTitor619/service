package com.lxw.framework.domain.course.response;

import com.lxw.framework.domain.course.CourseBase;
import com.lxw.framework.model.response.ResponseResult;
import com.lxw.framework.model.response.ResultCode;
import lombok.Data;
import lombok.ToString;

/**
 * Created by mrt on 2018/3/20.
 */
@Data
@ToString
public class AddCourseResult extends ResponseResult {
    public AddCourseResult(ResultCode resultCode,CourseBase courseBase) {
        super(resultCode);
        this.courseBase = courseBase;
    }
    private CourseBase courseBase;

}
