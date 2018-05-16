import com.asiainfo.sec.SmsEventCenterApplication;
import com.asiainfo.sec.model.ConstantPojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by PuMg on 2017/11/2/0002.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsEventCenterApplication.class)
public class SmsEventCenterTest {

    @Test
    public void test(){
        System.out.println("白名单："+ConstantPojo.getWhiteList().toString());
    }
}
