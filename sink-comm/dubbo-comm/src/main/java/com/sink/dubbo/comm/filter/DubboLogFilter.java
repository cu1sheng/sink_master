package com.sink.dubbo.comm.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.sink.dubbo.comm.vo.InvokeLog;

/**
 * @title:
 * @author: gongsd
 * @time: 2020/7/2.
 */
@Activate
public class DubboLogFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(DubboLogFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        InvokeLog invokeLog = new InvokeLog();

        Class<?> serviceType = invoker.getInterface();

        invokeLog.setMethodName(invocation.getMethodName());
        invokeLog.setServiceName(serviceType.getName());
        invokeLog.setConsumerIp(RpcContext.getContext().getRemoteHost());
        invokeLog.setProviderIp(RpcContext.getContext().getLocalHost());

        Result result = invoker.invoke(invocation);

        if(!invocation.getMethodName().contains("List") && !invocation.getMethodName().contains("list")){
            String returnStr = JSON.toJSONString(result.getValue());
            invokeLog.setResponseData(returnStr);
        }

        invokeLog.setResponseTime(System.currentTimeMillis() - invokeLog.getStartTime().getTime());

        logger.info(String.format("dubbo log filter [%s]", JSON.toJSONString(invokeLog)));
        return result;
    }
}
