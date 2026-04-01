package com.sau.gym.admin.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:EasyExcel 监听器
 * 日期: 2026/4/1 17:19
 */
public class ExcelListener<T> extends AnalysisEventListener<T> {

    //可以通过实例获取该值
    private List<T> datas = new ArrayList<>();

    @Override
    public void invoke(T o, AnalysisContext analysisContext) {  // 每解析一行数据就会调用一次该方法
        datas.add(o);//数据存储到list，供批量处理，或后续自己业务逻辑处理。
    }

    public List<T> getDatas() {
        return datas;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // excel解析完毕以后需要执行的代码
    }
}
