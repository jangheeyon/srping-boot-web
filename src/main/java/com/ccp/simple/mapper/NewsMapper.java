package com.ccp.simple.mapper;

import com.ccp.simple.domain.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {
    List<News> getAllNews();

    void insertNews(@Param("newsList") List<News> newsList);
}
