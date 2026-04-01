package com.sau.gym.admin.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.sau.gym.admin.mapper.UserMapper;
import com.sau.gym.model.vo.user.UserExcelVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:EasyExcel 监听器
 * 日期: 2026/4/1 17:19
 */
public class ExcelListener<T> extends AnalysisEventListener<T> {

    /**
     每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    //获取mapper对象
    private UserMapper userMapper;

    public ExcelListener(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 每解析一行数据就会调用一次该方法
    @Override
    public void invoke(T o, AnalysisContext analysisContext) {
        UserExcelVO data = (UserExcelVO)o;
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // excel解析完毕以后需要执行的代码
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
    }

    //批量插入数据
    private void saveData() {
        userMapper.batchInsert(cachedDataList);
    }
}
