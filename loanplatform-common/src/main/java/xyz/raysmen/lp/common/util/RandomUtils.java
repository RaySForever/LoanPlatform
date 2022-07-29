package xyz.raysmen.lp.common.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * RandomUtils
 * 生成随机四位或六位的验证码的工具类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.common.util
 * @date 2022/05/31 22:04
 */
@Slf4j
public class RandomUtils {

    private static final Random RANDOM = new Random();

    private static final DecimalFormat FOUR_DECIMAL_FORMAT = new DecimalFormat("0000");

    private static final DecimalFormat SIX_DECIMAL_FORMAT = new DecimalFormat("000000");

    public static String getFourBitRandom() {
        return FOUR_DECIMAL_FORMAT.format(RANDOM.nextInt(10000));
    }

    public static String getSixBitRandom() {
        return SIX_DECIMAL_FORMAT.format(RANDOM.nextInt(1000000));
    }

    /**
     * 给定数组，抽取n个数据
     * @param list  给定数组
     * @param n     抽取的个数
     * @return      随机值数组
     */
    public static <T> ArrayList<T> getRandom(List<T> list, int n) {

        Random random = new Random();

        final int size = list.size();

        HashMap<Object, Object> hashMap = new HashMap<>(size);

        // 生成随机数字并存入HashMap
        for (int i = 0; i < size; i++) {

            int number = random.nextInt(100) + 1;

            hashMap.put(number, i);
        }

        // 从HashMap导入数组
        Object[] randomObjs = hashMap.values().toArray();

        ArrayList<T> randomList = new ArrayList<>();

        // 遍历数组并打印数据
        for (int i = 0; i < n; i++) {
            randomList.add(list.get((int) randomObjs[i]));
        }
        log.info("获取到的随机值数组为：{}", randomList);
        return randomList;
    }

}
