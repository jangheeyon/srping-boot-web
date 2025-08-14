package com.ccp.simple.service.impl;

import com.ccp.simple.domain.News;
import com.ccp.simple.dto.RegistNewsResponseDto;
import com.ccp.simple.mapper.NewsMapper;
import com.ccp.simple.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsMapper newsMapper;


    @Override
    public List<News> getAllNews() {
        return newsMapper.getAllNews();
    }

    @Override
    public void insertNews(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode itemList = objectMapper.readTree(responseBody).get("items");
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        List<News> newsList = new ArrayList<>();
        for (JsonNode item : itemList) {
            News news = new News();
            news.setTitle(Jsoup.parse(item.get("title").asText()).text());
            news.setDescription(Jsoup.parse(item.get("description").asText()).text());
            news.setLink(item.get("link").asText());

            String pubDateStr = item.get("pubDate").asText();
            news.setPubDt(ZonedDateTime.parse(pubDateStr, formatter).toLocalDateTime());

            newsList.add(news);
        }
        newsMapper.insertNews(newsList);
    }
}
