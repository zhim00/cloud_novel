package com.rin.novel.manager.cache;

import com.rin.novel.core.constant.CacheConsts;
import com.rin.novel.dao.entity.UserInfo;
import com.rin.novel.dao.mapper.UserInfoMapper;
import com.rin.novel.dto.UserInfoDto;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 用户信息 缓存管理类
 *
 * @author xiongxiaoyang
 * @date 2022/5/12
 */
@Component
@RequiredArgsConstructor
public class UserInfoCacheManager {

    private final UserInfoMapper userInfoMapper;

    /**
     * 查询用户信息，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.USER_INFO_CACHE_NAME)
    public UserInfoDto getUser(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (Objects.isNull(userInfo)) {
            return null;
        }
        return UserInfoDto.builder()
            .id(userInfo.getId())
            .status(userInfo.getStatus()).build();
    }


}
