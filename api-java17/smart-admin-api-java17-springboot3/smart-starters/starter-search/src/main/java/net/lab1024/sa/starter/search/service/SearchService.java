package net.lab1024.sa.starter.search.service;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务统一抽象
 */
public interface SearchService {

    /**
     * 写入/更新文档
     *
     * @param index  索引名（不含前缀，前缀由配置自动拼接）
     * @param id     文档 ID
     * @param source 文档内容
     */
    void upsert(String index, String id, Map<String, Object> source);

    /**
     * 删除文档
     */
    void delete(String index, String id);

    /**
     * 全文检索
     *
     * @param index   索引名
     * @param keyword 关键词
     * @param fields  检索字段列表
     * @param from    分页起始
     * @param size    每页条数
     * @return 命中文档列表（原始 source）
     */
    List<Map<String, Object>> search(String index, String keyword, List<String> fields, int from, int size);
}
