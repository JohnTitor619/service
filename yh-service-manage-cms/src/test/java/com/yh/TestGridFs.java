package com.yh;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.yh.manage.cms.CmsApplication;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = CmsApplication.class)
@RunWith(SpringRunner.class)
public class TestGridFs {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    //把模板文件上传到GridFS
    @Test
    public void uploadTemplateFile2GridFS() throws FileNotFoundException {
        //获取要上传的模板文件所在目录的路径
        String path = this.getClass().getResource("/").getPath();
        //创建文件对象
        File file = new File(path+"course.ftl");
        //创建文件输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        //上传
        //ObjectId objectId = gridFsTemplate.store(fileInputStream, "yts_index_banner.ftl");

        ObjectId objectId = gridFsTemplate.store(fileInputStream, "yts_course.ftl");
        //634e129fdae5e62e2c8bef63
        System.out.println(objectId);
    }

    //从GridFS下载模板文件
    @Test
    public void downFile4GridFS() throws IOException {
        //1查询要下载的模板文件
        GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("634e129fdae5e62e2c8bef63")));
        //下载
        //通过GridFSBucket打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
        //通过GridFsResource操作GridFSDownloadStream
        GridFsResource gridFsResource = new GridFsResource(gridFsFile, gridFSDownloadStream);
        //通过GridFsResource获得下载流
        InputStream inputStream = gridFsResource.getInputStream();
        //String templateContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        //System.out.println(templateContent);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\Test\\banner.ftl"));
        IOUtils.copy(inputStream,fileOutputStream);

    }
}
