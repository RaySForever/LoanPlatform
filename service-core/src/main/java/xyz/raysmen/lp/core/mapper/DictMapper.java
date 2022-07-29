package xyz.raysmen.lp.core.mapper;

import org.springframework.stereotype.Repository;
import xyz.raysmen.lp.core.pojo.dto.ExcelDictDTO;
import xyz.raysmen.lp.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Repository
public interface DictMapper extends BaseMapper<Dict> {
    /**
     * 将读取到的Excel数据批量存储于数据库
     *
     * @param list 数据字典转换数据列表
     * @return 返回影响行数
     */
    int insertBatch(List<ExcelDictDTO> list);
}
