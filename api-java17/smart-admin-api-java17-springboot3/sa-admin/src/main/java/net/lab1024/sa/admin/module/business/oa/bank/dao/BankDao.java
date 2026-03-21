package net.lab1024.sa.admin.module.business.oa.bank.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.oa.bank.domain.BankEntity;
import net.lab1024.sa.admin.module.business.oa.bank.domain.BankQueryForm;
import net.lab1024.sa.admin.module.business.oa.bank.domain.BankVO;
import net.lab1024.sa.admin.module.system.datascope.DataScope;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OA办公-OA银行信息
 *
 * @Author 1024创新实验室:善逸
 * @Date 2022/6/23 21:59:22
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface BankDao extends BaseMapper<BankEntity> {

    /**
     * 根据账号查询
     */
    BankEntity queryByAccountNumber(@Param("enterpriseId") Long enterpriseId, @Param("accountNumber") String accountNumber, @Param("excludeBankId") Long excludeBankId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 删除银行信息
     *
     */
    void deleteBank(@Param("bankId") Long bankId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 银行信息分页查询 - 应用银行账户数据范围权限
     * 根据角色配置的数据范围，自动过滤用户可查看的银行账户数据
     * - ME(本人)：只能看自己创建的银行账户
     * - DEPARTMENT(本部门)：能看本部门员工创建的银行账户
     * - DEPARTMENT_AND_SUB(本部门及子部门)：能看本部门及下属部门创建的银行账户
     * - ALL(全部)：能看所有银行账户（超管/财务角色默认）
     */
    @DataScope(dataScopeType = DataScopeTypeEnum.BANK_ACCOUNT, joinSql = "t_oa_bank.create_user_id IN (#employeeIds)")
    List<BankVO> queryPage(Page page, @Param("queryForm") BankQueryForm queryForm);

    /**
     * 查询银行信息详情
     */
    BankVO getDetail(@Param("bankId") Long bankId, @Param("deletedFlag") Boolean deletedFlag);
}
