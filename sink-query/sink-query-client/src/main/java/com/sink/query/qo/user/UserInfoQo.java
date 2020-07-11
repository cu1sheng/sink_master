package com.sink.query.qo.user;

import com.sink.comm.vo.Pager;
import com.sink.query.vo.UserInfoVo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by SINK on 2020/7/11
 */
@Data
public class UserInfoQo implements Serializable {

    private static final long serialVersionUID = 8535854720074933754L;
    @NotNull(message = "分页参数不能为空")
    private Pager<UserInfoVo> page;
}
