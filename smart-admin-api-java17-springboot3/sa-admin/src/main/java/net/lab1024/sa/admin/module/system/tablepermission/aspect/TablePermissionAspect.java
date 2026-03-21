package net.lab1024.sa.admin.module.system.tablepermission.aspect;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.system.tablepermission.annotation.TablePermission;
import net.lab1024.sa.admin.module.system.tablepermission.constant.TableOperationEnum;
import net.lab1024.sa.admin.module.system.tablepermission.service.TablePermissionService;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 表权限AOP切面
 * 拦截带有 @TablePermission 注解的方法，进行权限检查
 *
 * @Author xzh
 * @Date 2025-11-28
 */
@Aspect
@Component
@Order(10) // 执行顺序：在Sa-Token权限检查之后
@Slf4j
public class TablePermissionAspect {

    @Resource
    private TablePermissionService tablePermissionService;

    /**
     * 定义切点：所有带有 @TablePermission 注解的方法
     */
    @Pointcut("@annotation(net.lab1024.sa.admin.module.system.tablepermission.annotation.TablePermission)")
    public void tablePermissionPointcut() {
    }

    /**
     * 环绕通知：在方法执行前检查表权限
     */
    @Around("tablePermissionPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取方法签名和注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        TablePermission annotation = method.getAnnotation(TablePermission.class);

        if (annotation == null) {
            // 理论上不会执行到这里
            return joinPoint.proceed();
        }

        // 2. 获取注解参数
        String moduleCode = annotation.module();
        TableOperationEnum operation = annotation.operation();
        String customMessage = annotation.message();

        // 3. 获取当前登录用户ID
        Long employeeId = SmartRequestUtil.getRequestUserId();
        if (employeeId == null) {
            log.warn("表权限检查失败：未登录用户尝试访问 {}#{}", 
                    joinPoint.getTarget().getClass().getSimpleName(), 
                    method.getName());
            return ResponseDTO.error(UserErrorCode.LOGIN_STATE_INVALID);
        }

        // 4. 检查权限
        boolean hasPermission = tablePermissionService.hasPermission(employeeId, moduleCode, operation);

        if (!hasPermission) {
            // 无权限，返回错误
            String errorMessage;
            if (StringUtils.isNotBlank(customMessage)) {
                errorMessage = customMessage;
            } else {
                errorMessage = String.format("您没有【%s】的【%s】权限", 
                        getModuleName(moduleCode), 
                        operation.getDesc());
            }

            log.warn("表权限检查失败：员工ID={}, 模块={}, 操作={}, 方法={}", 
                    employeeId, moduleCode, operation.getDesc(), 
                    joinPoint.getTarget().getClass().getSimpleName() + "#" + method.getName());

            return ResponseDTO.userErrorParam(errorMessage);
        }

        // 5. 有权限，继续执行
        log.debug("表权限检查通过：员工ID={}, 模块={}, 操作={}", 
                employeeId, moduleCode, operation.getDesc());

        return joinPoint.proceed();
    }

    /**
     * 根据模块代码获取模块名称（用于错误提示）
     */
    private String getModuleName(String moduleCode) {
        return switch (moduleCode) {
            case "employee" -> "员工管理";
            case "enterprise" -> "企业管理";
            case "invoice" -> "发票管理";
            case "bank" -> "银行账户";
            case "goods" -> "商品管理";
            case "order" -> "订单管理";
            default -> moduleCode;
        };
    }
}








