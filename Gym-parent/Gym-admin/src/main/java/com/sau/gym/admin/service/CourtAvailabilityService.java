package com.sau.gym.admin.service;

import com.sau.gym.model.vo.court.AvailableTimeSlotVO;

import java.util.List;

public interface CourtAvailabilityService {

    /**
     * 查询某个场地某一天的可预约时段。
     *
     * @param venueId 场馆ID
     * @param courtId 场地ID
     * @param date 预约日期，格式 yyyy-MM-dd
     * @return 时段列表
     */
    List<AvailableTimeSlotVO> getAvailableSlots(Long venueId, Long courtId, String date);
}
