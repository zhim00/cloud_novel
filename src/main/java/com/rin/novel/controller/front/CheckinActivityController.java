package com.rin.novel.controller.front;

import com.rin.novel.core.auth.UserHolder;
import com.rin.novel.core.common.resp.RestResp;
import com.rin.novel.core.constant.ApiRouterConsts;
import com.rin.novel.core.constant.SystemConfigConsts;
import com.rin.novel.dto.req.ReportReadingReqDto;
import com.rin.novel.dto.resp.CheckinStatusRespDto;
import com.rin.novel.dto.resp.UserCouponRespDto;
import com.rin.novel.service.CheckinActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台门户-打卡活动模块 API 控制器
 *
 * @author zhim00
 */
@Tag(name = "CheckinActivityController", description = "前台门户-21天阅读打卡活动模块")
@SecurityRequirement(name = SystemConfigConsts.HTTP_AUTH_HEADER_NAME)
@RestController
@RequestMapping(ApiRouterConsts.API_FRONT_CHECKIN_URL_PREFIX)
@RequiredArgsConstructor
public class CheckinActivityController {

    private final CheckinActivityService checkinActivityService;

    /**
     * 上报阅读时长接口
     */
    @Operation(summary = "上报阅读时长接口")
    @PostMapping("/reading")
    public RestResp<Void> reportReading(@Valid @RequestBody ReportReadingReqDto dto) {
        return checkinActivityService.reportReading(UserHolder.getUserId(), dto.getDurationSeconds());
    }

    /**
     * 查询打卡活动状态接口
     */
    @Operation(summary = "查询打卡活动状态接口")
    @GetMapping("/status")
    public RestResp<CheckinStatusRespDto> getCheckinStatus() {
        return checkinActivityService.getCheckinStatus(UserHolder.getUserId());
    }

    /**
     * 抢券接口
     */
    @Operation(summary = "抢券接口")
    @PostMapping("/grab")
    public RestResp<Void> grabCoupon() {
        return checkinActivityService.grabCoupon(UserHolder.getUserId());
    }

    /**
     * 查询我的优惠券列表接口
     */
    @Operation(summary = "查询我的优惠券列表接口")
    @GetMapping("/coupons")
    public RestResp<List<UserCouponRespDto>> listMyCoupons() {
        return checkinActivityService.listUserCoupons(UserHolder.getUserId());
    }
}

