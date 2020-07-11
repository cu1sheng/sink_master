package com.sink.dubbo.comm.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @title:
 * @author: gongsd
 * @time: 2020/7/2.
 */
@Data
public class InvokeLog implements Serializable {
    private static final long serialVersionUID = 5002111899647537191L;

    private Date startTime = new Date();

    private String serviceName;

    private String methodName;

    private String consumerIp;

    private String providerIp;

    private String responseData;

    private Long responseTime;

}
