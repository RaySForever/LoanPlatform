package xyz.raysmen.lp.core.mapper;

import org.springframework.stereotype.Repository;
import xyz.raysmen.lp.core.pojo.entity.LendItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 标的出借记录表 Mapper 接口
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Repository
public interface LendItemMapper extends BaseMapper<LendItem> {

}
