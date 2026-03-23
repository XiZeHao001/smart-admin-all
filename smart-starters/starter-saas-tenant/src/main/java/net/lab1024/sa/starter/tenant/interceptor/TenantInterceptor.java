package net.lab1024.sa.starter.tenant.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.tenant.TenantProperties;
import net.lab1024.sa.starter.tenant.service.TenantContextService;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户拦截器 — 从请求 Header 解析租户 ID 并写入线程上下文
 */
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    private final TenantContextService tenantContextService;
    private final TenantProperties properties;

    public TenantInterceptor(TenantContextService tenantContextService, TenantProperties properties) {
        this.tenantContextService = tenantContextService;
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantIdStr = request.getHeader(properties.getTenantIdHeader());
        if (StringUtils.hasText(tenantIdStr)) {
            try {
                tenantContextService.setCurrentTenantId(Long.parseLong(tenantIdStr));
            } catch (NumberFormatException e) {
                log.warn("[Tenant] 非法 TenantId: {}", tenantIdStr);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        tenantContextService.clear();
    }
}
