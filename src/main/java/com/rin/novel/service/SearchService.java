package com.rin.novel.service;

import com.rin.novel.core.common.resp.PageRespDto;
import com.rin.novel.core.common.resp.RestResp;
import com.rin.novel.dto.req.BookSearchReqDto;
import com.rin.novel.dto.resp.BookInfoRespDto;

/**
 * 搜索 服务类
 *
 * @author xiongxiaoyang
 * @date 2022/5/23
 */
public interface SearchService {

    /**
     * 小说搜索
     *
     * @param condition 搜索条件
     * @return 搜索结果
     */
    RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition);

}
