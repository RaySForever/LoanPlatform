package xyz.raysmen.lp.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.raysmen.lp.core.listener.ExcelDictDTOListener;
import xyz.raysmen.lp.core.mapper.DictMapper;
import xyz.raysmen.lp.core.pojo.dto.ExcelDictDTO;
import xyz.raysmen.lp.core.pojo.entity.Dict;
import xyz.raysmen.lp.core.service.DictService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Slf4j
@Service("dictService")
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    /**
     * 通过输入流导入数据字典，同时删除之前的缓存
     *
     * @param inputStream 输入流
     */
    @CacheEvict(value = "lp:core:dictList", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {
        // 需要指定用哪个class去读，在读取第一个sheet后文件流会自动关闭
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(this.baseMapper))
                .excelType(ExcelTypeEnum.XLSX).sheet().doRead();
        log.info("已完成从Excel导入数据字典！");
    }

    /**
     * 获取数据字典全部信息并转换为对应的数据传输专用对象
     *
     * @return ExcelDictDTO列表
     */
    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = this.baseMapper.selectList(null);
        //创建ExcelDictDTO列表，将Dict列表转换成ExcelDictDTO列表
        List<ExcelDictDTO> dtoList = new ArrayList<>(dictList.size());
        dictList.forEach(dict -> {
            ExcelDictDTO dto = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, dto);
            dtoList.add(dto);
        });
        return dtoList;
    }

    /**
     * 根据上级id获取数据字典列表
     * 缓存键为lp:core:dictList::parentId，其中parentId为动态参数
     *
     * @param parentId 上级id
     * @return 返回数据字典列表
     */
    @Cacheable(value = "lp:core:dictList", key = "#parentId")
    @Override
    public List<Dict> listByParentId(Long parentId) {
        LambdaQueryWrapper<Dict> dictQueryWrapper = new LambdaQueryWrapper<Dict>().eq(Dict::getParentId, parentId);
        // 查不出数据时为空列表
        List<Dict> dictList = this.baseMapper.selectList(dictQueryWrapper);
        dictList.forEach(dict -> {
            boolean hasChildren = this.hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });
        return dictList;
    }

    /**
     * 根据dictCode编码获取下级节点父节点ID
     *
     * @param dictCode 编码
     * @return 返回下级节点父节点ID
     */
    @Override
    public Long findByDictCode(String dictCode) {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<Dict>()
                .eq(Dict::getDictCode, dictCode);
        Dict dict = this.baseMapper.selectOne(wrapper);
        return dict == null ? null : dict.getId();
    }

    /**
     * 依据父节点编码和字典值获取具体字典的名称
     *
     * @param dictCode 父节点编码
     * @param value    字典值
     * @return 返回具体字典的名称
     */
    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {
        // 根据dictCode编码获取下级节点父节点ID，不存在则返回空字符串
        Long parentId = findByDictCode(dictCode);
        if(parentId == null) {
            return "";
        }

        // 根据父节点ID和字典值查询对应字典获取其名称
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<Dict>()
                .eq(Dict::getParentId, parentId)
                .eq(Dict::getValue, value);
        Dict dict = this.baseMapper.selectOne(wrapper);
        return dict == null ? "" : dict.getName();
    }

    private boolean hasChildren(Long parentId) {
        LambdaQueryWrapper<Dict> dictQueryWrapper = new LambdaQueryWrapper<Dict>().eq(Dict::getParentId, parentId);
        Long count = this.baseMapper.selectCount(dictQueryWrapper);
        return count > 0;
    }
}
