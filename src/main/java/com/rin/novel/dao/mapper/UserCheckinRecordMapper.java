package com.rin.novel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rin.novel.dao.entity.UserCheckinRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 用户打卡记录 Mapper 接口
 *
 * @author zhim00
 */
public interface UserCheckinRecordMapper extends BaseMapper<UserCheckinRecord> {

    /**
     * 统计用户当月打卡天数
     *
     * @param userId       用户ID
     * @param checkinMonth 打卡月份 yyyy-MM
     * @return 打卡天数
     */
    Integer countByUserIdAndMonth(@Param("userId") Long userId, @Param("checkinMonth") String checkinMonth);
}

