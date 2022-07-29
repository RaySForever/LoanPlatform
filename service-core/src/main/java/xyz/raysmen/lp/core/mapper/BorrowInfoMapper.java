package xyz.raysmen.lp.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import xyz.raysmen.lp.core.pojo.entity.BorrowInfo;

import java.util.List;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Repository
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {
    /**
     * 从数据库中获取借款信息列表
     *
     * @return  返回借款信息列表
     */
    List<BorrowInfo> selectBorrowInfoList();
}
