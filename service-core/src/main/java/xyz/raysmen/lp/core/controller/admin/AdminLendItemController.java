package xyz.raysmen.lp.core.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.LendItem;
import xyz.raysmen.lp.core.service.LendItemService;

import java.util.List;

/**
 * <p>
 * 标的的投资管理 前端控制器
 * </p>
 *
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/lendItem")
public class AdminLendItemController {

    private final LendItemService lendItemService;

    @Autowired
    public AdminLendItemController(LendItemService lendItemService, ObjectMapper objectMapper) {
        this.lendItemService = lendItemService;
    }

    /**
     * 获取列表
     *
     * @param lendId 标的id
     */
    @GetMapping("/list/{lendId}")
    public CustomResult list(@PathVariable Long lendId) {
        List<LendItem> list = lendItemService.selectByLendId(lendId);
        return CustomResult.ok().data("list", list);
    }
}
