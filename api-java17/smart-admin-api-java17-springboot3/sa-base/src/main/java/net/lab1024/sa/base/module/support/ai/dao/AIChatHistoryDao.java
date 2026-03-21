package net.lab1024.sa.base.module.support.ai.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.base.module.support.ai.domain.entity.AIChatHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI聊天历史DAO
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface AIChatHistoryDao extends BaseMapper<AIChatHistoryEntity> {

    /**
     * 根据会话ID查询历史记录 (限制条数)
     */
    List<AIChatHistoryEntity> selectByConversationId(@Param("conversationId") String conversationId, 
                                                      @Param("limit") Integer limit);

    /**
     * 根据会话ID查询所有历史记录
     */
    List<AIChatHistoryEntity> selectAllByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID删除历史记录
     */
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 查询会话的消息数量
     */
    Integer countByConversationId(@Param("conversationId") String conversationId);

    /**
     * 查询用户的历史记录（限制条数）
     */
    List<AIChatHistoryEntity> selectByUserId(@Param("userId") Long userId, 
                                             @Param("limit") Integer limit);
}
