package com.sau.gym.admin.controller.front;

import com.sau.gym.admin.service.CourtAvailabilityService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.court.AvailableTimeSlotVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作者:hfj
 * 功能:前台场地可预约时段接口
 * 日期: 2026/5/6 9:48
 */
@RestController
@RequestMapping("/front/courtBook")
public class FrontCourtAvailabilityController {

    private final CourtAvailabilityService courtAvailabilityService;

    public FrontCourtAvailabilityController(CourtAvailabilityService courtAvailabilityService) {
        this.courtAvailabilityService = courtAvailabilityService;
    }

    //查询某个场地某天的可预约时段。
    @GetMapping("/availableSlots")
    public Result getAvailableSlots(@RequestParam Long venueId,
                                    @RequestParam Long courtId,
                                    @RequestParam String date) {
        List<AvailableTimeSlotVO> list = courtAvailabilityService.getAvailableSlots(
                venueId,
                courtId,
                date
        );

        return Result.build(list, ResultCodeEnum.SUCCESS);
    }
}
