package com.lxw.framework.domain.course.ext;

import com.lxw.framework.domain.course.CourseBase;
import com.lxw.framework.domain.course.CourseMarket;
import com.lxw.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @AUTHOR: yadong
 * @DATE: 2021/10/18 15:24
 * @DESC:
 */
@Data
@NoArgsConstructor
public class CourseView {

    CourseBase courseBase;//基础信息
    CourseMarket courseMarket;//课程营销
    CoursePic coursePic;//课程图片
    TeachplanNode teachplanNode;//教学计划
}
