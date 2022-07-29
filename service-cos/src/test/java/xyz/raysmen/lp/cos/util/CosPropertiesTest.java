package xyz.raysmen.lp.cos.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * CosPropertiesTest
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.cos.util
 * @date 2022/06/06 18:24
 */
@SpringBootTest
class CosPropertiesTest {
    @Test
    public void propertiesTest() {
        System.out.println("测试开始！");
        System.out.println(CosProperties.SECRET_ID);
        System.out.println(CosProperties.SECRET_KEY);
        System.out.println(CosProperties.REGION);
        System.out.println(CosProperties.BUCKET_NAME);
        String[] keys = CosProperties.KEYS;
        for (String key : keys) {
            System.out.println(key);
        }
    }
}