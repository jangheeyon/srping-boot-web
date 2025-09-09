package com.ccp.simple.mapper;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.domain.News;
import com.ccp.simple.dto.KeywordCountDto;
import com.ccp.simple.dto.NewsResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {
    List<NewsResponseDto> getAllNews();

    List<NewsResponseDto> getAllSubcribedNews(@Param("userId") String userId);

    List<NewsResponseDto> findNewsByIds(List<Long> ids);

    void insertNews(News news);

    void insertNewsKeywordMapping(@Param("newsId") Long newsId, @Param("keywordId") Long keywordId);

    void updateNewsVisibility(@Param("newsId") Long newsId, @Param("visible") boolean visible);

    void updateLikeCount(@Param("newsId") Long newsId, @Param("likeCount") int likeCount);

    void updateViewCount(@Param("newsId") Long newsId, @Param("viewCount") int viewCount);

    boolean existsByLink(String link);

    Long getNewsIdByLink(@Param("link") String link);

    void insertKeyword(Keyword keyword);

    List<Keyword> getAllKeywords();

    void deleteKeyword(int keywordId);

    void insertLike(@Param("userId") String userId, @Param("newsId") Long newsId);

    void deleteLike(@Param("userId") String userId, @Param("newsId") Long newsId);

    List<String> searchSimilarUsers(@Param("userId") String userId, @Param("limit") int limit);

    List<Long> searchNewsIdsBySimilarUsers(@Param("list") List<String> userIds);

    List<Long> searchLikedNewsIdsByUser(@Param("userId") String userId);

    // 통계용
    List<NewsResponseDto> getTopLikedNews(@Param("limit") int limit);

    List<NewsResponseDto> getTopViewedNews(@Param("limit") int limit);

    List<KeywordCountDto> getTotalLikesByKeyword(@Param("limit") int limit);

    Keyword findKeywordByName(@Param("keyword") String keyword);

    void insertKeywordAndGetId(Keyword keyword);

    void subscribeKeyword(@Param("userId") String userId, @Param("keywordId") Long keywordId);

    List<Keyword> findKeywordsByUserId(@Param("userId") String userId);

    void deleteKeywordSubscription(@Param("userId") String userId, @Param("keywordId") Long keywordId);

    // 실시간 알림용
    List<String> findUserIdsByKeywordId(@Param("keywordId") Long keywordId);
}
