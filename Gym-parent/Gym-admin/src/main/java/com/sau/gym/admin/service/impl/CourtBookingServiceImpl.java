package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.CourtBookingMapper;
import com.sau.gym.admin.mapper.UserBalanceMapper;
import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.dto.venue.CourtBookDto;
import com.sau.gym.model.entity.user.UserBalance;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.vo.court.CourtBookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/16 18:15
 */
@Service
public class CourtBookingServiceImpl implements CourtBookingService {

    @Autowired
    private CourtBookingMapper courtBookingMapper;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    //场地预约的查询功能
    @Override
    public PageInfo<CourtBookVO> findByPage(Integer current, Integer limit, CourtBookDto courtBookDto) {

        //设置分页参数
        PageHelper.startPage(current,limit);

        //根据条件查询所有数据
        List<CourtBookVO> list = courtBookingMapper.findByPage(courtBookDto);

        //封装pageInfo对象
        PageInfo<CourtBookVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //删除预约场地
    @Override
    public void deleteById(Long id) {
        courtBookingMapper.deleteById(id);
    }

    //添加预约场地
    @Transactional
    @Override
    public boolean saveCourtBook(BookingDto bookingDto) {
        CourtBooking courtBooking = new CourtBooking();

        //1.先查询该用户的余额是否充足
        UserBalance userBalance = userBalanceMapper.GetBalanceById(bookingDto.getUserId());
        if (userBalance.getBalance().compareTo(bookingDto.getTotalPrice()) >= 0){
            //生成订单编码
            courtBooking.setOrderNo(getSecure32RandomNumber());

            courtBooking.setUserId(bookingDto.getUserId());
            courtBooking.setCourtId(bookingDto.getCourtId());
            courtBooking.setBookingDate(bookingDto.getBookingDate());
            courtBooking.setTotalPrice(bookingDto.getTotalPrice());
            courtBooking.setStatus(0);
            courtBooking.setRemark(bookingDto.getRemark());

            BigDecimal surplus = userBalance.getBalance().subtract(bookingDto.getTotalPrice());
            //保存到数据库
            courtBookingMapper.saveCourtBook(courtBooking);
            //更新用户余额
            userBalanceMapper.updateBalance(bookingDto.getUserId(),surplus);
            return true;
        }

        return false;
    }


    //查询所有预约记录
    @Override
    public Map<String, Object> getCourtOrder(Long userId) {
        //查询该用户所有的预约场地信息
        List<CourtBookVO> orders = courtBookingMapper.getCourtOrder(userId);

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("orders",orders);
        return resultMap;
    }

    //统计所有预约总数
    @Override
    public List<CourtBooking> countAllBook() {
        List<CourtBooking> list = courtBookingMapper.countAllBook();
        return list;
    }

    /***
     *
     * @return 生成32位订单编码
     */
    public static String getSecure32RandomNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(32);

        for (int i = 0; i < 32; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
