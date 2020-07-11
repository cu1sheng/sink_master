package com.sink.query.dao;

import com.sink.comm.vo.ReturnDO;
import com.sink.query.qo.user.UserInfoQo;
import com.sink.query.vo.UserInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by SINK on 2020/7/11
 */
@Mapper
public interface UserInfoDao {

    /**
     * 用户信息列表查询
     * @param userInfoQo
     * @return
     */
    List<UserInfoVo> getUserListPage(UserInfoQo userInfoQo);
}
