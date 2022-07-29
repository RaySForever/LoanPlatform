package xyz.raysmen.lp.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Getter
@Setter
@ToString
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上级id
     */
    private Long parentId;

    /**
     * 名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 值
     */
    @TableField("`value`")
    private Integer value;

    /**
     * 编码
     */
    private String dictCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记（0:不可用 1:可用）
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    /**
     * 是否包含子节点（在数据库表中忽略此列）
     */
    @TableField(exist = false)
    private boolean hasChildren;


}
