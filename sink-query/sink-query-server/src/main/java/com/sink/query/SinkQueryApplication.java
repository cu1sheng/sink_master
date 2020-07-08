package com.sink.query;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by cs on 2020/7/7
 */
@SpringBootApplication
public class SinkQueryApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SinkQueryApplication.class).web(WebApplicationType.NONE).run(args);
    }
}
