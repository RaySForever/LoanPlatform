package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.BorrowerAttach;
import xyz.raysmen.lp.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {
    /**
     * 通过借款人ID获取获取附件VO列表
     *
     * @param borrowerId    借款人ID
     * @return              返回对应的附件VO列表
     */
    List<BorrowerAttachVO> selectBorrowerAttachVOList(Long borrowerId);
}
