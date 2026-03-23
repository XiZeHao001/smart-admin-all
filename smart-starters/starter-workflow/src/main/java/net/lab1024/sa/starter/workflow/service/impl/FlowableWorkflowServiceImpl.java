package net.lab1024.sa.starter.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.workflow.service.WorkflowService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;

import java.util.Map;

/**
 * 基于 Flowable 的工作流实现
 */
@Slf4j
public class FlowableWorkflowServiceImpl implements WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public FlowableWorkflowServiceImpl(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        log.info("[SmartStarter-Workflow] 使用 Flowable 工作流引擎");
    }

    @Override
    public String startProcess(String processKey, String businessKey, Map<String, Object> variables) {
        var instance = runtimeService.startProcessInstanceByKey(processKey, businessKey, variables);
        log.info("[Workflow] 启动流程 processKey={}, businessKey={}, instanceId={}",
                processKey, businessKey, instance.getId());
        return instance.getId();
    }

    @Override
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
        log.info("[Workflow] 完成任务 taskId={}", taskId);
    }

    @Override
    public long countPendingTasks(String assignee) {
        return taskService.createTaskQuery()
                .taskAssignee(assignee)
                .count();
    }
}
