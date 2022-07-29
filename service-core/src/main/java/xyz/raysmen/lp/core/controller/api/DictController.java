package xyz.raysmen.lp.core.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raysmen.lp.common.exception.CustomAssert;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.pojo.entity.Dict;
import xyz.raysmen.lp.core.service.DictService;

import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/api/core/dict")
public class DictController {
    private final DictService dictService;

    @Autowired
    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    /**
     * 根据dictCode获取下级节点
     * @param dictCode  节点编码
     */
    @GetMapping("/findByDictCode/{dictCode}")
    public CustomResult findByDictCode(@PathVariable String dictCode) {
        Long parentId = dictService.findByDictCode(dictCode);
        CustomAssert.notNull(parentId, ResponseEnum.ERROR);
        // 分开两个方法有助于获取listByParentId的缓存结果
        List<Dict> list = dictService.listByParentId(parentId);
        
        return CustomResult.ok().data("dictList", list);
    }
}
