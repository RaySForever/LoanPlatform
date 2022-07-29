package xyz.raysmen.lp.core.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.Lend;
import xyz.raysmen.lp.core.service.LendService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/lend")
public class LendController {

    private final LendService lendService;

    @Autowired
    public LendController(LendService lendService) {
        this.lendService = lendService;
    }

    /**
     * 获取标的列表
     */
    @GetMapping("/list")
    public CustomResult list() {
        List<Lend> lendList = lendService.selectList();
        return CustomResult.ok().data("lendList", lendList);
    }

    /**
     * 获取标的信息
     *
     * @param id 标的id
     */
    @GetMapping("/show/{id}")
    public CustomResult show(@PathVariable Long id) {
        Map<String, Object> lendDetail = lendService.getLendDetail(id);
        return CustomResult.ok().data("lendDetail", lendDetail);
    }

    /**
     * 计算投资收益
     *
     * @param invest        投资金额
     * @param yearRate      年化收益
     * @param totalMonth    期数
     * @param returnMethod  还款方式
     */
    @GetMapping("/getInterestCount/{invest}/{yearRate}/{totalMonth}/{returnMethod}")
    public CustomResult getInterestCount(@PathVariable("invest") BigDecimal invest,
                                         @PathVariable("yearRate")BigDecimal yearRate,
                                         @PathVariable("totalMonth")Integer totalMonth,
                                         @PathVariable("returnMethod")Integer returnMethod) {
        BigDecimal interestCount = lendService.getInterestCount(invest, yearRate, totalMonth, returnMethod);
        return CustomResult.ok().data("interestCount", interestCount);
    }

}
