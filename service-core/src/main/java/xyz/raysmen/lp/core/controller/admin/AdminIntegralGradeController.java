package xyz.raysmen.lp.core.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.pojo.entity.IntegralGrade;
import xyz.raysmen.lp.core.service.IntegralGradeService;

import java.util.List;

/**
 * AdminIntegralGradeController
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.controller.admin
 * @date 2022/05/18 12:11
 * @description 积分等级管理
 */
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {
    private final IntegralGradeService integralGradeService;

    @Autowired
    public AdminIntegralGradeController(IntegralGradeService integralGradeService) {
        this.integralGradeService = integralGradeService;
    }

    @GetMapping("/list")
    public CustomResult listAll() {

        List<IntegralGrade> list = integralGradeService.list();

        return CustomResult.ok().data("list", list);
    }

    @DeleteMapping("/remove/{id}")
    public CustomResult removeById(@PathVariable Long id) {

        boolean result = integralGradeService.removeById(id);

        return result ? CustomResult.ok().message("删除成功") :
                CustomResult.error().message("删除失败");
    }

    @PostMapping("/save")
    public CustomResult save(@RequestBody IntegralGrade integralGrade) {

        CustomAssert.notNull(integralGrade.getBorrowAmount(), ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        boolean result = integralGradeService.save(integralGrade);

        return result ? CustomResult.ok().message("保存成功") :
                CustomResult.error().message("保存失败");
    }

    @GetMapping("/get/{id}")
    public CustomResult getById(@PathVariable Long id) {

        IntegralGrade integralGrade = integralGradeService.getById(id);

        return integralGrade != null ?
                CustomResult.ok().data("record", integralGrade) :
                CustomResult.error().message("数据不存在");
    }

    @PutMapping("/update")
    public CustomResult updateById(@RequestBody IntegralGrade integralGrade) {

        boolean result = integralGradeService.updateById(integralGrade);

        return result ? CustomResult.ok().message("修改成功") :
                CustomResult.error().message("修改失败");
    }
}
