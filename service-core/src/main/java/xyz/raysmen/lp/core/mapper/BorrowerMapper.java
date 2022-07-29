package xyz.raysmen.lp.core.mapper;

import org.springframework.stereotype.Repository;
import xyz.raysmen.lp.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 借款人 Mapper 接口
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Repository
public interface BorrowerMapper extends BaseMapper<Borrower> {

}
