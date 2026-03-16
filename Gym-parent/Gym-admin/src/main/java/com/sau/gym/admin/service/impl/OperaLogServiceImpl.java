package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.OperaLogMapper;
import com.sau.gym.admin.service.OperaLogService;
import com.sau.gym.model.dto.system.OperaLogDto;
import com.sau.gym.model.entity.system.OperaLog;
import com.sau.gym.model.entity.venue.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/10 19:42
 */
@Service
public class OperaLogServiceImpl implements OperaLogService {

    @Autowired
    private OperaLogMapper operaLogMapper;


    //操作日志列表查询
    @Override
    public PageInfo<OperaLog> findByPage(Integer current, Integer limit, OperaLogDto operaLogDto) {
        //设置分页参数
        PageHelper.startPage(current,limit);

        //根据条件查询所有数据
        List<OperaLog> list = operaLogMapper.findByPage(operaLogDto);

        //封装pageInfo对象
        PageInfo<OperaLog> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //删除操作日志记录
    @Override
    public void deleteById(Long id) {
        operaLogMapper.deleteById(id);
    }
}
