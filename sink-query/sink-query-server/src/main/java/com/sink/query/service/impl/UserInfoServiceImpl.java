package com.sink.query.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sink.comm.vo.Pager;
import com.sink.comm.vo.ReturnDO;
import com.sink.query.dao.UserInfoDao;
import com.sink.query.qo.user.UserInfoQo;
import com.sink.query.service.UserInfoService;
import com.sink.query.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by SINK on 2020/7/11
 */
@Slf4j
@Service(version = "1.0.0")
@Component
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public ReturnDO<Pager<UserInfoVo>> getUserListPage(@NotNull(message = "用户信息查询参数不能为空") UserInfoQo userInfoQo) {
        ReturnDO<Pager<UserInfoVo>> returnDO = new ReturnDO<>();
        try {
            returnDO.setObj(userInfoQo.getPage());
            List<UserInfoVo> voList = userInfoDao.getUserListPage(userInfoQo);
            returnDO.getObj().setResult(voList);
        } catch (Exception e) {
            log.error("用户信息列表查询发生异常", e);
            returnDO.setErrorMsg("999999", "用户信息列表查询失败");
        }
        return returnDO;
    }
}
