package com.yh.manage.cms.service;

import com.alibaba.fastjson.JSON;
import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.CmsTemplate;
import com.lxw.framework.domain.cms.request.QueryPageRequest;
import com.lxw.framework.domain.cms.response.CmsCode;
import com.lxw.framework.domain.cms.response.CmsPageResult;
import com.lxw.framework.exception.CastException;
import com.lxw.framework.exception.CustomException;
import com.lxw.framework.model.response.CommonCode;
import com.lxw.framework.model.response.QueryResponseResult;
import com.lxw.framework.model.response.QueryResult;
import com.lxw.framework.model.response.ResponseResult;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.yh.manage.cms.config.RabbitmqConfig;
import com.yh.manage.cms.dao.CmsPageRepository;
import com.yh.manage.cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CmsPageService {
//    @Autowired
//    private RestTemplate restTemplate;
//
    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RestTemplate restTemplate;

/*
    @Value("${yh.mq.routeKey}")
    private String routeKey;*/

    public QueryResponseResult findCmsPageList(Integer page, Integer size,
                                               QueryPageRequest queryPageRequest) {
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        //List<CmsPage> all = cmsPageRepository.findAll();
        //????????????
        CmsPage cmsPage = new CmsPage();
//        BeanUtils.copyProperties(queryPageRequest,cmsPage);
        //????????????????????????
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
            exampleMatcher = exampleMatcher
                    .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        //?????????Example,?????????????????????????????????
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //???????????????
        Pageable pageable = PageRequest.of(page, size);
        //????????????
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        //???????????????
        long totalElements = all.getTotalElements();
        //????????????????????????
        List<CmsPage> cmsPageList = all.getContent();

        QueryResult result = new QueryResult();
        result.setTotal(totalElements);
        result.setList(cmsPageList);

        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, result);
        return queryResponseResult;
    }

    public CmsPageResult addCmsPage(CmsPage cmsPage) {
        if (cmsPage == null
                || StringUtils.isEmpty(cmsPage.getPageName())
                || StringUtils.isEmpty(cmsPage.getSiteId())
                || StringUtils.isEmpty(cmsPage.getPageWebPath())) {
            return new CmsPageResult(CommonCode.FAIL, cmsPage);
        }
        CmsPage cmsPageDb = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmsPageDb == null) {
            cmsPageRepository.insert(cmsPage);
        }
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }

    //????????????html??????
    public String getPageHtml(String pageId){
        //????????????
        if (StringUtils.isEmpty(pageId)){
            throw new CustomException(CommonCode.VALIDATA);
        }
        //??????cmsPageId??????cmsPage??????
        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(pageId);
        if (!cmsPageOptional.isPresent()) {
            throw new CustomException(CmsCode.CMS_PAGE_EXISTSID);
        }
        CmsPage cmsPage = cmsPageOptional.get();
        String templateId = cmsPage.getTemplateId();
        String dataUrl = cmsPage.getDataUrl();
        //??????cmsPage??????cmsTemplateId???????????????????????????
        String templateContent = this.getTemplateContent(templateId);
        //??????cmsPage??????dataUrl???????????????????????????
        Map modelData = this.getModelDataByDataUrl(dataUrl);
        //??????freemarker??????????????????
        String htmlContent = this.genarateHtml(templateContent, modelData);
        return htmlContent;
    }
    //??????freemarker??????????????????
    private String genarateHtml(String templateContent,Map model){
        //?????????????????????
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDefaultEncoding("utf-8");
        //????????????????????????????????????????????????????????????
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        configuration.setTemplateLoader(stringTemplateLoader);
        //????????????
        try {
            Template template = configuration.getTemplate("template");
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return htmlContent;
        } catch (Exception e) {
            log.error("??????html??????,{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //??????cmsPage??????dataUrl???????????????????????????
    private Map getModelDataByDataUrl(String dataUrl){
        if (StringUtils.isEmpty(dataUrl)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        Map map = restTemplate.getForObject(dataUrl, Map.class);
        return map;
    }
    //????????????????????????
    private String getTemplateContent(String templateId){
        if (StringUtils.isEmpty(templateId)){
            throw new CustomException(CommonCode.VALIDATA);
        }
        Optional<CmsTemplate> cmsTemplateOptional = cmsTemplateRepository.findById(templateId);
        if (!cmsTemplateOptional.isPresent()) {
//            throw new CustomException(CmsCode.CMS_PAGE_EXISTSID);
                CastException.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        CmsTemplate cmsTemplate = cmsTemplateOptional.get();
        String templateFileId = cmsTemplate.getTemplateFileId();
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        try {
            InputStream inputStream = gridFsResource.getInputStream();
            String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return templateContent;
        } catch (Exception e) {
            log.error("???gridFS??????????????????{}",e.getMessage());
            throw new CustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

    }

    public ResponseResult postPage(String id){
        //1.??????html??????
        String pageHtml = this.getPageHtml(id);
        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(id);
        CmsPage cmsPage = cmsPageOptional.orElse(null);
        try {
            InputStream inputStream = IOUtils.toInputStream(pageHtml, "utf-8");
            //2.????????????html?????????GridFS???
            String htmlFileId = gridFsTemplate.store(inputStream,cmsPage.getPageName()).toHexString();
            cmsPage.setHtmlFileId(htmlFileId);
            //3.??????cmsPage??????????????????htmlFileId
            cmsPageRepository.save(cmsPage);
            Map<String, String> map = new HashMap<String, String>();
            map.put("pageId",id);
            //4.????????????
            rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,
                    cmsPage.getSiteId(),JSON.toJSONString(map));
            return new ResponseResult(CommonCode.SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CmsPageResult saveCmsPage(CmsPage cmsPage) {
        //????????????
        if (cmsPage == null || StringUtils.isEmpty(cmsPage.getPageName())
                            || StringUtils.isEmpty(cmsPage.getSiteId())
                            || StringUtils.isEmpty(cmsPage.getPageName())){
            CastException.cast(CommonCode.VALIDATA);
        }
        CmsPage cmsPageDb = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(),
                cmsPage.getPageWebPath());
        if (cmsPageDb == null){
            cmsPageRepository.insert(cmsPage);
        }else {
            cmsPage.setPageId(cmsPageDb.getPageId());
            cmsPageRepository.save(cmsPage);
        }
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);

    }

    //????????????
    public ResponseResult quickPostPage(CmsPage cmsPage) {
        //1.???????????????cmsPage??????
        CmsPageResult cmsPageResult = this.saveCmsPage(cmsPage);
        //2.??????
        if (cmsPageResult.isSuccess()){
            String pageId = cmsPageResult.getCmsPage().getPageId();
            ResponseResult responseResult = this.postPage(pageId);
            return responseResult;
        }
        return new ResponseResult(CommonCode.FAIL);
    }












        //??????id????????????
    public CmsPage getById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
        //?????????return null;
    }

    //??????????????????
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //??????id??????????????????
        CmsPage one = this.getById(id);
        if (one != null) {
            //????????????id
            one.setTemplateId(cmsPage.getTemplateId());
            //??????????????????one.setSiteId(cmsPage.getSiteId());
            //??????????????????
            one.setPageAliase(cmsPage.getPageAliase());
            //??????????????????one.setPageName(cmsPage.getPageName());
            //??????????????????
            one.setPageWebPath(cmsPage.getPageWebPath());
            //??????????????????one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //????????????
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //????????????
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //????????????
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    //????????????
    public ResponseResult delete(String id) {
        CmsPage one = this.getById(id);
        if (one != null) {
            //????????????
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


//    //????????????
//    public String preview(String id) {
//        if (StringUtils.isEmpty(id)
//        ) {
//            throw new CustomException(CommonCode.FAIL);
//        }
//        //1?????????id??????cmsPage??????
//        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(id);
//        if (!cmsPageOptional.isPresent()) {
//            CastException.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
//        }
//        CmsPage cmsPage = cmsPageOptional.get();
//        String dataUrl = cmsPage.getDataUrl();
//        //2???????????????dataUrl???????????????????????????
////        Map model = this.getModel(dataUrl);
//
//        String templateId = cmsPage.getTemplateId();
//        //3???????????????????????????
//        String templateContent = this.getTemplateById(templateId);
//        //4?????????Freemarker????????????html
//        String htmlContent = this.genericHtml(model, templateContent);
//        return htmlContent;
//    }
//
//        //2.????????????dataurl???????????????????????????
//    private Map getModel(String dataUrl){
//        if (StringUtils.isEmpty(dataUrl)){
//            CastException.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
//        }
//        Map map = restTemplate.getForObject(dataUrl, Map.class);
//        return map;
//    }
//    //3.????????????????????????
//    private  String getTemplateById(String templateId){
//        if (StringUtils.isEmpty(templateId)){
//            CastException.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
//        }
//        //??????id??????cmsTemplate??????
//        Optional<CmsTemplate> cmsTemplateOptinal = cmsTemplateRepository.findById(templateId);
//        if (!cmsTemplateOptinal.isPresent()){
//            CastException.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
//        }
//        CmsTemplate cmsTemplate = cmsTemplateOptinal.get();
//        //???Cmstemplate??????????????????id
//        String templateFileId = cmsTemplate.getTemplateFileId();
//        //??????????????????id??????????????????
//        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
//        //??????gridFSBucket????????????
//        GridFSDownloadStream gridFSDownloadStream = gridFSBucket
//                .openDownloadStream(gridFSFile.getObjectId());
//        //??????gridfsresoure,???????????????
//        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
//        try {
//            InputStream inputStream = gridFsResource.getInputStream();
//            String templateContent = IOUtils.toString(inputStream, "utf-8");
//            return templateContent;
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            e.printStackTrace();
//            CastException.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
//        }
//        return null;
//    }
//    //4?????????Freemarker????????????html
//    private String genericHtml(Map model,String templateContent){
//        //??????freemarker???Configuration
//        Configuration configuration = new Configuration(Configuration.getVersion());
//        //????????????
//        configuration.setDefaultEncoding("utf-8");
//        //???????????????????????????????????????
//        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
//        //????????????????????????
//        stringTemplateLoader.putTemplate("template",templateContent);
//        //??????????????????Configuration
//        configuration.setTemplateLoader(stringTemplateLoader);
//        //??????configuration????????????
//        try {
//            Template template = configuration.getTemplate("template");
//            //?????????????????????????????????????????????html
//            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
//            return htmlContent;
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e.getMessage());
//            CastException.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
//        }
//        return null;
//    }
//
//    public ResponseResult postPage(String id) {
//        //?????????
//        String htmlContent = this.preview(id);
//        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(id);
//        CmsPage cmsPage = cmsPageOptional.orElse(null);
//        //???html?????????gridfs
//        try {
//            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
//            String htmlFileId = gridFsTemplate.store(inputStream, cmsPage.getPageName()).toHexString();
//            cmsPage.setHtmlFileId(htmlFileId);
//            cmsPageRepository.save(cmsPage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("pageId",cmsPage.getPageId());
//
////        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CMS_POSTPAGE,cmsPage.getSiteId(), JSON.toJSONString(map));
//        return new ResponseResult(CommonCode.SUCCESS);
//
//
        }
