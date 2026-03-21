package net.lab1024.sa.base.module.support.ai.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI对话角色枚举
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum AIConversationRoleEnum {

    /**
     * 系统提示词
     */
    SYSTEM("system", "系统"),

    /**
     * 用户消息
     */
    USER("user", "用户"),

    /**
     * AI助手回复
     */
    ASSISTANT("assistant", "助手"),

    /**
     * 函数调用
     */
    FUNCTION("function", "函数"),

    /**
     * 工具调用
     */
    TOOL("tool", "工具"),
    ;

    private final String value;
    private final String desc;

    /**
     * 根据值获取枚举
     */
    public static AIConversationRoleEnum getByValue(String value) {
        for (AIConversationRoleEnum role : values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        return null;
    }
}
