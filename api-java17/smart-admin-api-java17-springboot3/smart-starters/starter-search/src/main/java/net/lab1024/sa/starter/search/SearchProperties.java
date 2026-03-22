package net.lab1024.sa.starter.search;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 搜索引擎配置
 * <pre>
 * smart:
 *   search:
 *     enabled: true
 *     uris:
 *       - http://localhost:9200
 *     username: elastic
 *     password: xxx
 *     index-prefix: smart_   # 索引名前缀，多环境隔离
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "smart.search")
public class SearchProperties {

    private boolean enabled = false;

    private List<String> uris = List.of("http://localhost:9200");
    private String username;
    private String password;

    /** 索引名前缀，建议按环境区分，如 dev_ / prod_ */
    private String indexPrefix = "smart_";
}
