package xyz.raysmen.lp.core.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.base.util.JwtUtils;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.hfb.HfbConst;
import xyz.raysmen.lp.core.hfb.RequestHelper;
import xyz.raysmen.lp.core.pojo.entity.LendItem;
import xyz.raysmen.lp.core.pojo.vo.InvestVO;
import xyz.raysmen.lp.core.service.LendItemService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 前端控制器
 * </p>
 * 标的的投资
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/lendItem")
public class LendItemController {

    private final LendItemService lendItemService;

    private final ObjectMapper objectMapper;

    @Autowired
    public LendItemController(LendItemService lendItemService, ObjectMapper objectMapper) {
        this.lendItemService = lendItemService;
        this.objectMapper = objectMapper;
    }

    /**
     * 会员投资提交数据
     *
     * @param investVO  投标信息
     */
    @PostMapping("/auth/commitInvest")
    public CustomResult commitInvest(@RequestBody InvestVO investVO, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String userName = JwtUtils.getUserName(token);
        // 完善传入的投标信息
        investVO.setInvestUserId(userId);
        investVO.setInvestName(userName);

        // 构建充值自动提交表单
        String formStr = lendItemService.commitInvest(investVO);
        return CustomResult.ok().data("formStr", formStr);
    }

    /**
     * 会员投资异步回调
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {

        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        String str = null;
        try {
            str = objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            log.error("Jackson序列化异常：JsonProcessingException：{}", ExceptionUtils.getStackTrace(e));
        }
        log.info("用户投资异步回调：" + str);

        //校验签名 P2pInvestNotifyVo
        if(RequestHelper.isSignEquals(paramMap)) {
            if(HfbConst.INVEST_SUCCESS_CODE.equals(paramMap.get(HfbConst.RESULT_CODE))) {
                lendItemService.notify(paramMap);
            } else {
                log.info("用户投资异步回调失败：" + str);
                return "fail";
            }
        } else {
            log.info("用户投资异步回调签名错误：" + str);
            return "fail";
        }
        return "success";
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
