package com.rin.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rin.novel.core.common.resp.RestResp;
import com.rin.novel.core.constant.DatabaseConsts;
import com.rin.novel.dao.entity.NewsContent;
import com.rin.novel.dao.entity.NewsInfo;
import com.rin.novel.dao.mapper.NewsContentMapper;
import com.rin.novel.dao.mapper.NewsInfoMapper;
import com.rin.novel.dto.resp.NewsInfoRespDto;
import com.rin.novel.manager.cache.NewsCacheManager;
import com.rin.novel.service.NewsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 新闻模块 服务实现类
 *
 * @author xiongxiaoyang
 * @date 2022/5/14
 */
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsCacheManager newsCacheManager;

    private final NewsInfoMapper newsInfoMapper;

    private final NewsContentMapper newsContentMapper;

    @Override
    public RestResp<List<NewsInfoRespDto>> listLatestNews() {
        return RestResp.ok(newsCacheManager.listLatestNews());
    }

    @Override
    public RestResp<NewsInfoRespDto> getNews(Long id) {
        NewsInfo newsInfo = newsInfoMapper.selectById(id);
        QueryWrapper<NewsContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.NewsContentTable.COLUMN_NEWS_ID, id)
            .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        NewsContent newsContent = newsContentMapper.selectOne(queryWrapper);
        return RestResp.ok(NewsInfoRespDto.builder()
            .title(newsInfo.getTitle())
            .sourceName(newsInfo.getSourceName())
            .updateTime(newsInfo.getUpdateTime())
            .content(newsContent.getContent())
            .build());
    }
}
