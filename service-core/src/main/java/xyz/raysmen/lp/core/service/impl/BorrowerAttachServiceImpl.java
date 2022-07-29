package xyz.raysmen.lp.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import xyz.raysmen.lp.core.pojo.entity.BorrowerAttach;
import xyz.raysmen.lp.core.mapper.BorrowerAttachMapper;
import xyz.raysmen.lp.core.pojo.util.BeansConverter;
import xyz.raysmen.lp.core.pojo.vo.BorrowerAttachVO;
import xyz.raysmen.lp.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author Rays
 * @since 2022-05-17
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    /**
     * 通过借款人ID获取获取附件VO列表
     *
     * @param borrowerId 借款人ID
     * @return 返回对应的附件VO列表
     */
    @Override
    public List<BorrowerAttachVO> selectBorrowerAttachVOList(Long borrowerId) {
        // 通过借款人ID获取获取附件列表
        LambdaQueryWrapper<BorrowerAttach> wrapper = new LambdaQueryWrapper<BorrowerAttach>()
                .eq(BorrowerAttach::getBorrowerId, borrowerId);
        List<BorrowerAttach> borrowerAttachList = this.baseMapper.selectList(wrapper);

        // 将附件列表转换为附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList = new ArrayList<>();
        borrowerAttachList.forEach(borrowerAttach -> {
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            BeansConverter.borrowerAttachTOBorrowerAttachVO(borrowerAttach, borrowerAttachVO);
            borrowerAttachVOList.add(borrowerAttachVO);
        });
        return borrowerAttachVOList;
    }
}
