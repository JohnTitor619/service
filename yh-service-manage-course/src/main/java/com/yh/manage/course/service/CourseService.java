package com.yh.manage.course.service;

import com.alibaba.nacos.api.cmdb.spi.CmdbService;
import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.response.CmsPageResult;
import com.lxw.framework.domain.cms.response.CoursePreviewResult;
import com.lxw.framework.domain.course.CourseBase;
import com.lxw.framework.domain.course.CourseMarket;
import com.lxw.framework.domain.course.CoursePic;
import com.lxw.framework.domain.course.ext.CourseView;
import com.lxw.framework.domain.course.ext.TeachplanNode;
import com.lxw.framework.domain.course.response.CourseCode;
import com.lxw.framework.exception.CastException;
import com.lxw.framework.model.response.CommonCode;
import com.lxw.framework.model.response.ResponseResult;
import com.yh.manage.course.client.CmsPageClient;
import com.yh.manage.course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
public class CourseService {
    @Value("${course‐publish.siteId}")
    private String siteId;

    @Value("${course‐publish.templateId}")
    private String templateId;

    @Value("${course‐publish.previewUrl}")
    private String previewUrl;

    @Value("${course‐publish.pageWebPath}")
    private String pageWebPath;

    @Value("${course‐publish.pagePhysicalPath}")
    private String pagePhysicalPath;

    @Value("${course‐publish.dataUrlPre}")
    private String dataUrlPre;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    public CourseView findCourseById(String courseId) {
        if (StringUtils.isEmpty(courseId)){
            CastException.cast(CourseCode.COURSE_MEDIS_NAMEISNULL);
        }
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (!courseBaseOptional.isPresent()) {
            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CourseBase courseBase = courseBaseOptional.get();

        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);

        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);
        CourseMarket courseMarket = courseMarketOptional.orElse(null);

        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(courseId);
        CoursePic coursePic = coursePicOptional.orElse(null);

        CourseView courseView = new CourseView();
        courseView.setCourseBase(courseBase);
        courseView.setCourseMarket(courseMarket);
        courseView.setCoursePic(coursePic);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    public CoursePreviewResult preview(String id){
//        //数据校验
////        if (StringUtils.isEmpty(id)){
////            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
////        }
////        //根据Id查询课程基本信息
////        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
////        if (!courseBaseOptional.isPresent()) {
////            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
////        }
////        CourseBase courseBase = courseBaseOptional.get();
        CourseBase courseBase = findCourseBaseById(id);
        //组装一个cmsPage信息
        CmsPage cmsPage = this.doneCmsPage(courseBase);
        //远程调用cms服务，保存更新cmsPage信息
        CmsPageResult cmsPageResult = cmsPageClient.modifyCmsPage(cmsPage);
        //拼接预览的Url
        String previewPageUrl = this.previewUrl+cmsPageResult.getCmsPage().getPageId();
        CoursePreviewResult coursePreviewResult = new CoursePreviewResult(CommonCode.SUCCESS, previewPageUrl);
        return coursePreviewResult;

    }
    //组装一个cmsPage信息
    private CmsPage doneCmsPage(CourseBase courseBase){
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageAliase(courseBase.getName());
        cmsPage.setPageName(courseBase.getId()+".html");
        cmsPage.setSiteId(siteId);
        cmsPage.setTemplateId(templateId);
        cmsPage.setDataUrl(dataUrlPre+courseBase.getId());
        cmsPage.setPagePhysicalPath(pagePhysicalPath);
        cmsPage.setPageWebPath(pageWebPath);
        return cmsPage;
    }

    private CourseBase findCourseBaseById(String id){
        //数据校验
        if (StringUtils.isEmpty(id)){
            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        //根据Id查询课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (!courseBaseOptional.isPresent()) {
            CastException.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CourseBase courseBase = courseBaseOptional.get();
        return courseBase;
    }

    public ResponseResult quickPost(String id) {
        CourseBase courseBase = findCourseBaseById(id);
        //组装一个cmsPage信息
        CmsPage cmsPage = this.doneCmsPage(courseBase);
        //远程调用cms服务一键发布
        ResponseResult responseResult = cmsPageClient.quickPost(cmsPage);
        //发布成功，更新课程状态为已发布
        if (responseResult.isSuccess()){
            courseBase.setStatus("202002");
            courseBaseRepository.save(courseBase);
            return new ResponseResult(CommonCode.SUCCESS);
        }else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }
}

