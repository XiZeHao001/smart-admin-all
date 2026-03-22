package net.lab1024.sa.starter.tenant;

import net.lab1024.sa.starter.tenant.interceptor.TenantInterceptor;
import net.lab1024.sa.starter.tenant.mybatis.TenantLineHandlerImpl;
import net.lab1024.sa.starter.tenant.service.TenantContextService;
import net.lab1024.sa.starter.tenant.service.impl.TenantContextServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 多租户自动配置
 * <p>触发条件：{@code smart.tenant.enabled=true}</p>
 */
@Configuration
@EnableConfigurationProperties(TenantProperties.class)
@ConditionalOnProperty(prefix = "smart.tenant", name = "enabled", havingValue = "true")
public class TenantAutoConfiguration implements WebMvcConfigurer {

    private final TenantProperties properties;
    private final TenantContextService tenantContextService;

    public TenantAutoConfiguration(TenantProperties properties, TenantContextService tenantContextService) {
        this.properties = properties;
        this.tenantContextService = tenantContextService;
    }

    @Bean
    @ConditionalOnMissingBean
    public TenantContextService tenantContextService() {
        return new TenantContextServiceImpl();
    }

    /**
     * MyBatis Plus 多租户拦截器（column 隔离模式）
     * 业务方若已配置 MybatisPlusInterceptor，需手动添加 TenantLineInnerInterceptor
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "smart.tenant", name = "isolation-mode", havingValue = "column", matchIfMissing = true)
    public MybatisPlusInterceptor mybatisPlusInterceptor(TenantContextService tenantContextService) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(
            new TenantLineInnerInterceptor(new TenantLineHandlerImpl(properties, tenantContextService))
        );
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantInterceptor(tenantContextService, properties))
                .addPathPatterns("/**");
    }
}
