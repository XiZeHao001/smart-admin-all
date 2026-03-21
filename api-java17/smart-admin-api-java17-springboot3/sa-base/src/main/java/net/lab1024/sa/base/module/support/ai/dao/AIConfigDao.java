package net.lab1024.sa.base.module.support.ai.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.module.support.ai.domain.entity.AIConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI配置DAO
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface AIConfigDao extends BaseMapper<AIConfigEntity> {

    /**
     * 分页查询
     */
    List<AIConfigEntity> queryPage(Page page, 
                                   @Param("provider") String provider,
                                   @Param("modelType") String modelType,
                                   @Param("enabledFlag") Boolean enabledFlag,
                                   @Param("searchWord") String searchWord);

    /**
     * 根据配置Key查询
     */
    AIConfigEntity selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 查询默认模型
     */
    AIConfigEntity selectDefaultModel();

    /**
     * 查询所有启用的配置
     */
    List<AIConfigEntity> selectEnabledConfigs();

    /**
     * 根据提供商查询
     */
    List<AIConfigEntity> selectByProvider(@Param("provider") String provider);

    /**
     * 根据模型类型查询
     */
    List<AIConfigEntity> selectByModelType(@Param("modelType") String modelType);

    /**
     * 设置默认模型
     */
    int updateDefaultFlag(@Param("id") Long id, @Param("defaultFlag") Boolean defaultFlag);

    /**
     * 清除所有默认标记
     */
    int clearAllDefaultFlags();
}
