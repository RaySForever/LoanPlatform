package xyz.raysmen.lp.core.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.core.pojo.entity.UserLoginRecord;
import xyz.raysmen.lp.core.service.UserLoginRecordService;

import java.util.List;

/**
 * AdminUserLoginRecordController
 * 会员登录日志管理 前端控制器
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.controller.admin
 * @date 2022/06/09 21:51
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {
    private final UserLoginRecordService userLoginRecordService;

    @Autowired
    public AdminUserLoginRecordController(UserLoginRecordService userLoginRecordService) {
        this.userLoginRecordService = userLoginRecordService;
    }

    /**
     * 获取会员登录日志列表
     */
    @GetMapping("/listTop50/{userId}")
    public CustomResult listTop50(@PathVariable Long userId) {
        List<UserLoginRecord> userLoginRecordList = userLoginRecordService.listTop50(userId);
        return CustomResult.ok().data("list", userLoginRecordList);
    }
}
