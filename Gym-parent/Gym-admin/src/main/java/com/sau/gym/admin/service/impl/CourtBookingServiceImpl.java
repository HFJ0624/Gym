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
import com.sau.gym.model.entity.venue.BookingRefundRequest;
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

    @Autowired
    private BookingRefundRequestMapper bookingRefundRequestMapper;

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
            Date date = new Date();
            paymentRecord.setPayTime(date);
            paymentRecord.setCreateTime(date);
            paymentRecord.setUserId(bookingDto.getUserId());
            paymentRecord.setStatus(1);
            paymentRecord.setOrderNo(order_no);
            paymentRecord.setAmount(totalPrice);
            paymentRecordMapper.insertOne(paymentRecord);

            BigDecimal surplus = userBalance.getBalance().subtract(totalPrice);
            //保存到数据库
            courtBookingMapper.saveCourtBook(courtBooking);
            //更新用户余额
            userBalanceMapper.updateBalance(bookingDto.getUserId(),surplus,date);

            //监听事件:获取通知消息-用户下单预约消息
            eventPublisher.publishEvent(new NotificationEvent(
                    bookingDto.getUserId(),
                    "预约订单已创建",
                    "您的" + court.getName()  +"预约订单已创建，已完成支付。订单号：" + order_no,
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelOrder(Long orderId,String reason) {
        User user = AuthContextUtil.get();

        if (user == null || user.getId() == null) {
            throw new SauException(ResultCodeEnum.LOGIN_AUTH);
        }

        cancelOrderByUserId(
                user.getId(),
                orderId,
                reason,
                "FRONT"
        );
    }

    /**
     * 统一取消预约入口
     * 前台用户取消预约、Agent 确认取消预约，都走这个方法。
     * 为什么要统一：
     * 1. 避免前台取消和 Agent 取消各写一套逻辑
     * 2. 避免退款申请重复生成
     * 3. 避免有的入口直接退款，有的入口生成退款申请，导致业务不一致
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelOrderByUserId(Long userId, Long orderId, String reason, String source) {
        if (userId == null) {
            throw new SauException(ResultCodeEnum.LOGIN_AUTH);
        }

        if (orderId == null) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }

        //1. 查询预约订单
        CourtBooking courtBooking = courtBookingMapper.selectById(orderId);

        if (courtBooking == null || courtBooking.getIsDeleted() == 1) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }

        //2. 校验订单是否属于当前用户
        if (!userId.equals(courtBooking.getUserId())) {
            throw new SauException(ResultCodeEnum.ILLEGAL_REQUEST);
        }

        //3. 校验订单状态
        if (courtBooking.getStatus() == null
                || courtBooking.getStatus() == 2
                || courtBooking.getStatus() == 3) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }

        //4. 更新订单状态为已取消
        courtBookingMapper.cancelOrder(orderId);

        //5. 如果订单金额大于 0，则生成退款申请
        //这里不直接退余额。后台审核通过后，才执行真正退款。
        if (courtBooking.getTotalPrice() != null
                && courtBooking.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {

            int exists = bookingRefundRequestMapper.countByBookingId(orderId);

            if (exists <= 0) {
                BookingRefundRequest request = new BookingRefundRequest();

                request.setBookingId(courtBooking.getId());
                request.setOrderNo(courtBooking.getOrderNo());
                request.setUserId(userId);
                request.setRefundAmount(courtBooking.getTotalPrice());
                request.setReason(reason);
                request.setStatus(0);

                bookingRefundRequestMapper.insertRefundRequest(request);
            }
        }

        //6. 发布通知
        eventPublisher.publishEvent(new NotificationEvent(
                userId,
                "预约已取消",
                "您的预约订单已取消，退款申请已提交，等待后台审核。订单号：" + courtBooking.getOrderNo(),
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
