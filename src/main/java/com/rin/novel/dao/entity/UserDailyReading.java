package com.rin.novel.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户每日阅读时长
 *
 * @author zhim00
 */
@Data
@TableName("user_daily_reading")
public class UserDailyReading implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 阅读日期
     */
    private LocalDate readingDate;

    /**
     * 累计阅读时长(秒)
     */
    private Integer durationSeconds;

    /**
     * 是否已打卡 0-否 1-是
     */
    private Integer isCheckedIn;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

