package xyz.raysmen.lp.core.controller.admin;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.raysmen.lp.common.exception.BusinessException;
import xyz.raysmen.lp.common.result.CustomResult;
import xyz.raysmen.lp.common.result.ResponseEnum;
import xyz.raysmen.lp.core.pojo.dto.ExcelDictDTO;
import xyz.raysmen.lp.core.pojo.entity.Dict;
import xyz.raysmen.lp.core.service.DictService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 数据字典管理 前端控制器
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@RestController
@RequestMapping("/admin/core/dict")
public class AdminDictController {
    private final DictService dictService;

    @Autowired
    public AdminDictController(DictService dictService) {
        this.dictService = dictService;
    }

    /**
     * Excel批量导入数据字典
     */
    @PostMapping("/import")
    public CustomResult batchImport(@RequestParam MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            dictService.importData(inputStream);
            return CustomResult.ok().message("数据字典数据批量导入成功！");
        } catch (IOException e) {
            // 将IOException包含进业务异常并抛出给统一异常处理，文件上传错误
            throw new BusinessException(e, ResponseEnum.UPLOAD_ERROR);
        }
    }

    /**
     * Excel数据的导出
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        String fileName = "myDictionary";
        try {
            // 直接用浏览器或者用postman
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            // 未处理其他特殊浏览器的情况
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            List<ExcelDictDTO> dtoList = dictService.listDictData();
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dtoList);
        } catch (IOException e) {
            // 1.可能出现非IOException情况；
            // 2.HttpServletResponse可能需要重置，并更改ContentType为"application/json"；
            // 将IOException包含进业务异常并抛出给统一异常处理，数据导出失败
            throw new BusinessException(e, ResponseEnum.EXPORT_DATA_ERROR);
        }
    }

    /**
     * 根据上级id获取子节点数据列表，该方法属于延迟加载
     * <p>
     * 树形数据的两种加载方案：
     *   方案一：非延迟加载
     *   需要后端返回的数据结构中包含嵌套数据，并且嵌套数据放在children属性中
     *
     *
     *   方案二：延迟加载
     *   不需要后端返回数据中包含嵌套数据，并且要定义布尔属性hasChildren，表示当前节点是否包含子数据
     *   如果hasChildren为true，就表示当前节点包含子数据
     *   如果hasChildren为false，就表示当前节点不包含子数据
     *   如果当前节点包含子数据，那么点击当前节点的时候，就需要通过load方法加载子数据
     * </p>
     */
    @GetMapping("/listByParentId/{parentId}")
    public CustomResult listByParentId(@PathVariable Long parentId) {
        List<Dict> dictList = dictService.listByParentId(parentId);
        return  CustomResult.ok().data("list", dictList);
    }
}
