package xyz.raysmen.lp.core.hfb;

import lombok.extern.slf4j.Slf4j;
import xyz.raysmen.lp.common.util.HttpUtils;
import xyz.raysmen.lp.common.util.MD5;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class RequestHelper {

    public static void main(String[] args) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("d", "4");
        paramMap.put("b", "2");
        paramMap.put("c", "3");
        paramMap.put("a", "1");
    }

    /**
     * 请求数据获取签名
     *
     * @param paramMap
     * @return
     */
    public static String getSign(Map<String, Object> paramMap) {
        paramMap.remove("sign");
        // 汇付宝验签参数sign生成要求：
        // 所有变量值按照参数名（不包含sign参数）升序用|连接，最后连接signKey
        TreeMap<String, Object> sorted = new TreeMap<>(paramMap);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> param : sorted.entrySet()) {
            builder.append(param.getValue()).append("|");
        }
        builder.append(HfbConst.SIGN_KEY);
        log.info("加密前：" + builder);
        // 采用32位Md5小写（编码utf-8）加密
        String md5Str = MD5.encrypt(builder.toString());
        log.info("加密后：" + md5Str);
        return md5Str;
    }

    /**
     * Map转换
     *
     * @param paramMap
     * @return
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }

    /**
     * 签名校验
     *
     * @param paramMap
     * @return
     */
    public static boolean isSignEquals(Map<String, Object> paramMap) {
        String sign = (String) paramMap.get("sign");
        String md5Str = getSign(paramMap);
        return sign.equals(md5Str);
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 封装同步请求
     *
     * @param paramMap 接口调用参数表
     * @param url      调用地址
     * @return 返回应答数据
     */
    public static String sendRequest(Map<String, Object> paramMap, String url) {
        String result = "";
        try {
            //封装post参数
            StringBuilder postdata = new StringBuilder();
            for (Map.Entry<String, Object> param : paramMap.entrySet()) {
                postdata.append(param.getKey()).append("=")
                        .append(param.getValue()).append("&");
            }
            log.info(String.format("--> 发送请求到汇付宝：post data %1s", postdata));
            byte[] reqData = postdata.toString().getBytes(StandardCharsets.UTF_8);
            byte[] respData = HttpUtils.doPost(url, reqData);
            result = new String(respData);
            log.info(String.format("--> 汇付宝应答结果：result data %1s", result));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
