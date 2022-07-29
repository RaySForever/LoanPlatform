package xyz.raysmen.lp.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegexValidateUtils
 * 常用正则表达式验证工具类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.common.util
 * @date 2022/05/31 21:58
 */
public class RegexValidateUtils {

    protected static boolean flag = false;

    public static boolean check(String str, String regex) {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证邮箱
     *
     * @param email 需要验证的邮箱
     * @return 验证邮箱成功与否
     */
    public static boolean checkEmail(String email) {
        String regex = "^\\w+[-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$ ";
        return check(email, regex);
    }

    /**
     * 验证手机号码
     *
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     *
     * @param cellphone 需要验证的手机号码
     * @return 验证手机号码成功与否
     */
    public static boolean checkCellphone(String cellphone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        return check(cellphone, regex);
    }

    /**
     * 验证固话号码
     *
     * @param telephone 需要验证的固话号码
     * @return 验证固话号码成功与否
     */
    public static boolean checkTelephone(String telephone) {
        String regex = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
        return  check(telephone, regex);
    }

    /**
     * 验证传真号码
     *
     * @param fax 需要验证的传真号码
     * @return 验证传真号码成功与否
     */
    public static boolean checkFax(String fax) {
        String regex = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
        return check(fax, regex);
    }

    /**
     * 验证QQ号码
     *
     * @param QQ 需要验证的QQ号码
     * @return 验证QQ号码成功与否
     */
    public static boolean checkQQ(String QQ) {
        String regex = "^[1-9][0-9]{4,} $";
        return check(QQ, regex);
    }
}
