package net.lab1024.sa.base.config;

import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.domain.DataScopePlugin;
import net.lab1024.sa.base.handler.MybatisPlusFillHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源配置（多数据源版本）
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2017-11-28 15:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    // ⭐ 注意：使用 Dynamic Datasource 后，无需手动创建 DataSource Bean
    // Spring Boot 会自动根据配置文件创建多数据源

    @Value("${spring.datasource.dynamic.druid.username:druid}")
    String druidUserName;

    @Value("${spring.datasource.dynamic.druid.password:1024}")
    String druidPassword;

    @Value("${spring.datasource.dynamic.druid.login.enabled:false}")
    boolean druidLoginEnable;

    @Value("${spring.datasource.dynamic.druid.method.pointcut:net.lab1024.sa..*Service.*}")
    String methodPointcut;

    @jakarta.annotation.Resource
    private MybatisPlusInterceptor paginationInterceptor;

    @jakarta.annotation.Resource
    private DataScopePlugin dataScopePlugin;

    /**
     * ⭐ 重要：配置 SqlSessionFactory（动态数据源兼容）
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        // 使用动态数据源
        factoryBean.setDataSource(dataSource);

        // 设置Mapper XML 文件的位置
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:/mapper/**/*.xml");
        factoryBean.setMapperLocations(resources);

        // 设置 MyBatis-Plus 插件
        List<Interceptor> pluginsList = new ArrayList<>();
        pluginsList.add(paginationInterceptor);
        if (dataScopePlugin != null) {
            // 添加数据权限插件
            pluginsList.add(dataScopePlugin);
        }
        factoryBean.setPlugins(pluginsList.toArray(new Interceptor[0]));

        // 设置全局配置，添加字段自动填充处理
        factoryBean.setGlobalConfig(new GlobalConfig().setBanner(false).setMetaObjectHandler(new MybatisPlusFillHandler()));

        return factoryBean.getObject();
    }

    /**
     * Druid 监控页面（非正式环境才加载）
     */
    // 条件注解：只有在非生产环境才加载（由 SystemEnvironmentConfig 控制）
    @Conditional(SystemEnvironmentConfig.class)
    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings("/druid/*");

        // 设置初始化参数（用户名、密码、是否允许重置数据）
        Map<String, String> initParameters = new HashMap<>();
        if (druidLoginEnable) {
            initParameters.put("loginUsername", druidUserName);
            initParameters.put("loginPassword", druidPassword);
        }
        initParameters.put("resetEnable", "false");
        servletRegistrationBean.setInitParameters(initParameters);

        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/*");
        return filterRegistrationBean;
    }

    @Bean
    public JdkRegexpMethodPointcut jdkRegexpMethodPointcut() {
        JdkRegexpMethodPointcut jdkRegexpMethodPointcut = new JdkRegexpMethodPointcut();
        jdkRegexpMethodPointcut.setPatterns(methodPointcut);
        return jdkRegexpMethodPointcut;
    }

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor() {
        DefaultPointcutAdvisor pointcutAdvisor = new DefaultPointcutAdvisor();
        pointcutAdvisor.setPointcut(jdkRegexpMethodPointcut());
        pointcutAdvisor.setAdvice(new DruidStatInterceptor());
        return pointcutAdvisor;
    }
}