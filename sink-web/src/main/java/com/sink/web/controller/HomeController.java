package com.sink.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cs on 2020/7/7
 */
@RestController
@RequestMapping(value = "/home")
@Slf4j
@Api(tags = "主页")
public class HomeController {

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ApiOperation(value = "访问测试",httpMethod = "GET")
    public void init(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.println("this is Spring Boot");
    }
}
