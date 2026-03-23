package net.lab1024.sa.starter.tenant.service.impl;

import net.lab1024.sa.starter.tenant.service.TenantContextService;

/**
 * 基于 ThreadLocal 的租户上下文实现
 */
public class TenantContextServiceImpl implements TenantContextService {

    private static final ThreadLocal<Long> TENANT_ID_HOLDER = new ThreadLocal<>();

    @Override
    public Long getCurrentTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    @Override
    public void setCurrentTenantId(Long tenantId) {
        TENANT_ID_HOLDER.set(tenantId);
    }

    @Override
    public void clear() {
        TENANT_ID_HOLDER.remove();
    }
}
