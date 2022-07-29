package xyz.raysmen.lp.core.service;

import xyz.raysmen.lp.core.pojo.dto.ExcelDictDTO;
import xyz.raysmen.lp.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
public interface DictService extends IService<Dict> {
    /**
     * 通过输入流导入数据字典
     *
     * @param inputStream 输入流
     */
    void importData(InputStream inputStream);

    /**
     * 获取数据字典全部信息并转换为对应的数据传输专用对象
     *
     * @return ExcelDictDTO列表
     */
    List<ExcelDictDTO> listDictData();

    /**
     * 根据上级id获取数据字典列表
     * 缓存键为lp:core:dictList::parentId，其中parentId为动态参数
     *
     * @param parentId 上级id
     * @return         返回数据字典列表
     */
    List<Dict> listByParentId(Long parentId);

    /**
     * 根据dictCode编码获取下级节点父节点ID
     *
     * @param dictCode  编码
     * @return          返回下级节点父节点ID
     */
    Long findByDictCode(String dictCode);

    /**
     * 依据父节点编码和字典值获取具体字典的名称
     *
     * @param dictCode  父节点编码
     * @param value     字典值
     * @return          返回具体字典的名称
     */
    String getNameByParentDictCodeAndValue(String dictCode, Integer value);
}
