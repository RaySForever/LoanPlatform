package xyz.raysmen.lp.core.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.BorrowInfo;
import xyz.raysmen.lp.core.pojo.vo.BorrowInfoApprovalVO;
import xyz.raysmen.lp.core.service.BorrowInfoService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息管理 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController {
    private final BorrowInfoService borrowInfoService;

    @Autowired
    public AdminBorrowInfoController(BorrowInfoService borrowInfoService) {
        this.borrowInfoService = borrowInfoService;
    }

    /**
     * 借款信息列表
     */
    @GetMapping("/list")
    public CustomResult list() {
        List<BorrowInfo> list = borrowInfoService.selectList();
        return CustomResult.ok().data("list", list);
    }

    /**
     * 获取借款信息
     *
     * @param id    借款id
     */
    @GetMapping("/show/{id}")
    public CustomResult show(@PathVariable Long id) {
        Map<String, Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return CustomResult.ok().data("borrowInfoDetail", borrowInfoDetail);
    }

    /**
     * 审批借款信息
     */
    @PostMapping("/approval")
    public CustomResult approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO) {
        borrowInfoService.approval(borrowInfoApprovalVO);
        return CustomResult.ok().message("审批完成");
    }
}
