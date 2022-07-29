package xyz.raysmen.lp.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Getter
@Setter
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Integer STATUS_NORMAL = 1;
    public static final Integer STATUS_LOCKED = 0;
    public static final String USER_AVATAR =
            "https://lp-file-1308899945.cos.ap-guangzhou.myqcloud.com/avatar/default_avatar.png";

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 1：出借人 2：借款人
     */
    private Integer userType;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 用户密码
     */
    @TableField("`password`")
    private String password;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户姓名
     */
    @TableField("`name`")
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 微信用户标识openid
     */
    private String openid;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 绑定状态（0：未绑定，1：绑定成功 -1：绑定失败）
     */
    private Integer bindStatus;

    /**
     * 借款人认证状态（0：未认证 1：认证中 2：认证通过 -1：认证失败）
     */
    private Integer borrowAuthStatus;

    /**
     * 绑定账户协议号
     */
    private String bindCode;

    /**
     * 用户积分
     */
    private Integer integral;

    /**
     * 状态（0：锁定 1：正常）
     */
    @TableField("`status`")
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除(1:已删除，0:未删除)
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
