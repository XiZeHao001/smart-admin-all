package net.lab1024.sa.starter.tenant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 多租户配置
 * <pre>
 * smart:
 *   tenant:
 *     enabled: true
 *     isolation-mode: schema   # schema（Schema隔离）| datasource（独立数据源）| column（字段隔离）
 *     tenant-id-header: X-Tenant-Id
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "smart.tenant")
public class TenantProperties {

    private boolean enabled = false;

    /**
     * 隔离模式
     * <ul>
     *   <li>column     — 同库同表，tenant_id 字段区分（默认，成本最低）</li>
     *   <li>schema     — 同库不同 Schema</li>
     *   <li>datasource — 独立数据源，彻底隔离</li>
     * </ul>
     */
    private String isolationMode = "column";

    /** 从请求头中读取租户 ID 的 Header 名称 */
    private String tenantIdHeader = "X-Tenant-Id";

    /** 不做租户过滤的表名（逗号分隔） */
    private String ignoreTables = "";
}
