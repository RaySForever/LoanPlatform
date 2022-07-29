package xyz.raysmen.lp.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.raysmen.lp.core.pojo.entity.Borrower;
import xyz.raysmen.lp.core.pojo.vo.BorrowerApprovalVO;
import xyz.raysmen.lp.core.pojo.vo.BorrowerDetailVO;
import xyz.raysmen.lp.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface BorrowerService extends IService<Borrower> {
    /**
     * 保存借款人信息
     *
     * @param borrowerVO    借款人认证信息
     * @param userId        用户ID
     */
    void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId);

    /**
     * 通过用户ID获取借款人认证状态
     *
     * @param userId    用户ID
     * @return          返回借款人认证状态，数字形式
     */
    Integer getStatusByUserId(Long userId);

    /**
     * 获取借款人分页列表
     *
     * @param pageParam 传入带有分页参数的分页对象
     * @param keyword   查询关键词
     * @return          返回带有查询结果的分页对象
     */
    IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword);

    /**
     * 通过 id 获取借款人信息
     *
     * @param id    借款人id
     * @return      返回借款人信息详情
     */
    BorrowerDetailVO getBorrowerDetailVOById(Long id);

    /**
     * 审批借款人
     *
     * @param borrowerApprovalVO    借款人审批信息
     */
    void approval(BorrowerApprovalVO borrowerApprovalVO);
}
