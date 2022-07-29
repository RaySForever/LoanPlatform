package xyz.raysmen.lp.core.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ExcelDictDTO
 * 数据字典传输至Excel的DTO类
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.dto
 * @date 2022/05/22 09:30
 */
@Getter
@Setter
@ToString
public class ExcelDictDTO {
    /**
     * id
     */
    @ExcelProperty("id")
    private Long id;

    /**
     * 上级id
     */
    @ExcelProperty("上级id")
    private Long parentId;

    /**
     * 名称
     */
    @ExcelProperty("名称")
    private String name;

    /**
     * 值
     */
    @ExcelProperty("值")
    private Integer value;

    /**
     * 编码
     */
    @ExcelProperty("编码")
    private String dictCode;

}
