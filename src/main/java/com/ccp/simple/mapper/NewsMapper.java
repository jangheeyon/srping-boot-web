package com.ccp.simple.mapper;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.domain.News;
import com.ccp.simple.domain.UserNewsLike;
import com.ccp.simple.dto.NewsResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {
    List<NewsResponseDto> getAllNews();

    void insertNews(News news);

    void insertNewsKeywordMapping(@Param("newsId") Long newsId, @Param("keywordId") Long keywordId);

    void updateNewsVisibility(@Param("newsId") Long newsId, @Param("visible") boolean visible);

    void updateLikeCount(@Param("newsId") Long newsId, @Param("likeCount") int likeCount);

    boolean existsByLink(String link);

    Long getNewsIdByLink(@Param("link") String link);

    void insertKeyword(Keyword keyword);

    List<Keyword> getAllKeywords();

    void deleteKeyword(int keywordId);

    // 좋아요 기능
    UserNewsLike findLike(@Param("userId") String userId, @Param("newsId") Long newsId);

    void insertLike(@Param("userId") String userId, @Param("newsId") Long newsId);

    void deleteLike(@Param("userId") String userId, @Param("newsId") Long newsId);
}
