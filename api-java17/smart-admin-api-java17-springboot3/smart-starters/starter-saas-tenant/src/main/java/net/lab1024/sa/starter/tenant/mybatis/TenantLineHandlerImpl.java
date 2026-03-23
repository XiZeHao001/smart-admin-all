package net.lab1024.sa.starter.tenant.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.lab1024.sa.starter.tenant.TenantProperties;
import net.lab1024.sa.starter.tenant.service.TenantContextService;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MyBatis Plus 多租户 Handler（column 隔离模式）
 * <p>自动为 SQL 添加 tenant_id 条件</p>
 */
public class TenantLineHandlerImpl implements TenantLineHandler {

    private final TenantContextService tenantContextService;
    private final Set<String> ignoreTables;

    public TenantLineHandlerImpl(TenantProperties properties, TenantContextService tenantContextService) {
        this.tenantContextService = tenantContextService;
        String raw = properties.getIgnoreTables();
        this.ignoreTables = StringUtils.hasText(raw)
                ? Arrays.stream(raw.split(",")).map(String::trim).collect(Collectors.toSet())
                : Set.of();
    }

    @Override
    public Expression getTenantId() {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return new LongValue(tenantId == null ? 0L : tenantId);
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return ignoreTables.contains(tableName.toLowerCase());
    }
}
