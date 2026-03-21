package net.lab1024.sa.base.module.support.ai.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.module.support.ai.domain.entity.AIConversationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI会话DAO
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface AIConversationDao extends BaseMapper<AIConversationEntity> {

    /**
     * 根据会话ID查询
     */
    AIConversationEntity selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 分页查询用户的会话列表
     */
    List<AIConversationEntity> queryPageByUserId(Page page, @Param("userId") Long userId);

    /**
     * 根据会话ID删除
     */
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 更新会话统计信息
     */
    int updateConversationStats(@Param("conversationId") String conversationId,
                                @Param("messageCount") Integer messageCount,
                                @Param("totalTokens") Integer totalTokens,
                                @Param("totalCost") java.math.BigDecimal totalCost);

    /**
     * 更新会话标题
     */
    int updateTitle(@Param("conversationId") String conversationId, 
                    @Param("title") String title);
}
