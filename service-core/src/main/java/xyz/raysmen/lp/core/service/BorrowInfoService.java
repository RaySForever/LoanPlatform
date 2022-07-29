package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.BorrowInfo;
import xyz.raysmen.lp.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface BorrowInfoService extends IService<BorrowInfo> {
    /**
     * 通过用户ID获取借款额度
     *
     * @param userId    用户ID
     * @return          返回该用户的借款额度
     */
    BigDecimal getBorrowAmount(Long userId);

    /**
     * 保存当前借款申请信息
     *
     * @param borrowInfo    借款申请信息
     * @param userId        用户ID
     */
    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    /**
     * 通过用户ID获取借款申请审批状态
     *
     * @param userId    用户ID
     * @return          返回借款申请审批状态
     */
    Integer getStatusByUserId(Long userId);

    /**
     * 获取借款信息列表
     *
     * @return  返回借款信息列表
     */
    List<BorrowInfo> selectList();

    /**
     * 通过借款id获取借款信息详情表
     *
     * @param id    借款id
     * @return      返回借款信息详情表
     */
    Map<String, Object> getBorrowInfoDetail(Long id);

    /**
     * 审批借款信息
     *
     * @param borrowInfoApprovalVO  借款审批信息
     */
    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
