package xyz.raysmen.lp.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import xyz.raysmen.lp.core.pojo.entity.UserAccount;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Repository
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    /**
     * 更新账户信息，由绑定编号确定需要更新的账户
     *
     * @param bindCode      绑定编号
     * @param amount        借款数额
     * @param freezeAmount  冻结数额
     */
    void updateAccount(@Param("bindCode")String bindCode,
                       @Param("amount") BigDecimal amount,
                       @Param("freezeAmount")BigDecimal freezeAmount);
}
