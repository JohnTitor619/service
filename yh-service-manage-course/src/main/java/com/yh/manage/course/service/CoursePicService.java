package com.yh.manage.course.service;

import com.lxw.framework.domain.course.CoursePic;
import com.lxw.framework.domain.course.response.CourseCode;
import com.lxw.framework.exception.CastException;
import com.lxw.framework.model.response.CommonCode;
import com.lxw.framework.model.response.ResponseResult;
import com.yh.manage.course.dao.CoursePicRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
public class CoursePicService {

    @Autowired
    private CoursePicRepository coursePicRepository;

    public ResponseResult addCoursePic(CoursePic coursePic) {
        if (coursePic == null
                || StringUtils.isEmpty(coursePic.getCourseId())
                || StringUtils.isEmpty(coursePic.getPic())) {
            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findCoursePicByCourseId(@PathVariable String courseId) {
        if (StringUtils.isEmpty(courseId)){
            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(courseId);
        CoursePic coursePic = coursePicOptional.orElse(null);
        return coursePic;
    }

    public ResponseResult deleteCoursePic(String courseId) {
        if (StringUtils.isEmpty(courseId)){
            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        coursePicRepository.deleteById(courseId);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
