package xyz.raysmen.lp.core.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * BorrowerAttachVO
 * 借款人附件资料
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.pojo.vo
 * @date 2022/07/16 14:38
 */
@Getter
@Setter
@ToString
public class BorrowerAttachVO {
    /**
     * 图片类型（idCard1：身份证正面，idCard2：身份证反面，house：房产证，car：车）
     */
    private String imageType;

    /**
     * 图片路径
     */
    private String imageUrl;
}
