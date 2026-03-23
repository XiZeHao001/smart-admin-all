package net.lab1024.sa.starter.workflow.service;

import java.util.Map;

/**
 * 工作流服务统一抽象
 */
public interface WorkflowService {

    /**
     * 启动流程实例
     *
     * @param processKey  流程定义 Key（BPMN 中定义）
     * @param businessKey 业务主键，用于关联业务数据
     * @param variables   流程变量
     * @return 流程实例 ID
     */
    String startProcess(String processKey, String businessKey, Map<String, Object> variables);

    /**
     * 完成当前任务（审批通过/拒绝等）
     *
     * @param taskId    任务 ID
     * @param variables 任务变量（如 approved=true/false）
     */
    void completeTask(String taskId, Map<String, Object> variables);

    /**
     * 查询当前用户待办任务数量
     *
     * @param assignee 处理人（通常为用户 ID 字符串）
     */
    long countPendingTasks(String assignee);
}
