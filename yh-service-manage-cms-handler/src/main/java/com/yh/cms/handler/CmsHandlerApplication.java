package com.yh.cms.handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.lxw.api.cms")
@ComponentScan("com.lxw.api.config")
//@ComponentScan("com.lxw.framework.exception")
public class CmsHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsHandlerApplication.class,args);
    }
}
