package net.lab1024.sa.starter.tenant.service;

/**
 * 租户上下文服务
 * <p>负责从请求中解析租户 ID，并在线程上下文中传递。</p>
 */
public interface TenantContextService {

    /** 获取当前线程的租户 ID */
    Long getCurrentTenantId();

    /** 设置当前线程的租户 ID（拦截器调用） */
    void setCurrentTenantId(Long tenantId);

    /** 清除当前线程的租户 ID（请求结束时调用） */
    void clear();
}
