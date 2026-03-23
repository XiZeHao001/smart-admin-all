package net.lab1024.sa.starter.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.search.SearchProperties;
import net.lab1024.sa.starter.search.service.SearchService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch 8.x 实现
 */
@Slf4j
public class ElasticsearchSearchServiceImpl implements SearchService {

    private final ElasticsearchClient client;
    private final String indexPrefix;

    public ElasticsearchSearchServiceImpl(ElasticsearchClient client, SearchProperties properties) {
        this.client = client;
        this.indexPrefix = properties.getIndexPrefix();
        log.info("[SmartStarter-Search] 使用 Elasticsearch，indexPrefix={}", indexPrefix);
    }

    private String realIndex(String index) {
        return indexPrefix + index;
    }

    @Override
    public void upsert(String index, String id, Map<String, Object> source) {
        try {
            client.index(IndexRequest.of(req -> req
                    .index(realIndex(index))
                    .id(id)
                    .document(source)));
        } catch (IOException e) {
            throw new RuntimeException("[Search] upsert 失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String index, String id) {
        try {
            client.delete(DeleteRequest.of(req -> req.index(realIndex(index)).id(id)));
        } catch (IOException e) {
            throw new RuntimeException("[Search] delete 失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> search(String index, String keyword, List<String> fields, int from, int size) {
        try {
            SearchResponse<Map> response = client.search(req -> req
                    .index(realIndex(index))
                    .from(from)
                    .size(size)
                    .query(q -> q.multiMatch(mm -> mm
                            .query(keyword)
                            .fields(fields))),
                    Map.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("[Search] search 失败: " + e.getMessage(), e);
        }
    }
}
