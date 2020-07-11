package com.sink.query;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by SINK on 2020/7/7
 */
@DubboComponentScan("com.sink.query.service")
@MapperScan(basePackages = "com.sink.query.dao")
@SpringBootApplication
public class SinkQueryApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SinkQueryApplication.class).web(WebApplicationType.NONE).run(args);
    }
}
