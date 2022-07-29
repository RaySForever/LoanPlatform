package xyz.raysmen.lp.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.raysmen.lp.core.mapper.DictMapper;
import xyz.raysmen.lp.core.pojo.dto.ExcelDictDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * ExcelDictDTOListener
 * 数据字典与Excel转换时监听器
 *
 * @author Rays
 * @project LoanPlatform
 * @package xyz.raysmen.lp.core.listener
 * @date 2022/05/22 12:52
 */
@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {
    /**
     * 每隔100条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    private List<ExcelDictDTO> cachedDataList = new ArrayList<>(BATCH_COUNT);

    private DictMapper dictMapper;

    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    /**
     * 遍历每一行的记录
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context analysis context
     */
    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext context) {
        log.info("解析到一条记录: {}", data);
        cachedDataList.add(data);
        // 每达到BATCH_COUNT了，就需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成新建 cachedDataList，利用gc清理，可能会造成OOM
            cachedDataList = new ArrayList<>(BATCH_COUNT);
        }
    }

    /**
     * if have something to do after all analysis
     *
     * @param context 分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 存储于数据库
     */
    private void saveData() {
        log.info("目前共解析{}条数据，开始存储于数据库！", cachedDataList.size());
        int insertBatch = dictMapper.insertBatch(cachedDataList);
        log.info("共{}条数据存储成功！", insertBatch);
    }
}
