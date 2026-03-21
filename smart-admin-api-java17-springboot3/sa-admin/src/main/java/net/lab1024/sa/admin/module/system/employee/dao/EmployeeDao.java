package net.lab1024.sa.admin.module.system.employee.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.system.datascope.DataScope;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeTypeEnum;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.admin.module.system.employee.domain.form.EmployeeQueryForm;
import net.lab1024.sa.admin.module.system.employee.domain.vo.EmployeeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 员工 dao
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-12-09 22:57:49
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface EmployeeDao extends BaseMapper<EmployeeEntity> {
    /**
     * 查询员工列表 - 应用员工数据范围权限
     * 根据角色配置的数据范围，自动过滤用户可查看的员工数据
     * - ME(本人)：只能看自己的信息
     * - DEPARTMENT(本部门)：能看本部门的员工
     * - DEPARTMENT_AND_SUB(本部门及子部门)：能看本部门及下属部门的员工
     * - ALL(全部)：能看所有员工（超管/HR角色默认）
     * 
     * 注意：这里使用 departmentIdList 参数来过滤，因此数据范围条件需要关联部门
     */
    @DataScope(dataScopeType = DataScopeTypeEnum.EMPLOYEE_MANAGEMENT, joinSql = "t_employee.department_id IN (SELECT department_id FROM t_employee WHERE employee_id IN (#employeeIds))")
    List<EmployeeVO> queryEmployee(Page page, @Param("queryForm") EmployeeQueryForm queryForm, @Param("departmentIdList") List<Long> departmentIdList);

    /**
     * 查询员工
     */
    List<EmployeeVO> selectEmployeeByDisabledAndDeleted(@Param("disabledFlag") Boolean disabledFlag, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 更新单个
     */
    void updateDisableFlag(@Param("employeeId") Long employeeId, @Param("disabledFlag") Boolean disabledFlag);

    /**
     * 通过登录名查询
     */
    EmployeeEntity getByLoginName(@Param("loginName") String loginName, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 通过姓名查询
     */
    EmployeeEntity getByActualName(@Param("actualName") String actualName, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 通过手机号查询
     */
    EmployeeEntity getByPhone(@Param("phone") String phone, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 通过邮箱账号查询
     */
    EmployeeEntity getByEmail(@Param("email") String email, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 获取所有员工
     */
    List<EmployeeVO> listAll();

    /**
     * 获取某个部门员工数
     */
    Integer countByDepartmentId(@Param("departmentId") Long departmentId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 获取一批员工
     */
    List<EmployeeVO> getEmployeeByIds(@Param("employeeIds") Collection<Long> employeeIds);

    /**
     * 查询单个员工信息
     */
    EmployeeVO getEmployeeById(@Param("employeeId") Long employeeId);

    /**
     * 获取某个部门的员工
     */
    List<EmployeeEntity> selectByDepartmentId(@Param("departmentId") Long departmentId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 查询某些部门下用户名是xxx的员工
     */
    List<EmployeeEntity> selectByActualName(@Param("departmentIdList") List<Long> departmentIdList, @Param("actualName") String actualName, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 获取某批部门的员工Id
     */
    List<Long> getEmployeeIdByDepartmentIdList(@Param("departmentIds") List<Long> departmentIds, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 获取所有
     */
    List<Long> getEmployeeId(@Param("leaveFlag") Boolean leaveFlag, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 获取某个部门的员工Id
     */
    List<Long> getEmployeeIdByDepartmentId(@Param("departmentId") Long departmentId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 员工重置密码
     */
    Integer updatePassword(@Param("employeeId") Long employeeId, @Param("password") String password);

}