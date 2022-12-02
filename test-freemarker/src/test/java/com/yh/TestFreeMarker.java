package com.yh;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFreeMarker {
    //根据模板文件，通过freemarker技术生成静态html文件
    @Test
    public void genHtmlByTemplateFile() throws IOException, TemplateException {
        //指定模板文件所在路径
        String templateDirPath = this.getClass().getResource("/templates/").getPath();
        //配置类,创建配置类对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //指定模板文件所用编码
        configuration.setDefaultEncoding("utf-8");
        //指定模板文件所在目录
        configuration.setDirectoryForTemplateLoading(new File(templateDirPath));
        //通过配置类获取模板对象
        Template template = configuration.getTemplate("hello.ftl");
        //加载模板文件
        Map modelDate = this.getModelDate();
        //通过freemarker引擎生成静态html
        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, modelDate);
        System.out.println(htmlContent);
        //指定html输出位置
        String htmlFilePath = "D:\\Test\\stu.html";
        //文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream(new File(htmlFilePath));
        //文件输入流
        InputStream inputStream = IOUtils.toInputStream(htmlContent,"utf-8");
        //
        IOUtils.copy(inputStream,fileOutputStream);
        inputStream.close();
        fileOutputStream.close();

    }

    public String getTemplateContent() {
        String templateContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div>\n" +
                "        hello <#if name??>${name}</#if>\n" +
                "        <table>\n" +
                "            <tr>\n" +
                "                <td>序号</td>\n" +
                "                <td>姓名</td>\n" +
                "                <td>年龄</td>\n" +
                "                <td>钱包</td>\n" +
                "                <td>生日</td>\n" +
                "            </tr>\n" +
                "            <#list stus as stu>\n" +
                "                <tr>\n" +
                "                    <td>${stu_index+1}</td>\n" +
                "                    <td>${stu.name}</td>\n" +
                "                    <td>${stu.age}</td>\n" +
                "                    <td>${stu.money}</td>\n" +
                "                    <td>${stu.birthday?string(\"yyyy-MM-dd\")}</td>\n" +
                "                </tr>\n" +
                "            </#list>\n" +
                "            <#list stuMap?keys as k>\n" +
                "                <tr>\n" +
                "                    <td>${k_index+1}</td>\n" +
                "                    <td>${stuMap[k].name}</td>\n" +
                "                    <td>${stuMap[k].age}</td>\n" +
                "                    <td>${stuMap[k].money?c}</td>\n" +
                "                    <td>${stuMap[k].birthday?datetime}</td>\n" +
                "                </tr>\n" +
                "            </#list>\n" +
                "        </table>\n" +
                "\n" +
                "        <#assign text=\"{'bank':'工商银行','account':'10101920201920212'}\" />\n" +
                "        <#assign data=text?eval />\n" +
                "        开户行：${data.bank}  账号：${data.account}\n" +
                "    </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        return templateContent;
    }
    //根据模板字符串，通过freemarker技术生成静态html文件
    @Test
    public void genHtmlByTemplateString() throws IOException, TemplateException {
        //获取模板内容
        String templateContent = this.getTemplateContent();
        //配置类,创建配置类对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //指定模板文件所用编码
        configuration.setDefaultEncoding("utf-8");
        //创建String模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        //把StringTemplateLoader放入模板对象
        configuration.setTemplateLoader(stringTemplateLoader);
        //通过配置类获取模板对象
        Template template = configuration.getTemplate("template");
        //加载模板文件
        Map modelDate = this.getModelDate();
        //通过freemarker引擎生成静态html
        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, modelDate);
        System.out.println(htmlContent);
        //指定html输出位置
        String htmlFilePath = "D:\\Test\\stu_string.html";
        //文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream(new File(htmlFilePath));
        //文件输入流
        InputStream inputStream = IOUtils.toInputStream(htmlContent,"utf-8");
        //
        IOUtils.copy(inputStream,fileOutputStream);
        inputStream.close();
        fileOutputStream.close();

    }

    public Map getModelDate(){
        Map map = new HashMap();
        Student stu1 = new Student();
        stu1.setAge(18);
        stu1.setName("张三");
        stu1.setMoney(1000000000f);
        stu1.setBirthday(new Date());

        Student stu2 = new Student();
        stu2.setAge(19);
        stu2.setName("李四");
        stu2.setMoney(2000f);
        stu2.setBirthday(new Date());

        ArrayList<Student> list = new ArrayList<Student>();
        list.add(stu1);
        list.add(stu2);

        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("s1",stu1);
        stuMap.put("s2",stu2);

        map.put("stuMap",stuMap);
        map.put("stus",list);
        return map;
    }
}
