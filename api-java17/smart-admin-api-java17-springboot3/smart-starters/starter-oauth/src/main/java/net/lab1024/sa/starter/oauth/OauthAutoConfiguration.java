package net.lab1024.sa.starter.oauth;

import net.lab1024.sa.starter.oauth.service.OauthService;
import net.lab1024.sa.starter.oauth.service.impl.DefaultOauthServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 第三方登录自动配置
 * <p>触发条件：{@code smart.oauth.enabled=true}</p>
 */
@Configuration
@EnableConfigurationProperties(OauthProperties.class)
@ConditionalOnProperty(prefix = "smart.oauth", name = "enabled", havingValue = "true")
public class OauthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebClient oauthWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean(OauthService.class)
    public OauthService oauthService(OauthProperties properties, WebClient oauthWebClient) {
        return new DefaultOauthServiceImpl(properties, oauthWebClient);
    }
}
