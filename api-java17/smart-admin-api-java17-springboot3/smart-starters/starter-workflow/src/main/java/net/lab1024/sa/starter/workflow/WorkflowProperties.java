package net.lab1024.sa.starter.workflow;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 工作流配置
 * <pre>
 * smart:
 *   workflow:
 *     enabled: true
 *     engine: flowable   # 当前仅支持 flowable，预留 activiti
 *     auto-deploy-dir: classpath:processes/
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "smart.workflow")
public class WorkflowProperties {

    private boolean enabled = false;

    /** 流程引擎：flowable（默认） */
    private String engine = "flowable";

    /** 启动时自动部署的流程定义目录 */
    private String autoDeployDir = "classpath:processes/";
}
