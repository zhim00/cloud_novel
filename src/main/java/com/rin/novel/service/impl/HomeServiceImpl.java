package com.rin.novel.service.impl;

import com.rin.novel.core.common.resp.RestResp;
import com.rin.novel.dto.resp.HomeBookRespDto;
import com.rin.novel.dto.resp.HomeFriendLinkRespDto;
import com.rin.novel.manager.cache.FriendLinkCacheManager;
import com.rin.novel.manager.cache.HomeBookCacheManager;
import com.rin.novel.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页模块 服务实现类
 *
 * @author xiongxiaoyang
 * @date 2022/5/13
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final HomeBookCacheManager homeBookCacheManager;

    private final FriendLinkCacheManager friendLinkCacheManager;

    @Override
    public RestResp<List<HomeBookRespDto>> listHomeBooks() {
        return RestResp.ok(homeBookCacheManager.listHomeBooks());
    }

    @Override
    public RestResp<List<HomeFriendLinkRespDto>> listHomeFriendLinks() {
        return RestResp.ok(friendLinkCacheManager.listFriendLinks());
    }
}
