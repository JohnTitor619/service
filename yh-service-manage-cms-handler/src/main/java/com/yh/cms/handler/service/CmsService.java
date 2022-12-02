package com.yh.cms.handler.service;

import com.lxw.framework.domain.cms.CmsPage;
import com.lxw.framework.domain.cms.CmsSite;
import com.lxw.framework.domain.cms.response.CmsCode;
import com.lxw.framework.exception.CustomException;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.yh.cms.handler.dao.CmsPageRepository;
import com.yh.cms.handler.dao.CmsSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
@Slf4j
public class CmsService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    //把页面下载，并放到指定位置
    public void postPage(String pageId){
        //根据Id查询到CmsPage信息
        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(pageId);
        if (!cmsPageOptional.isPresent()) {
            throw new CustomException(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        CmsPage cmsPage = cmsPageOptional.get();
        //从CmsPage获取htmlFileId
        String htmlFileId = cmsPage.getHtmlFileId();
        //从GridFS上下载html文件
        InputStream inputStream = this.downloadHtml2GridFs(htmlFileId);
        //拼接接存放html路径
        String siteId = cmsPage.getSiteId();
        Optional<CmsSite> cmsSiteOptional = cmsSiteRepository.findById(siteId);
        if (!cmsSiteOptional.isPresent()) {
            throw new CustomException(CmsCode.CMS_PAGE_EXISTSID);
        }
        CmsSite cmsSite = cmsSiteOptional.get();
        String pagePath = cmsSite.getSitePhysicalPath()+cmsPage.getPagePhysicalPath()
                +cmsPage.getPageName();
        //把html输出到指定位置
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
            inputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw  new RuntimeException(e);
        }
    }
    //从GridFS上下载html文件
    private InputStream downloadHtml2GridFs(String htmlFileId){
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        //通过gridFSBucket获取GridFSDownloadStream
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        try {
            InputStream inputStream = gridFsResource.getInputStream();
            return inputStream;
        } catch (IOException e) {
            log.error("下载html异常:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
