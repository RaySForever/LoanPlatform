package xyz.raysmen.lp.sms.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * SmsPropertiesTest
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.sms.util
 * @date 2022/06/01 12:56
 */
@SpringBootTest
class SmsPropertiesTest {

    @Test
    public void propertiesTest() {
        System.out.println("测试开始！");
        System.out.println(SmsProperties.SECRET_ID);
        System.out.println(SmsProperties.SECRET_KEY);
        System.out.println(SmsProperties.ENDPOINT_DOMAIN);
        System.out.println(SmsProperties.REGION);
        System.out.println(SmsProperties.SDK_APP_ID);
        System.out.println(SmsProperties.SIGN_NAME);
        System.out.println(SmsProperties.TEMPLATE_ID);
    }

}