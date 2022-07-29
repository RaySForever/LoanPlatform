package xyz.raysmen.lp.core.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.LendReturn;
import xyz.raysmen.lp.core.service.LendReturnService;

import java.util.List;

/**
 * <p>
 * 还款记录表 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/lendReturn")
public class AdminLendReturnController {
    private final LendReturnService lendReturnService;

    @Autowired
    public AdminLendReturnController(LendReturnService lendReturnService) {
        this.lendReturnService = lendReturnService;
    }

    /**
     * 获取列表
     *
     * @param lendId 标的id
     */
    @GetMapping("/list/{lendId}")
    public CustomResult list(@PathVariable Long lendId) {
        List<LendReturn> list = lendReturnService.selectByLendId(lendId);
        return CustomResult.ok().data("list", list);
    }
}
