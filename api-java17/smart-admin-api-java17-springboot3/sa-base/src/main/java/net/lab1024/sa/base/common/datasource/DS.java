package net.lab1024.sa.base.common.datasource;

import java.lang.annotation.*;

/**
 * 数据源切换注解（使用 Dynamic Datasource 的 @DS）
 * 可用于类或方法上
 *
 * @Author
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {
    /**
     * 数据源名称（对应配置文件中的 key）
     * 默认使用 master
     */
    String value() default "master";
}
