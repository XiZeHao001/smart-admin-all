package net.lab1024.sa.base.common.datasource;

/**
 * 数据源常量
 */
public interface DataSourceConstants {
    /** 主数据源（PostgreSQL） */
    String MASTER = "master";

    /** MySQL 从库 */
    String SLAVE_MYSQL = "slave_mysql";

    /** SQL Server 客户库 */
    String CLIENT_SQLSERVER = "client_sqlserver";

    /** Oracle 遗留系统 */
    String LEGACY_ORACLE = "legacy_oracle";

    /** 达梦数据库 */
    String DM_DATABASE = "dm_database";

    /** 人大金仓 */
    String KINGBASE_DATABASE = "kingbase_database";
}
