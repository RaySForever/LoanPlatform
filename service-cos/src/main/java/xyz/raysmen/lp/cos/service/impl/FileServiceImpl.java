package xyz.raysmen.lp.cos.service.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Credentials;
import com.tencent.cloud.Response;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import xyz.raysmen.lp.common.exception.BusinessException;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.cos.service.FileService;
import xyz.raysmen.lp.cos.util.CosProperties;

import java.io.InputStream;
import java.util.TreeMap;
import java.util.UUID;

/**
 * FileServiceImpl
 * 对象存储服务实现类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.cos.service.impl
 * @date 2022/06/01 21:16
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    /**
     * 文件上传至腾讯云COS，文件大小一般应在20M以内，最大不超过5G
     *
     * @param inputStream 输入流
     * @param module      上传至的模块名
     * @param fileName    上传文件名
     * @return 返回对象地址
     */
    @Override
    public String upload(InputStream inputStream, String module, String fileName) {
        // 调用 COS 接口之前必须保证本进程存在一个 COSClient 实例，如果没有则创建
        COSClient cosClient = createCOSClient();

        //构建日期路径：avatar/2019/02/26/文件名
        String folder = new DateTime().toString("yyyy/MM/dd");

        //文件名更改为：uuid.扩展名
        fileName = UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));

        // 对象键(Key)是对象在存储桶中的唯一标识。
        String key = module + "/" + folder + "/" + fileName;

        PutObjectRequest putObjectRequest = new PutObjectRequest(CosProperties.BUCKET_NAME, key, inputStream, new ObjectMetadata());

        try {
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            log.info("返回上传的请求ID为{}", putObjectResult.getRequestId());

            //阿里云文件绝对路径
            return "https://" + CosProperties.BUCKET_NAME + ".cos." + CosProperties.REGION + ".myqcloud.com/" + key;
        } catch (CosServiceException e) {
            log.error("请求已正确传输到服务，但COS服务无法处理", e);
            throw new BusinessException(e, ResponseEnum.TENCENTCLOUD_COS_SERVICE_ERROR);
        } catch (CosClientException e) {
            log.error("无法从服务获得响应或客户端无法解析来自服务的响应", e);
            throw new BusinessException(e, ResponseEnum.TENCENTCLOUD_COS_CLIENT_ERROR);
        } finally {
            // 确认本进程不再使用 cosClient 实例之后，关闭之
            cosClient.shutdown();
        }
    }

    /**
     * 根据对象地址删除文件
     *
     * @param url cos中的文件的对象地址
     */
    @Override
    public void removeFile(String url) {
        // 调用 COS 接口之前必须保证本进程存在一个 COSClient 实例，如果没有则创建
        COSClient cosClient = createCOSClient();

        // 对象键(Key)是对象在存储桶中的唯一标识。详情请参见 [对象键](https://cloud.tencent.com/document/product/436/13324)
        String key = url.substring(url.lastIndexOf("myqcloud.com") + 13);

        try {
            cosClient.deleteObject(CosProperties.BUCKET_NAME, key);
        } catch (CosServiceException e) {
            log.error("请求已正确传输到服务，但COS服务无法处理", e);
            throw new BusinessException(e, ResponseEnum.TENCENTCLOUD_COS_SERVICE_ERROR);
        } catch (CosClientException e) {
            log.error("无法从服务获得响应或客户端无法解析来自服务的响应", e);
            throw new BusinessException(e, ResponseEnum.TENCENTCLOUD_COS_CLIENT_ERROR);
        } finally {
            // 确认本进程不再使用 cosClient 实例之后，关闭之
            cosClient.shutdown();
        }
    }

    /**
     * 创建 COSClient 实例，这个实例用来后续调用请求
     *
     * @return 返回 COSClient 实例
     */
    private COSClient createCOSClient() {
        // 获取临时秘钥
        Credentials tempKey = getTempKey();
        // 这里需要已经获取到临时密钥的结果。
        // 临时密钥的生成参考 https://cloud.tencent.com/document/product/436/14048#cos-sts-sdk
        COSCredentials cred = new BasicSessionCredentials(tempKey.tmpSecretId, tempKey.tmpSecretKey, tempKey.sessionToken);

        // ClientConfig 中包含了后续请求 COS 的客户端设置：
        ClientConfig clientConfig = new ClientConfig();

        // 设置 bucket 的地域
        // COS_REGION 请参照 https://cloud.tencent.com/document/product/436/6224
        clientConfig.setRegion(new Region(CosProperties.REGION));

        // 设置请求协议, http 或者 https
        // 5.6.53 及更低的版本，建议设置使用 https 协议
        // 5.6.54 及更高版本，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);

        // 以下的设置，是可选的：

        // 设置 socket 读取超时，默认 30s
        clientConfig.setSocketTimeout(30 * 1000);
        // 设置建立连接超时，默认 30s
        clientConfig.setConnectionTimeout(30 * 1000);

        // 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }

    private Credentials getTempKey() {

        TreeMap<String, Object> config = new TreeMap<>();

        try {
            //这里的 SecretId 和 SecretKey 代表了用于申请临时密钥的永久身份（主账号、子账号等），子账号需要具有操作存储桶的权限。
            // 替换为您的云 api 密钥 SecretId
            config.put("secretId", CosProperties.SECRET_ID);
            // 替换为您的云 api 密钥 SecretKey
            config.put("secretKey", CosProperties.SECRET_KEY);

            // 临时密钥有效时长，单位是秒，默认 1800 秒，目前主账号最长 2 小时（即 7200 秒），子账号最长 36 小时（即 129600）秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", CosProperties.BUCKET_NAME);
            // 换成 bucket 所在地区
            config.put("region", CosProperties.REGION);

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径
            // 列举几种典型的前缀授权场景：
            // 1、允许访问所有对象："*"
            // 2、允许访问指定的对象："a/a1.txt", "b/b1.txt"
            // 3、允许访问指定前缀的对象："a*", "a/*", "b/*"
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefixes", new String[] {
                    "*"
            });

            // 密钥的权限列表。必须在这里指定本次临时密钥所需要的权限。
            // 简单上传、表单上传和分块上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    //查询对象列表
                    "name/cos:GetBucket",
                    //简单上传操作
                    "name/cos:PutObject",
                    //表单上传对象
                    "name/cos:PostObject",
                    //分块上传：初始化分块操作
                    "name/cos:InitiateMultipartUpload",
                    //分块上传：List 进行中的分块上传
                    "name/cos:ListMultipartUploads",
                    //分块上传：List 已上传分块操作
                    "name/cos:ListParts",
                    //分块上传：上传分块块操作
                    "name/cos:UploadPart",
                    //分块上传：完成所有分块上传操作
                    "name/cos:CompleteMultipartUpload",
                    //取消分块上传操作
                    "name/cos:AbortMultipartUpload",
                    //删除单个对象
                    "name/cos:DeleteObject"
            };
            config.put("allowActions", allowActions);

            Response response = CosStsClient.getCredential(config);

            log.info("成功生成临时秘钥！");

            return response.credentials;
        } catch (Exception e) {
            log.error("生成临时秘钥失败！", e);
            throw new BusinessException(e, ResponseEnum.TENCENTCLOUD_TEMP_KEY_ERROR);
        }
    }
}
