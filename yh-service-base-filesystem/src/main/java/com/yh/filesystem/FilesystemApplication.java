package com.yh.filesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @AUTHOR: yadong
 * @DATE: 2021/7/19 15:32
 * @DESC:
 */
@SpringBootApplication
@EntityScan("com.lxw.framework.domain.filesystem")
@ComponentScan(basePackages = {"com.lxw.api.filesystem","com.lxw.api.config"})
@ComponentScan(basePackages = {"com.yh.filesystem"})
//@ComponentScan(basePackages = {"com.lxw.framework.exception"})
public class FilesystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(FilesystemApplication.class, args);
    }
}
