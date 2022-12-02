package com.yh.manage.course.service;

import com.lxw.framework.domain.course.CourseBase;
import com.lxw.framework.domain.course.Teachplan;
import com.lxw.framework.domain.course.ext.TeachplanNode;
import com.lxw.framework.domain.course.response.CourseCode;
import com.lxw.framework.exception.CastException;
import com.lxw.framework.model.response.CommonCode;
import com.lxw.framework.model.response.ResponseResult;
import com.yh.manage.course.dao.CourseBaseRepository;
import com.yh.manage.course.dao.TeachplanMapper;
import com.yh.manage.course.dao.TeachplanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TeachPlanService {

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    public TeachplanNode findListByCourseId(String courseId){
        if (StringUtils.isEmpty(courseId)){
            CastException.cast(CourseCode.COURSE_PUBLISH_CDETAILERROR);
        }
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        return teachplanNode;
    }

    public ResponseResult addTeachPlan(Teachplan teachplan){
        //数据校验
        if (teachplan == null
                || StringUtils.isEmpty(teachplan.getPname())
                || StringUtils.isEmpty(teachplan.getCourseid())) {
            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        //校验课程是否真实存在
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(teachplan.getCourseid());
        if (!courseBaseOptional.isPresent()) {
            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CourseBase courseBase = courseBaseOptional.get();
        //判断该节点的父节点是否为空，为空代表添加是二级节点
        if (StringUtils.isEmpty(teachplan.getParentid())){
            //查询根节点
            List<Teachplan> teachplanList = teachplanRepository
                    .findByCourseidAndParentid(teachplan.getCourseid(), "0");
            //查询该节点的父节点，如果没有查到父节点，添加根节点，再添加该节点
            if (teachplanList == null || teachplanList.size() <= 0){
                //说明数据中该门课程没有课程计划根节点
                Teachplan root = new Teachplan();
                root.setPname(courseBase.getName());
                root.setCourseid(teachplan.getCourseid());
                root.setGrade("1");//层级 分为1、2、3级
                root.setParentid("0");
                root.setStatus("1");
                teachplanRepository.save(root);

                teachplan.setParentid(root.getId());
                teachplan.setGrade("2");
                teachplanRepository.save(teachplan);
            }else {
                //查询该节点的父节点，查到父节点，设置该节点的父节点，再添加该节点
                Teachplan root = teachplanList.get(0);
                teachplan.setParentid(root.getId());
                teachplan.setGrade("2");
                teachplanRepository.save(teachplan);
            }
        }else {
            teachplan.setGrade("3");//层级 分为1、2、3级
            teachplanRepository.save(teachplan);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
