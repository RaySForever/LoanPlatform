package xyz.raysmen.lp.core.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.LendItemReturn;
import xyz.raysmen.lp.core.service.LendItemReturnService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/lendItemReturn")
public class LendItemReturnController {
    private final LendItemReturnService lendItemReturnService;

    @Autowired
    public LendItemReturnController(LendItemReturnService lendItemReturnService) {
        this.lendItemReturnService = lendItemReturnService;
    }

    /**
     * 获取列表
     *
     * @param lendId 标的id
     */
    @GetMapping("/auth/list/{lendId}")
    public CustomResult list(@PathVariable Long lendId,
                             HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        List<LendItemReturn> list = lendItemReturnService.selectByLendId(lendId, userId);
        return CustomResult.ok().data("list", list);
    }
}
