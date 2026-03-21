package net.lab1024.sa.base.module.support.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.base.common.controller.SupportBaseController;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.constant.SwaggerTagConst;
import net.lab1024.sa.base.module.support.ai.domain.form.AIConfigForm;
import net.lab1024.sa.base.module.support.ai.domain.form.AIConfigQueryForm;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIConfigVO;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIModelConfigVO;
import net.lab1024.sa.base.module.support.ai.service.AIConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI配置管理Controller
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
@Tag(name = SwaggerTagConst.Support.AI_CONFIG)
public class AIConfigController extends SupportBaseController {

    @Resource
    private AIConfigService aiConfigService;

    @Operation(summary = "分页查询AI配置")
    @PostMapping("/ai/config/query")
    public ResponseDTO<PageResult<AIConfigVO>> queryPage(@RequestBody @Valid AIConfigQueryForm queryForm) {
        return aiConfigService.queryPage(queryForm);
    }

    @Operation(summary = "查询AI配置详情")
    @GetMapping("/ai/config/detail/{id}")
    public ResponseDTO<AIConfigVO> getDetail(@PathVariable Long id) {
        return aiConfigService.getDetail(id);
    }

    @Operation(summary = "查询所有启用的模型配置")
    @GetMapping("/ai/config/models")
    public ResponseDTO<List<AIModelConfigVO>> listEnabledModels() {
        return aiConfigService.listEnabledModels();
    }

    @Operation(summary = "添加AI配置")
    @PostMapping("/ai/config/add")
    public ResponseDTO<String> add(@RequestBody @Valid AIConfigForm form) {
        return aiConfigService.add(form);
    }

    @Operation(summary = "更新AI配置")
    @PostMapping("/ai/config/update")
    public ResponseDTO<String> update(@RequestBody @Valid AIConfigForm form) {
        return aiConfigService.update(form);
    }

    @Operation(summary = "删除AI配置")
    @GetMapping("/ai/config/delete/{id}")
    public ResponseDTO<String> delete(@PathVariable Long id) {
        return aiConfigService.delete(id);
    }

    @Operation(summary = "设置默认模型")
    @GetMapping("/ai/config/set-default/{id}")
    public ResponseDTO<String> setDefault(@PathVariable Long id) {
        return aiConfigService.setDefault(id);
    }
}
