package com.yh;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.sun.deploy.net.URLEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.UUID;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TestQiniu {
    @Test
    public void testUploadFile2Qiniu() {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region1());
//        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "A6xqJusVK04iJOsUg4hYw1XqowKqKZws1AEJtmJG";
        String secretKey = "2zMkGr8zIf2Inz7Qzr1CudjzqyRduiNuYJzPytSY";
        String bucket = "syq-sps";
//如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = this.getClass().getResource("/static/").getPath()+ "2.JPG";
//        String localFilePath = "/home/qiniu/test.png";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = UUID.randomUUID().toString();
        //认证
        Auth auth = Auth.create(accessKey, secretKey);
        //指定上传空间
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }
    @Test
    public void getdownloadurl() throws UnsupportedEncodingException {
        String fileName = "FmK-evno2FF8AVeKWMRDp0Q05dZE";
        String domainOfBucket = "http://rk2zhusqb.hb-bkt.clouddn.com";
        String encodedFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        String accessKey = "A6xqJusVK04iJOsUg4hYw1XqowKqKZws1AEJtmJG";
        String secretKey = "2zMkGr8zIf2Inz7Qzr1CudjzqyRduiNuYJzPytSY";
        Auth auth = Auth.create(accessKey, secretKey);
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        System.out.println(finalUrl);
    }

}