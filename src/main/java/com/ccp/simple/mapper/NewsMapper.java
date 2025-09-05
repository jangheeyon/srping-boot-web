package com.ccp.simple.mapper;

import com.ccp.simple.domain.Keyword;
import com.ccp.simple.domain.News;
import com.ccp.simple.dto.NewsResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {
    List<NewsResponseDto> getAllNews();

    List<NewsResponseDto> findNewsByIds(List<Long> ids);

    void insertNews(News news);

    void insertNewsKeywordMapping(@Param("newsId") Long newsId, @Param("keywordId") Long keywordId);

    void updateNewsVisibility(@Param("newsId") Long newsId, @Param("visible") boolean visible);

    void updateLikeCount(@Param("newsId") Long newsId, @Param("likeCount") int likeCount);

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
}
