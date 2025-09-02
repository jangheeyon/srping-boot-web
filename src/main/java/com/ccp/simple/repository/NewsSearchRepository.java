package com.ccp.simple.repository;

import com.ccp.simple.document.NewsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NewsSearchRepository extends ElasticsearchRepository<NewsDocument, Long> {
}
