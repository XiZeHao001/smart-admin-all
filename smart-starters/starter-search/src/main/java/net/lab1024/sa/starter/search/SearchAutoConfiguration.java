package net.lab1024.sa.starter.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import net.lab1024.sa.starter.search.service.SearchService;
import net.lab1024.sa.starter.search.service.impl.ElasticsearchSearchServiceImpl;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 搜索引擎自动配置
 * <p>触发条件：{@code smart.search.enabled=true} 且 classpath 中存在 ES 客户端</p>
 */
@Configuration
@EnableConfigurationProperties(SearchProperties.class)
@ConditionalOnProperty(prefix = "smart.search", name = "enabled", havingValue = "true")
@ConditionalOnClass(ElasticsearchClient.class)
public class SearchAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchClient elasticsearchClient(SearchProperties properties) {
        HttpHost[] hosts = properties.getUris().stream()
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);

        var builder = RestClient.builder(hosts);

        if (StringUtils.hasText(properties.getUsername())) {
            var credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword())
            );
            builder.setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            );
        }

        ElasticsearchTransport transport = new RestClientTransport(builder.build(), new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    @Bean
    @ConditionalOnMissingBean(SearchService.class)
    public SearchService searchService(ElasticsearchClient client, SearchProperties properties) {
        return new ElasticsearchSearchServiceImpl(client, properties);
    }
}
