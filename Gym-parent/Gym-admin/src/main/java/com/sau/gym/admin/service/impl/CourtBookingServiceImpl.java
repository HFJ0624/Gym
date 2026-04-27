package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.CourtBookingMapper;
import com.sau.gym.admin.mapper.UserBalanceMapper;
import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.dto.venue.CourtBookDto;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.entity.user.UserBalance;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.vo.court.CourtBookVO;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Duration;
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

        //获取时间差
        Duration duration = Duration.between(bookingDto.getStartTime(), bookingDto.getEndTime());
        //开始时间大于结束时间直接报错
        if (duration.isNegative()){
            return false;
        }

        //校验预约时间段是否和别人的冲突
        if (!verify(bookingDto)){
            return false;
        }

        //计算预约时间多久
        double hours = Math.round(duration.toMinutes() / 60.0 * 100) / 100.0;
        if (hours < 1){
            //预约时长不能小于1小时
            throw new SauException(ResultCodeEnum.TIME_LESS);
        }

        //计算订单价格
        BigDecimal totalPrice = bookingDto.getHoursPrice().multiply(new BigDecimal(String.valueOf(hours)));

        CourtBooking courtBooking = new CourtBooking();

        //1.先查询该用户的余额是否充足
        UserBalance userBalance = userBalanceMapper.GetBalanceById(bookingDto.getUserId());
        if (userBalance.getBalance().compareTo(totalPrice) >= 0){
            //生成订单编码
            courtBooking.setOrderNo(getSecure32RandomNumber());

            courtBooking.setUserId(bookingDto.getUserId());
            courtBooking.setCourtId(bookingDto.getCourtId());
            courtBooking.setBookingDate(bookingDto.getBookingDate());
            courtBooking.setTotalPrice(totalPrice);
            courtBooking.setStatus(1); //已支付
            courtBooking.setRemark(bookingDto.getRemark());
            courtBooking.setStartTime(bookingDto.getStartTime());
            courtBooking.setEndTime(bookingDto.getEndTime());

            BigDecimal surplus = userBalance.getBalance().subtract(totalPrice);
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
    public PageInfo<CourtBookVO> getCourtOrder(Long userId,Integer current,Integer limit) {

        PageHelper.startPage(current,limit);

        //查询该用户所有的预约场地信息
        List<CourtBookVO> orders = courtBookingMapper.getCourtOrder(userId);

        PageInfo<CourtBookVO> pageInfo = new PageInfo<>(orders);

        return pageInfo;
    }

    //统计所有预约总数
    @Override
    public List<CourtBooking> countAllBook() {
        List<CourtBooking> list = courtBookingMapper.countAllBook();
        return list;
    }

    //前台用户取消预约订单
    @Transactional
    @Override
    public void cancelOrder(Long orderId) {
        //获取订单信息
        CourtBooking courtBooking = courtBookingMapper.selectById(orderId);

        //取消订单
        courtBookingMapper.cancelOrder(orderId);

        User user = AuthContextUtil.get();
        UserBalance userBalance = userBalanceMapper.GetBalanceById(user.getId());

        //把订单余额返回给用户的余额
        userBalanceMapper.updateBalance(user.getId(),courtBooking.getTotalPrice().add(userBalance.getBalance()));
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

    /***
     *
     * @param bookingDto 前端预约实体类
     * @return 返回预约时间段是否冲突,冲突返回false
     */
    public Boolean verify(BookingDto bookingDto){
        List<CourtBookVO> list = courtBookingMapper.selectBookTime(bookingDto.getBookingDate(),bookingDto.getCourtId());

        // 遍历所有已预约时间段，判断是否重叠
        for (CourtBookVO courtBookVO : list) {
            // 【核心】判断时间段是否重叠
            boolean isOverlap = bookingDto.getStartTime().isBefore(courtBookVO.getEndTime())
                    && bookingDto.getEndTime().isAfter(courtBookVO.getStartTime());

            // 重叠=时间冲突，直接返回false
            if (isOverlap) {
                return false;
            }
        }

        //校验通过
        return true;
    }
}
