package xyz.raysmen.lp.core.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.TransFlow;
import xyz.raysmen.lp.core.service.TransFlowService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 交易流水表 前端控制器
 * </p>
 * 资金记录
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/transFlow")
public class TransFlowController {
    private final TransFlowService transFlowService;

    @Autowired
    public TransFlowController(TransFlowService transFlowService) {
        this.transFlowService = transFlowService;
    }

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public CustomResult list(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        List<TransFlow> list = transFlowService.selectByUserId(userId);
        return CustomResult.ok().data("list", list);
    }
}
