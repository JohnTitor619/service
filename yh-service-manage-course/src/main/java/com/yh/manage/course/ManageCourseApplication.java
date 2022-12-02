package com.yh.manage.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootApplication
@EnableFeignClients//启用OpenFeign
@EntityScan("com.lxw.framework.domain.course")//扫描实体类
@ComponentScan(basePackages={"com.lxw.api"})//扫描接口
@ComponentScan(basePackages={"com.yh.manage.course"})
@ComponentScan(basePackages={"com.lxw.framework"})//扫描common下的所有类
public class ManageCourseApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ManageCourseApplication.class, args);
    }
}
