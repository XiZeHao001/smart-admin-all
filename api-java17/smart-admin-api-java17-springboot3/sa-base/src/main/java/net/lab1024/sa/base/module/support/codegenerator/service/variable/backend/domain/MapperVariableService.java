package net.lab1024.sa.base.module.support.codegenerator.service.variable.backend.domain;

import cn.hutool.core.bean.BeanUtil;
import net.lab1024.sa.base.module.support.codegenerator.constant.CodeQueryFieldQueryTypeEnum;
import net.lab1024.sa.base.module.support.codegenerator.domain.form.CodeGeneratorConfigForm;
import net.lab1024.sa.base.module.support.codegenerator.domain.model.CodeQueryField;
import net.lab1024.sa.base.module.support.codegenerator.service.variable.CodeGenerateBaseVariableService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 1024创新实验室-主任:卓大
 * @Date 2022/9/29 17:20:41
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */

public class MapperVariableService extends CodeGenerateBaseVariableService {

    @Override
    public boolean isSupport(CodeGeneratorConfigForm form) {
        return true;
    }

    @Override
    public Map<String, Object> getInjectVariablesMap(CodeGeneratorConfigForm form) {
        Map<String, Object> variablesMap = new HashMap<>();
        List<Map<String, Object>> finalQueryFiledList = new ArrayList<>();

        for (CodeQueryField queryField : form.getQueryFields()) {
            Map<String, Object> fieldMap = BeanUtil.beanToMap(queryField);
            finalQueryFiledList.add(fieldMap);

            // ------------------------------------------------------------
            // 核心修改：适配 PostgreSQL 语法 (INSTR -> ILIKE CONCAT)
            // ------------------------------------------------------------

            //处理模糊查询 (LIKE)
            if (CodeQueryFieldQueryTypeEnum.LIKE.getValue().equals(queryField.getQueryTypeEnum())) {
                StringBuilder stringBuilder = new StringBuilder();
                List<String> columnNameList = queryField.getColumnNameList();

                // 参数占位符：#{queryForm.fieldName}
                String paramPlaceholder = "#{queryForm." + queryField.getFieldName() + "}";

                if (columnNameList.size() == 1) {
                    // 单字段模糊查询
                    // 修改前: AND INSTR(table.col, #{val})
                    // 修改后: AND table.col ILIKE CONCAT('%', #{val}, '%')
                    stringBuilder.append("AND ")
                            .append(form.getTableName()).append(".").append(columnNameList.get(0))
                            .append(" ILIKE CONCAT('%', ")
                            .append(paramPlaceholder)
                            .append(", '%')");
                } else {
                    // 多字段模糊查询 (OR)
                    for (int i = 0; i < columnNameList.size(); i++) {
                        String column = form.getTableName() + "." + columnNameList.get(i);

                        if (i == 0) {
                            // 第一个条件，带括号开头
                            stringBuilder.append("AND (\n                  ")
                                    .append(column)
                                    .append(" ILIKE CONCAT('%', ")
                                    .append(paramPlaceholder)
                                    .append(", '%')");
                        } else {
                            // 后续条件，用 OR 连接
                            stringBuilder.append("\n                  OR ")
                                    .append(column)
                                    .append(" ILIKE CONCAT('%', ")
                                    .append(paramPlaceholder)
                                    .append(", '%')");
                        }
                    }
                    // 闭合括号
                    stringBuilder.append("\n                )");
                }
                fieldMap.put("likeStr", stringBuilder.toString());
            }
            //  处理字典查询 (DICT)
            // 原逻辑用的是 INSTR，推测可能是为了匹配逗号分隔的字符串，改为 PostgreSQL 兼容写法
            else if (CodeQueryFieldQueryTypeEnum.DICT.equalsValue(queryField.getQueryTypeEnum())) {
                String columnName = form.getTableName() + "." + queryField.getColumnNameList().get(0);
                String paramPlaceholder = "#{queryForm." + queryField.getFieldName() + "}";

                // 修改后: AND table.col ILIKE CONCAT('%', #{val}, '%')
                // 注意：如果是精确匹配，建议直接用 =，但为了保持原逻辑这里用 ILIKE
                String stringBuilder = "AND " + columnName +
                        " ILIKE CONCAT('%', " + paramPlaceholder + ", '%')";

                fieldMap.put("likeStr", stringBuilder);
            }
            else {
                // 其他类型 (等于、日期范围等)，通常不需要特殊处理，除非涉及 DATE_FORMAT
                // 如果生成器其他地方有 DATE_FORMAT，也需要去对应的地方改
                fieldMap.put("columnName", queryField.getColumnNameList().get(0));
            }
        }

        variablesMap.put("queryFields", finalQueryFiledList);
        variablesMap.put("daoClassName", form.getBasic().getJavaPackageName() + ".dao." + form.getBasic().getModuleName() + "Dao");
        return variablesMap;
    }
}