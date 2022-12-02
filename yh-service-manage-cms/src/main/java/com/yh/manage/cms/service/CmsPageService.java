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
        //创建对象
        CmsPage cmsPage = new CmsPage();
//        BeanUtils.copyProperties(queryPageRequest,cmsPage);
        //创建条件匹配对象
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

        //创建了Example,存放值对象和条件匹配器
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //设置了分页
        Pageable pageable = PageRequest.of(page, size);
        //执行查询
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        //获取总条数
        long totalElements = all.getTotalElements();
        //获取查询列表信息
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

    //生成静态html页面
    public String getPageHtml(String pageId){
        //数据校验
        if (StringUtils.isEmpty(pageId)){
            throw new CustomException(CommonCode.VALIDATA);
        }
        //根据cmsPageId获取cmsPage信息
        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(pageId);
        if (!cmsPageOptional.isPresent()) {
            throw new CustomException(CmsCode.CMS_PAGE_EXISTSID);
        }
        CmsPage cmsPage = cmsPageOptional.get();
        String templateId = cmsPage.getTemplateId();
        String dataUrl = cmsPage.getDataUrl();
        //通过cmsPage获取cmsTemplateId，获取模板文件内容
        String templateContent = this.getTemplateContent(templateId);
        //通过cmsPage获取dataUrl，远程获取模型数据
        Map modelData = this.getModelDataByDataUrl(dataUrl);
        //通过freemarker生成静态页面
        String htmlContent = this.genarateHtml(templateContent, modelData);
        return htmlContent;
    }
    //通过freemarker生成静态页面
    private String genarateHtml(String templateContent,Map model){
        //创建配置类对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDefaultEncoding("utf-8");
        //创建字符串模板加载器对象，加载字符串模板
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template = configuration.getTemplate("template");
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return htmlContent;
        } catch (Exception e) {
            log.error("生成html异常,{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //通过cmsPage获取dataUrl，远程获取模型数据
    private Map getModelDataByDataUrl(String dataUrl){
        if (StringUtils.isEmpty(dataUrl)) {
            throw new CustomException(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        Map map = restTemplate.getForObject(dataUrl, Map.class);
        return map;
    }
    //获取模板文件内容
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
            log.error("从gridFS下载模板异常{}",e.getMessage());
            throw new CustomException(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

    }

    public ResponseResult postPage(String id){
        //1.生成html内容
        String pageHtml = this.getPageHtml(id);
        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(id);
        CmsPage cmsPage = cmsPageOptional.orElse(null);
        try {
            InputStream inputStream = IOUtils.toInputStream(pageHtml, "utf-8");
            //2.把生成的html上传到GridFS上
            String htmlFileId = gridFsTemplate.store(inputStream,cmsPage.getPageName()).toHexString();
            cmsPage.setHtmlFileId(htmlFileId);
            //3.更新cmsPage信息，持久化htmlFileId
            cmsPageRepository.save(cmsPage);
            Map<String, String> map = new HashMap<String, String>();
            map.put("pageId",id);
            //4.发送消息
            rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,
                    cmsPage.getSiteId(),JSON.toJSONString(map));
            return new ResponseResult(CommonCode.SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CmsPageResult saveCmsPage(CmsPage cmsPage) {
        //数据校验
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

    //一键发布
    public ResponseResult quickPostPage(CmsPage cmsPage) {
        //1.保存或更新cmsPage信息
        CmsPageResult cmsPageResult = this.saveCmsPage(cmsPage);
        //2.发布
        if (cmsPageResult.isSuccess()){
            String pageId = cmsPageResult.getCmsPage().getPageId();
            ResponseResult responseResult = this.postPage(pageId);
            return responseResult;
        }
        return new ResponseResult(CommonCode.FAIL);
    }












        //根据id查询页面
    public CmsPage getById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
        //返回空return null;
    }

    //更新页面信息
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //根据id查询页面信息
        CmsPage one = this.getById(id);
        if (one != null) {
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //返回失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    //删除页面
    public ResponseResult delete(String id) {
        CmsPage one = this.getById(id);
        if (one != null) {
            //删除页面
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


//    //页面预览
//    public String preview(String id) {
//        if (StringUtils.isEmpty(id)
//        ) {
//            throw new CustomException(CommonCode.FAIL);
//        }
//        //1、根据id获取cmsPage信息
//        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(id);
//        if (!cmsPageOptional.isPresent()) {
//            CastException.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
//        }
//        CmsPage cmsPage = cmsPageOptional.get();
//        String dataUrl = cmsPage.getDataUrl();
//        //2、远程调用dataUrl，获取页面模型信息
////        Map model = this.getModel(dataUrl);
//
//        String templateId = cmsPage.getTemplateId();
//        //3、获取页面模板信息
//        String templateContent = this.getTemplateById(templateId);
//        //4、通过Freemarker生成静态html
//        String htmlContent = this.genericHtml(model, templateContent);
//        return htmlContent;
//    }
//
//        //2.远程调用dataurl，获取页面模型信息
//    private Map getModel(String dataUrl){
//        if (StringUtils.isEmpty(dataUrl)){
//            CastException.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
//        }
//        Map map = restTemplate.getForObject(dataUrl, Map.class);
//        return map;
//    }
//    //3.获取页面模板信息
//    private  String getTemplateById(String templateId){
//        if (StringUtils.isEmpty(templateId)){
//            CastException.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
//        }
//        //根据id获取cmsTemplate信息
//        Optional<CmsTemplate> cmsTemplateOptinal = cmsTemplateRepository.findById(templateId);
//        if (!cmsTemplateOptinal.isPresent()){
//            CastException.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
//        }
//        CmsTemplate cmsTemplate = cmsTemplateOptinal.get();
//        //从Cmstemplate获取模板文件id
//        String templateFileId = cmsTemplate.getTemplateFileId();
//        //根据模板文件id查询文件信息
//        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
//        //通过gridFSBucket打开下载
//        GridFSDownloadStream gridFSDownloadStream = gridFSBucket
//                .openDownloadStream(gridFSFile.getObjectId());
//        //创建gridfsresoure,操作下载流
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
//    //4、通过Freemarker生成静态html
//    private String genericHtml(Map model,String templateContent){
//        //创建freemarker的Configuration
//        Configuration configuration = new Configuration(Configuration.getVersion());
//        //设置编码
//        configuration.setDefaultEncoding("utf-8");
//        //创建字符串类型的模板加载器
//        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
//        //把模板放入加载器
//        stringTemplateLoader.putTemplate("template",templateContent);
//        //把加载器放入Configuration
//        configuration.setTemplateLoader(stringTemplateLoader);
//        //通过configuration获取模板
//        try {
//            Template template = configuration.getTemplate("template");
//            //把模型数据渲染到模板，生成静态html
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
//        //静态化
//        String htmlContent = this.preview(id);
//        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(id);
//        CmsPage cmsPage = cmsPageOptional.orElse(null);
//        //把html上传到gridfs
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
