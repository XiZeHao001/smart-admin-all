package net.lab1024.sa.admin.module.system.fieldpermission.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.entity.FieldMetadataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字段元数据DAO
 *
 * @Author
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
@Mapper
public interface FieldMetadataDao extends BaseMapper<FieldMetadataEntity> {

    /**
     * 根据模块代码查询字段元数据
     *
     * @param moduleCode  模块代码
     * @param enabledFlag 是否启用
     * @return 字段元数据列表
     */
    List<FieldMetadataEntity> selectByModule(@Param("moduleCode") String moduleCode, @Param("enabledFlag") Boolean enabledFlag);

    /**
     * 根据模块代码查询单条字段元数据（默认查启用的）
     *
     * @param moduleCode 模块代码
     * @return 字段元数据
     */
    default FieldMetadataEntity selectByModuleCode(String moduleCode) {
        List<FieldMetadataEntity> list = selectByModule(moduleCode, true);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询所有模块的字段元数据
     *
     * @param enabledFlag 是否启用
     * @return 字段元数据列表
     */
    List<FieldMetadataEntity> selectAll(@Param("enabledFlag") Boolean enabledFlag);
}

