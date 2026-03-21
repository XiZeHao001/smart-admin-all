package net.lab1024.sa.admin.module.business.oa.enterprise.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.oa.enterprise.domain.entity.EnterpriseEntity;
import net.lab1024.sa.admin.module.business.oa.enterprise.domain.form.EnterpriseQueryForm;
import net.lab1024.sa.admin.module.business.oa.enterprise.domain.vo.EnterpriseExcelVO;
import net.lab1024.sa.admin.module.business.oa.enterprise.domain.vo.EnterpriseListVO;
import net.lab1024.sa.admin.module.business.oa.enterprise.domain.vo.EnterpriseVO;
import net.lab1024.sa.admin.module.system.datascope.DataScope;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业
 *
 * @Author 1024创新实验室: 开云
 * @Date 2022/7/28 20:37:15
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface EnterpriseDao extends BaseMapper<EnterpriseEntity> {

    /**
     * 根据企业名称查询
     *
     */
    EnterpriseEntity queryByEnterpriseName(@Param("enterpriseName") String enterpriseName, @Param("excludeEnterpriseId") Long excludeEnterpriseId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 删除企业
     */
    void deleteEnterprise(@Param("enterpriseId") Long enterpriseId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 企业分页查询 - 应用企业数据范围权限
     * 根据角色配置的数据范围，自动过滤用户可查看的企业数据
     * - ME(本人)：只能看自己创建的企业
     * - DEPARTMENT(本部门)：能看本部门员工创建的企业
     * - DEPARTMENT_AND_SUB(本部门及子部门)：能看本部门及下属部门创建的企业
     * - ALL(全部)：能看所有企业（超管默认）
     */
    @DataScope(dataScopeType = DataScopeTypeEnum.ENTERPRISE, joinSql = "t_oa_enterprise.create_user_id IN (#employeeIds)")
    List<EnterpriseVO> queryPage(Page page, @Param("queryForm") EnterpriseQueryForm queryForm);

    /**
     * 查询导出的数据 - 应用企业数据范围权限
     */
    @DataScope(dataScopeType = DataScopeTypeEnum.ENTERPRISE, joinSql = "t_oa_enterprise.create_user_id IN (#employeeIds)")
    List<EnterpriseExcelVO> selectExcelExportData(@Param("queryForm") EnterpriseQueryForm queryForm);

    /**
     * 查询企业详情
     *
     */
    EnterpriseVO getDetail(@Param("enterpriseId") Long enterpriseId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 查询列表
     *
     */
    List<EnterpriseListVO> queryList(@Param("type") Integer type, @Param("disabledFlag") Boolean disabledFlag, @Param("deletedFlag") Boolean deletedFlag);


}
