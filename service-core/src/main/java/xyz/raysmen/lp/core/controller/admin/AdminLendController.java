package xyz.raysmen.lp.core.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.Lend;
import xyz.raysmen.lp.core.service.LendService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的管理 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/lend")
public class AdminLendController {
    private final LendService lendService;

    @Autowired
    public AdminLendController(LendService lendService) {
        this.lendService = lendService;
    }

    /**
     * 标的列表
     */
    @GetMapping("/list")
    public CustomResult list() {
        List<Lend> lendList = lendService.selectList();
        return CustomResult.ok().data("list", lendList);
    }

    /**
     * 通过标的ID获取标的信息
     */
    @GetMapping("/show/{id}")
    public CustomResult show(@PathVariable Long id) {
        Map<String, Object> lendDetails = lendService.getLendDetail(id);
        return CustomResult.ok().data("lendDetail", lendDetails);
    }

    /**
     * 放款
     *
     * @param id    标的id
     */
    @GetMapping("/makeLoan/{id}")
    public CustomResult makeLoan(@PathVariable Long id) {
        lendService.makeLoan(id);
        return CustomResult.ok().message("放款成功");
    }
}
