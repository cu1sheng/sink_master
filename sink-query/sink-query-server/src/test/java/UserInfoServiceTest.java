import com.sink.comm.util.JsonUtil;
import com.sink.comm.vo.Pager;
import com.sink.comm.vo.ReturnDO;
import com.sink.query.SinkQueryApplication;
import com.sink.query.qo.user.UserInfoQo;
import com.sink.query.service.UserInfoService;
import com.sink.query.vo.UserInfoVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by SINK on 2020/7/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SinkQueryApplication.class)
public class UserInfoServiceTest {

    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void getUserListPage() {
        UserInfoQo userInfoQo = new UserInfoQo();
        userInfoQo.setPage(new Pager<>());
        ReturnDO<Pager<UserInfoVo>> returnDO = userInfoService.getUserListPage(userInfoQo);
        System.err.println("状态：" + returnDO.isSuccess());
        System.err.println("结果：" + JsonUtil.toJson(returnDO.getObj()));
        System.err.println("错误：" + returnDO.getErrorMsg());
    }
}
