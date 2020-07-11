package com.sink.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sink.comm.util.JsonUtil;
import com.sink.comm.util.MD5Util;
import com.sink.comm.vo.Pager;
import com.sink.comm.vo.ReturnDO;
import com.sink.query.qo.user.UserInfoQo;
import com.sink.query.service.UserInfoService;
import com.sink.query.vo.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 服务接口测试
 * Created by SINK on 2020/7/7
 */
@RestController
@RequestMapping(value = "/home")
@Slf4j
@Api(tags = "主页")
public class HomeController {

    @Reference(version = "1.0.0")
    private UserInfoService userInfoService;

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ApiOperation(value = "访问测试",httpMethod = "GET")
    public void init(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String compute = MD5Util.compute("1234");
        writer.println("this is Spring Boot:" + compute);
        UserInfoQo infoQo = new UserInfoQo();
        infoQo.setPage(new Pager<>());
        ReturnDO<Pager<UserInfoVo>> returnDO = userInfoService.getUserListPage(infoQo);
        writer.println("用户列表："+ JsonUtil.toJson(returnDO.getObj()));
    }
}
