package xyz.raysmen.lp.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * <p>
 * 标的编号生成工具类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public class LendNoUtils {

    /**
     * 定义的随机数个数
     */
    private static final int RANDOM_NUMBER = 3;

    public static String getNo() {

        LocalDateTime time=LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String strDate = dtf.format(time);

        StringBuilder stringBuilder = new StringBuilder(strDate);
        Random random = new Random();
        for (int i = 0; i < RANDOM_NUMBER; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }

    public static String getLendNo() {

        return "LEND" + getNo();
    }

    public static String getLendItemNo() {

        return "INVEST" + getNo();
    }

    public static String getLoanNo() {

        return "LOAN" + getNo();
    }

    public static String getReturnNo() {
        return "RETURN" + getNo();
    }


    public static Object getWithdrawNo() {
        return "WITHDRAW" + getNo();
    }


    public static String getReturnItemNo() {
        return "RETURNITEM" + getNo();
    }


    public static String getChargeNo() {

        return "CHARGE" + getNo();
    }

    /**
     * 获取交易编码
     */
    public static String getTransNo() {
        return "TRANS" + getNo();
    }

}