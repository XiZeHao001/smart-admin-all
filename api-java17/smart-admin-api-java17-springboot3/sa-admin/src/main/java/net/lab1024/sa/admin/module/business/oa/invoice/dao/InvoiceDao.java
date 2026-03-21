package net.lab1024.sa.admin.module.business.oa.invoice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.oa.invoice.domain.InvoiceEntity;
import net.lab1024.sa.admin.module.business.oa.invoice.domain.InvoiceQueryForm;
import net.lab1024.sa.admin.module.business.oa.invoice.domain.InvoiceVO;
import net.lab1024.sa.admin.module.system.datascope.DataScope;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OA发票信息
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2022-06-23 19:32:59
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface InvoiceDao extends BaseMapper<InvoiceEntity> {

    /**
     * 根据账号查询
     * @param enterpriseId
     * @param accountNumber
     * @param excludeInvoiceId
     * @param deletedFlag
     * @return
     */
    InvoiceEntity queryByAccountNumber(@Param("enterpriseId") Long enterpriseId, @Param("accountNumber") String accountNumber, @Param("excludeInvoiceId") Long excludeInvoiceId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 删除发票信息
     *
     * @param invoiceId
     * @param deletedFlag
     */
    void deleteInvoice(@Param("invoiceId") Long invoiceId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 发票信息分页查询 - 应用发票数据范围权限
     * 根据角色配置的数据范围，自动过滤用户可查看的发票数据
     * - ME(本人)：只能看自己创建的发票
     * - DEPARTMENT(本部门)：能看本部门员工创建的发票
     * - DEPARTMENT_AND_SUB(本部门及子部门)：能看本部门及下属部门创建的发票
     * - ALL(全部)：能看所有发票（超管/财务角色默认）
     *
     * @param page
     * @param queryForm
     * @return
     */
    @DataScope(dataScopeType = DataScopeTypeEnum.INVOICE, joinSql = "t_oa_invoice.create_user_id IN (#employeeIds)")
    List<InvoiceVO> queryPage(Page page, @Param("queryForm") InvoiceQueryForm queryForm);

    /**
     * 查询发票信息详情
     * @param invoiceId
     * @param deletedFlag
     * @return
     */
    InvoiceVO getDetail(@Param("invoiceId") Long invoiceId, @Param("deletedFlag") Boolean deletedFlag);
}
