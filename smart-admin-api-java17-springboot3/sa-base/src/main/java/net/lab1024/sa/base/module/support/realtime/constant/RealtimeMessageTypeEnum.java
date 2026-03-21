package net.lab1024.sa.base.module.support.realtime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 实时消息类型枚举
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum RealtimeMessageTypeEnum implements BaseEnum {

    // ========== AI相关 ==========
    /**
     * AI对话流式输出
     */
    AI_CHAT_STREAM("AI_CHAT_STREAM", "AI对话流式输出"),

    /**
     * AI函数调用
     */
    AI_FUNCTION_CALL("AI_FUNCTION_CALL", "AI函数调用"),

    // ========== 进度相关 ==========
    /**
     * 任务进度更新
     */
    PROGRESS("PROGRESS", "任务进度"),

    /**
     * 任务状态变更
     */
    TASK_STATUS("TASK_STATUS", "任务状态"),

    // ========== 通知相关 ==========
    /**
     * 系统通知
     */
    NOTIFICATION("NOTIFICATION", "系统通知"),

    /**
     * 审批通知
     */
    APPROVAL("APPROVAL", "审批通知"),

    /**
     * 消息提醒
     */
    MESSAGE("MESSAGE", "消息提醒"),

    // ========== 日志相关 ==========
    /**
     * 日志流式输出
     */
    LOG_STREAM("LOG_STREAM", "日志流"),

    /**
     * 操作日志
     */
    OPERATION_LOG("OPERATION_LOG", "操作日志"),

    // ========== 监控相关 ==========
    /**
     * 系统指标数据
     */
    METRIC("METRIC", "系统指标"),

    /**
     * 告警信息
     */
    ALERT("ALERT", "告警信息"),

    // ========== 协同相关 ==========
    /**
     * 在线用户变更
     */
    ONLINE_USER("ONLINE_USER", "在线用户"),

    /**
     * 数据变更同步
     */
    DATA_SYNC("DATA_SYNC", "数据同步"),

    // ========== 自定义 ==========
    /**
     * 自定义消息（业务模块可扩展）
     */
    CUSTOM("CUSTOM", "自定义消息"),

    /**
     * 心跳消息
     */
    HEARTBEAT("HEARTBEAT", "心跳");

    private final String value;
    private final String desc;

    public static RealtimeMessageTypeEnum getByValue(String value) {
        for (RealtimeMessageTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}



