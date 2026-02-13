package com.rin.novel.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 上报阅读时长请求DTO
 *
 * @author zhim00
 */
@Data
public class ReportReadingReqDto {

    /**
     * 阅读时长(秒)，单次上报不超过600秒(10分钟)
     */
    @NotNull(message = "阅读时长不能为空")
    @Min(value = 1, message = "阅读时长必须大于0")
    @Max(value = 600, message = "单次上报阅读时长不能超过10分钟")
    private Integer durationSeconds;
}

