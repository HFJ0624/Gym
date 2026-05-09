package com.sau.gym.admin.mapper;

import com.sau.gym.admin.agent.model.AgentCancelableBookingVO;
import com.sau.gym.model.dto.venue.CourtBookDto;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.vo.court.CourtBookEmailVO;
import com.sau.gym.model.vo.court.CourtBookVO;
import com.sau.gym.model.vo.system.TurnoverVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Mapper
public interface CourtBookingMapper {

    //根据条件查询所有数据
    List<CourtBookVO> findByPage(CourtBookDto courtBookDto);


    //删除预约场地
    void deleteById(Long id);

    //插入预约数据
    void saveCourtBook(CourtBooking courtBooking);

    //查询该用户所有的预约场地信息
    List<CourtBookVO> getCourtOrder(Long userId);

    //统计所有预约总数
    List<CourtBooking> countAllBook();

    //预约字段的校验规则
    List<CourtBookVO> selectBookTime(LocalDate bookingDate, Long courtId);

    CourtBooking selectById(Long orderId);

    //取消订单
    void cancelOrder(Long orderId);

    /**
     * 查询【待提醒】的订单：
     * 已支付(status=1) + 未提醒(is_reminded=0)提前一天提醒
     */
    List<CourtBookEmailVO> selectRemindOrders(String targetTime);

    /**
     * 标记订单为已提醒
     */
    void updateRemindedStatus(@Param("orderId") Long orderId);

    //获取前一星期的营业额数据
    List<TurnoverVo> getCourtBookTurnover();

    /**
     * Agent 查询当前用户可取消的预约列表。
     *
     * 这里不直接筛选“未来订单”，因为未来判断涉及日期和时间，
     * 第一版可以在 Service 层做更清晰的判断。
     */
    List<AgentCancelableBookingVO> selectAgentUserBookings(@Param("userId") Long userId);

    /**
     * Agent 查询当前用户某一条预约详情。
     * 必须带 userId，防止用户通过 bookingId 取消别人的预约。
     */
    AgentCancelableBookingVO selectAgentBookingDetail(@Param("userId") Long userId, @Param("bookingId") Long bookingId);

    /**
     * Agent 取消当前用户的预约订单。
     * 只允许用户取消自己的订单。
     */
    int updateAgentCancelBooking(@Param("userId") Long userId, @Param("bookingId") Long bookingId, @Param("cancelRemark") String cancelRemark);
}
