package xyz.raysmen.lp.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.Borrower;
import xyz.raysmen.lp.core.pojo.vo.BorrowerApprovalVO;
import xyz.raysmen.lp.core.pojo.vo.BorrowerDetailVO;
import xyz.raysmen.lp.core.service.BorrowerService;

/**
 * <p>
 * 借款人管理 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {
    private final BorrowerService borrowerService;

    @Autowired
    public AdminBorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    /**
     * 获取借款人分页列表
     * @param page      当前页码
     * @param limit     每页记录数
     * @param keyword   查询关键字
     */
    @GetMapping("/list/{page}/{limit}")
    public CustomResult listPage(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 @RequestParam(required = false) String keyword) {
        Page<Borrower> pageParam = new Page<>(page, limit);

        IPage<Borrower> pageModel = borrowerService.listPage(pageParam, keyword);
        return CustomResult.ok().data("pageModel", pageModel);
    }

    /**
     * 获取借款人信息
     *
     * @param id    借款人id
     */
    @GetMapping("/show/{id}")
    public CustomResult show(@PathVariable Long id) {
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);

        return CustomResult.ok().data("borrowerDetailVO", borrowerDetailVO);
    }

    /**
     * 借款额度审批
     */
    @PostMapping("/approval")
    public CustomResult approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO) {
        borrowerService.approval(borrowerApprovalVO);

        return CustomResult.ok().message("审批完成");
    }
}
