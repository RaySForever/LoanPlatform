package xyz.raysmen.lp.cos.service;

import java.io.InputStream;

/**
 * FileService
 * 文件上传业务服务类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.cos.service
 * @date 2022/06/01 20:51
 */
public interface FileService {
    /**
     * 文件上传至腾讯云COS，文件大小一般应在20M以内，最大不超过5G
     *
     * @param inputStream   输入流
     * @param module        上传至的模块名
     * @param fileName      上传文件名
     * @return              返回对象地址
     */
    String upload(InputStream inputStream, String module, String fileName);

    /**
     * 根据对象地址删除文件
     *
     * @param url   cos中的文件的对象地址
     */
    void removeFile(String url);
}
