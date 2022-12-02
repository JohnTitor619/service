package com.yh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
public class HelloController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("hello")
    public String toHello(Map map) {
        map.put("name","张三");
        return "hello";
    }

    @GetMapping("toBanner")
    public String toBanner(Map map){
        ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:31001/cms/getmodel/5a791725dd573c3574ee333f",Map.class);
        Map body = entity.getBody();
        map.putAll(body);
        return "index_banner";
    }

    @GetMapping("toCourse")
    public String toCourse(Map map){
        ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:31200/course/courseview/297e7c7c62b888f00162b8a7dec20000",Map.class);
        Map body = entity.getBody();
        map.putAll(body);
        return "course";
    }

    @GetMapping("stu/list")
    public Map toStuList(){
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
