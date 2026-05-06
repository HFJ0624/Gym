package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.CourtBookingMapper;
import com.sau.gym.admin.mapper.CourtMapper;
import com.sau.gym.admin.mapper.VenueMapper;
import com.sau.gym.admin.service.CourtAvailabilityService;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.court.AvailableTimeSlotVO;
import com.sau.gym.model.vo.court.CourtBookVO;
import com.sau.gym.model.vo.court.CourtVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:场地可预约时段服务实现
 * 核心逻辑：
 * 1. 根据 venueId 找到场馆开放时间
 * 2. 根据 courtId 查询当天已预约时间段
 * 3. 按 1 小时切分场馆开放时间
 * 4. 判断每个时段是否与已有预约冲突
 * 5. 返回给前端展示
 * 日期: 2026/5/6 9:42
 */
@Service
public class CourtAvailabilityServiceImpl implements CourtAvailabilityService {

    private final VenueMapper venueMapper;
    private final CourtMapper courtMapper;
    private final CourtBookingMapper courtBookingMapper;

    public CourtAvailabilityServiceImpl(VenueMapper venueMapper,
                                        CourtMapper courtMapper,
                                        CourtBookingMapper courtBookingMapper) {
        this.venueMapper = venueMapper;
        this.courtMapper = courtMapper;
        this.courtBookingMapper = courtBookingMapper;
    }

    /**
     * 查询某个场地某一天的可预约时段。
     */
    @Override
    public List<AvailableTimeSlotVO> getAvailableSlots(Long venueId, Long courtId, String date) {
        if (venueId == null) {
            throw new RuntimeException("场馆ID不能为空");
        }

        if (courtId == null) {
            throw new RuntimeException("场地ID不能为空");
        }

        if (date == null || date.trim().isEmpty()) {
            throw new RuntimeException("预约日期不能为空");
        }

        LocalDate bookingDate;
        try {
            bookingDate = LocalDate.parse(date);
        } catch (Exception e) {
            throw new RuntimeException("预约日期格式错误，请使用 yyyy-MM-dd");
        }

        //1.查询场馆
        Venue venue = findVenueById(venueId);

        if (venue == null) {
            throw new RuntimeException("场馆不存在");
        }

        //2. 校验场地是否属于该场馆
        CourtVO court = findCourtById(venueId, courtId);

        if (court == null) {
            throw new RuntimeException("当前场馆下不存在该场地");
        }

        //3. 获取场馆开放时间,如果场馆没有配置开放时间，给一个兜底值。
        LocalTime openTime = venue.getOpenTime() == null
                ? LocalTime.of(8, 0)
                : venue.getOpenTime();

        LocalTime closeTime = venue.getCloseTime() == null
                ? LocalTime.of(22, 0)
                : venue.getCloseTime();

        if (!openTime.isBefore(closeTime)) {
            throw new RuntimeException("场馆开放时间配置错误");
        }

        //4. 查询当天该场地已预约时间段。
        List<CourtBookVO> bookedList = courtBookingMapper.selectBookTime(bookingDate, courtId);

        //5. 按 1 小时生成时段。
        return buildSlotList(bookingDate, openTime, closeTime, bookedList);
    }

    /**
     * 根据场馆ID查询场馆。
     */
    private Venue findVenueById(Long venueId) {
        List<Venue> venueList = venueMapper.findAllVenue();

        if (venueList == null || venueList.isEmpty()) {
            return null;
        }

        return venueList.stream()
                .filter(v -> v != null && venueId.equals(v.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据场馆ID和场地ID查询场地。
     */
    private CourtVO findCourtById(Long venueId, Long courtId) {
        List<CourtVO> courtList = courtMapper.getAllCourt(venueId);

        if (courtList == null || courtList.isEmpty()) {
            return null;
        }

        return courtList.stream()
                .filter(c -> c != null && courtId.equals(c.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 生成时段列表。
     */
    private List<AvailableTimeSlotVO> buildSlotList(LocalDate bookingDate,
                                                    LocalTime openTime,
                                                    LocalTime closeTime,
                                                    List<CourtBookVO> bookedList) {
        List<AvailableTimeSlotVO> result = new ArrayList<>();

        LocalTime current = openTime;

        //默认一个时段 1 小时
        while (current.plusHours(1).compareTo(closeTime) <= 0) {
            LocalTime slotStart = current;
            LocalTime slotEnd = current.plusHours(1);

            AvailableTimeSlotVO vo = new AvailableTimeSlotVO();

            vo.setDate(bookingDate.toString());
            vo.setStartTime(slotStart.toString());
            vo.setEndTime(slotEnd.toString());
            vo.setLabel(formatSlotLabel(slotStart, slotEnd));

            String reason = getUnavailableReason(bookingDate, slotStart, slotEnd, bookedList);

            vo.setAvailable(reason == null);
            vo.setReason(reason);

            result.add(vo);

            current = slotEnd;
        }

        return result;
    }

    /**
     * 获取不可预约原因。
     * 返回 null 表示可预约。
     */
    private String getUnavailableReason(LocalDate bookingDate,
                                        LocalTime slotStart,
                                        LocalTime slotEnd,
                                        List<CourtBookVO> bookedList) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        //过去日期不可预约。
        if (bookingDate.isBefore(today)) {
            return "已过期";
        }

        //今天已经过去的时段不可预约。
        if (bookingDate.isEqual(today) && !slotStart.isAfter(now)) {
            return "已过期";
        }

        //判断是否和已有预约冲突。
        if (bookedList != null && !bookedList.isEmpty()) {
            for (CourtBookVO book : bookedList) {
                if (book == null || book.getStartTime() == null || book.getEndTime() == null) {
                    continue;
                }

                boolean overlap = slotStart.isBefore(book.getEndTime())
                        && slotEnd.isAfter(book.getStartTime());

                if (overlap) {
                    return "已被预约";
                }
            }
        }

        return null;
    }

    //格式化时段展示文本。08:00:00 -> 08:00
    private String formatSlotLabel(LocalTime start, LocalTime end) {
        return start.toString().substring(0, 5)
                + "-"
                + end.toString().substring(0, 5);
    }
}
