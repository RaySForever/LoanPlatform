package xyz.raysmen.lp.core.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.BorrowInfo;
import xyz.raysmen.lp.core.service.BorrowInfoService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/borrowInfo")
public class BorrowInfoController {
    private final BorrowInfoService borrowInfoService;

    @Autowired
    public BorrowInfoController(BorrowInfoService borrowInfoService) {
        this.borrowInfoService = borrowInfoService;
    }

    /**
     * 获取借款额度
     */
    @GetMapping("/auth/getBorrowAmount")
    public CustomResult getBorrowAmount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return CustomResult.ok().data("borrowAmount", borrowAmount);
    }

    /**
     * 提交借款申请
     */
    @PostMapping("/auth/save")
    public CustomResult save(@RequestBody BorrowInfo borrowInfo, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowInfoService.saveBorrowInfo(borrowInfo, userId);
        return CustomResult.ok().message("提交成功");
    }

    /**
     * 获取借款申请审批状态
     */
    @GetMapping("/auth/getBorrowerStatus")
    public CustomResult getBorrowerStatus(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        Integer status = borrowInfoService.getStatusByUserId(userId);
        return CustomResult.ok().data("borrowInfoStatus", status);
    }
}
