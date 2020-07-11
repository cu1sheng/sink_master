package com.sink.query.service;

import com.sink.comm.vo.Pager;
import com.sink.comm.vo.ReturnDO;
import com.sink.query.qo.user.UserInfoQo;
import com.sink.query.vo.UserInfoVo;

import javax.validation.constraints.NotNull;

/**
 * Created by SINK on 2020/7/11
 */
public interface UserInfoService {

    /**
     * 用户信息列表查询-分页
     * @param userInfoQo
     * @return
     */
    ReturnDO<Pager<UserInfoVo>> getUserListPage(@NotNull(message = "用户信息查询参数不能为空") UserInfoQo userInfoQo);
}
