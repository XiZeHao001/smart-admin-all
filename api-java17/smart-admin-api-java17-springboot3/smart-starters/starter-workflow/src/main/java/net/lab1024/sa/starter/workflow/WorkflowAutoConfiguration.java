package net.lab1024.sa.starter.workflow;

import net.lab1024.sa.starter.workflow.service.WorkflowService;
import net.lab1024.sa.starter.workflow.service.impl.FlowableWorkflowServiceImpl;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工作流自动配置
 * <p>触发条件：{@code smart.workflow.enabled=true} 且 classpath 中存在 Flowable</p>
 */
@Configuration
@EnableConfigurationProperties(WorkflowProperties.class)
@ConditionalOnProperty(prefix = "smart.workflow", name = "enabled", havingValue = "true")
@ConditionalOnClass({RuntimeService.class, TaskService.class})
public class WorkflowAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WorkflowService.class)
    public WorkflowService workflowService(RuntimeService runtimeService, TaskService taskService) {
        return new FlowableWorkflowServiceImpl(runtimeService, taskService);
    }
}
