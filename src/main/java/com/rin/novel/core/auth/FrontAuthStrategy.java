package com.rin.novel.core.auth;

import com.rin.novel.core.common.exception.BusinessException;
import com.rin.novel.core.util.JwtUtils;
import com.rin.novel.manager.cache.UserInfoCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 前台门户系统 认证授权策略
 *
 * @author xiongxiaoyang
 * @date 2022/5/18
 */
@Component
@RequiredArgsConstructor
public class FrontAuthStrategy implements AuthStrategy {

    private final JwtUtils jwtUtils;

    private final UserInfoCacheManager userInfoCacheManager;

    @Override
    public void auth(String token, String requestUri) throws BusinessException {
        // 统一账号认证
        authSSO(jwtUtils, userInfoCacheManager, token);
    }

}