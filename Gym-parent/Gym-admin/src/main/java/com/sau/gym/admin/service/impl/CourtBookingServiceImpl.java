package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.enums.NotificationBusinessTypeEnum;
import com.sau.gym.admin.enums.NotificationTypeEnum;
import com.sau.gym.admin.mapper.*;
import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.dto.venue.CourtBookDto;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.event.NotificationEvent;
import com.sau.gym.model.entity.finance.BalanceRecord;
import com.sau.gym.model.entity.finance.PaymentRecord;
import com.sau.gym.model.entity.finance.RefundRecord;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.entity.user.UserBalance;
import com.sau.gym.model.entity.venue.Court;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.vo.court.CourtBookVO;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
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
    private CourtMapper courtMapper;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private BalanceRecordMapper balanceRecordMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private RefundRecordMapper refundRecordMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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

    //下单预约场地
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
            String order_no = getSecure32RandomNumber();
            courtBooking.setOrderNo(order_no);

            courtBooking.setUserId(bookingDto.getUserId());
            courtBooking.setCourtId(bookingDto.getCourtId());
            courtBooking.setBookingDate(bookingDto.getBookingDate());
            courtBooking.setTotalPrice(totalPrice);
            courtBooking.setStatus(1); //已支付
            courtBooking.setRemark(bookingDto.getRemark());
            courtBooking.setStartTime(bookingDto.getStartTime());
            courtBooking.setEndTime(bookingDto.getEndTime());

            //插入流水信息
            BalanceRecord balanceRecord = new BalanceRecord();
            balanceRecord.setUserId(bookingDto.getUserId());
            balanceRecord.setType(2);
            balanceRecord.setAmount(totalPrice);
            balanceRecord.setBeforeBalance(userBalance.getBalance());
            balanceRecord.setAfterBalance(userBalance.getBalance().subtract(totalPrice));
            balanceRecord.setOrderNo(order_no);
            balanceRecord.setCreateTime(new Date());

            //获取场地信息
            Court court = courtMapper.selectOne(bookingDto.getCourtId());
            balanceRecord.setRemark("用户预约场地,订单金额:" + totalPrice + "元,场地名称:" + court.getName());
            balanceRecordMapper.insertOne(balanceRecord);

            //插入支付流水信息
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setOrderType(1);
            paymentRecord.setPayChannel(1);
            paymentRecord.setPayNo(get20SerialNo());
            paymentRecord.setPayTime(new Date());
            paymentRecord.setCreateTime(new Date());
            paymentRecord.setUserId(bookingDto.getUserId());
            paymentRecord.setStatus(1);
            paymentRecord.setOrderNo(order_no);
            paymentRecord.setAmount(totalPrice);
            paymentRecordMapper.insertOne(paymentRecord);

            BigDecimal surplus = userBalance.getBalance().subtract(totalPrice);
            //保存到数据库
            courtBookingMapper.saveCourtBook(courtBooking);
            //更新用户余额
            userBalanceMapper.updateBalance(bookingDto.getUserId(),surplus);

            //监听事件:获取通知消息-用户下单预约消息
            eventPublisher.publishEvent(new NotificationEvent(
                    bookingDto.getUserId(),
                    "预约订单已创建",
                    "您的预约订单已创建，已完成支付。订单号：" + order_no,
                    NotificationTypeEnum.BOOKING.getCode(),
                    courtBooking.getId(),
                    order_no,
                    NotificationBusinessTypeEnum.BOOKING_ORDER.getCode()
            ));

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

        //插入退款流水表
        RefundRecord refundRecord = new RefundRecord();
        refundRecord.setRefundAmount(courtBooking.getTotalPrice());
        refundRecord.setOrderType(1);
        refundRecord.setOrderNo(courtBooking.getOrderNo());
        refundRecord.setStatus(1);
        refundRecord.setUserId(courtBooking.getUserId());
        refundRecord.setCreateTime(new Date());
        refundRecord.setRefundTime(new Date());
        PaymentRecord paymentRecord =paymentRecordMapper.selectOne(courtBooking.getOrderNo());
        refundRecord.setPayNo(paymentRecord.getPayNo());
        refundRecord.setOrderNo(courtBooking.getOrderNo());
        refundRecordMapper.insertOne(refundRecord);

        //把订单余额返回给用户的余额
        userBalanceMapper.updateBalance(user.getId(),courtBooking.getTotalPrice().add(userBalance.getBalance()));

        //监听事件:获取通知消息-用户退款消息
        eventPublisher.publishEvent(new NotificationEvent(
                user.getId(),
                "预约已取消",
                "您的预约订单已取消。订单号：" + courtBooking.getOrderNo(),
                NotificationTypeEnum.BOOKING.getCode(),
                courtBooking.getId(),
                courtBooking.getOrderNo(),
                NotificationBusinessTypeEnum.BOOKING_ORDER.getCode()
        ));
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

    /***
     *
     * @return 生成20位流水号（时间+随机数，纯数字）
     */
    public static String get20SerialNo() {
        long time = System.currentTimeMillis();
        String random = String.format("%07d", (int) (Math.random() * 10000000));
        return String.valueOf(time) + random;
    }
}
