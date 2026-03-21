package net.lab1024.sa.base.module.support.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.module.support.ai.constant.AIProviderEnum;
import net.lab1024.sa.base.module.support.ai.dao.AIConfigDao;
import net.lab1024.sa.base.module.support.ai.domain.entity.AIConfigEntity;
import net.lab1024.sa.base.module.support.ai.domain.form.AIConfigForm;
import net.lab1024.sa.base.module.support.ai.domain.form.AIConfigQueryForm;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIConfigVO;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIModelConfigVO;
import net.lab1024.sa.base.module.support.ai.util.ApiKeyEncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI配置管理服务
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Service
public class AIConfigService {

    @Resource
    private AIConfigDao aiConfigDao;

    /**
     * 分页查询
     */
    public ResponseDTO<PageResult<AIConfigVO>> queryPage(AIConfigQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        List<AIConfigEntity> list = aiConfigDao.queryPage(page,
            queryForm.getProvider(),
            queryForm.getModelType(),
            queryForm.getEnabledFlag(),
            queryForm.getSearchWord());

        List<AIConfigVO> voList = list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        PageResult<AIConfigVO> pageResult = SmartPageUtil.convert2PageResult(page, voList);
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 查询详情
     */
    public ResponseDTO<AIConfigVO> getDetail(Long id) {
        AIConfigEntity entity = aiConfigDao.selectById(id);
        if (entity == null) {
            return ResponseDTO.userErrorParam("配置不存在");
        }

        AIConfigVO vo = convertToVO(entity);
        return ResponseDTO.ok(vo);
    }

    /**
     * 查询所有启用的模型配置 (用于下拉选择)
     */
    public ResponseDTO<List<AIModelConfigVO>> listEnabledModels() {
        List<AIConfigEntity> list = aiConfigDao.selectEnabledConfigs();

        List<AIModelConfigVO> voList = list.stream()
            .map(this::convertToModelConfigVO)
            .collect(Collectors.toList());

        return ResponseDTO.ok(voList);
    }

    /**
     * 获取启用的模型列表（返回List，不包装ResponseDTO）
     * 用于前端聊天页面获取可用模型
     */
    public List<AIConfigVO> getEnabledModels() {
        List<AIConfigEntity> list = aiConfigDao.selectEnabledConfigs();
        return list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 添加配置
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(AIConfigForm form) {
        // 校验 configKey 唯一性
        AIConfigEntity existEntity = aiConfigDao.selectByConfigKey(form.getConfigKey());
        if (existEntity != null) {
            return ResponseDTO.userErrorParam("配置Key已存在");
        }

        // 转换实体
        AIConfigEntity entity = SmartBeanUtil.copy(form, AIConfigEntity.class);

        // 加密 API Key
        if (StringUtils.isNotBlank(form.getApiKey())) {
            entity.setApiKey(ApiKeyEncryptUtil.encrypt(form.getApiKey()));
        }
        if (StringUtils.isNotBlank(form.getApiSecret())) {
            entity.setApiSecret(ApiKeyEncryptUtil.encrypt(form.getApiSecret()));
        }

        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeletedFlag(false);

        // 如果设置为默认，先清除其他默认标记
        if (Boolean.TRUE.equals(form.getDefaultFlag())) {
            aiConfigDao.clearAllDefaultFlags();
        }

        aiConfigDao.insert(entity);

        return ResponseDTO.ok();
    }

    /**
     * 更新配置
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update(AIConfigForm form) {
        if (form.getId() == null) {
            return ResponseDTO.userErrorParam("ID不能为空");
        }

        AIConfigEntity entity = aiConfigDao.selectById(form.getId());
        if (entity == null) {
            return ResponseDTO.userErrorParam("配置不存在");
        }

        // 校验 configKey 唯一性 (排除自己)
        AIConfigEntity existEntity = aiConfigDao.selectByConfigKey(form.getConfigKey());
        if (existEntity != null && !existEntity.getId().equals(form.getId())) {
            return ResponseDTO.userErrorParam("配置Key已存在");
        }

        // 更新实体
        SmartBeanUtil.copyProperties(form, entity);

        // 如果提供了新的 API Key，则加密
        if (StringUtils.isNotBlank(form.getApiKey())) {
            entity.setApiKey(ApiKeyEncryptUtil.encrypt(form.getApiKey()));
        }
        if (StringUtils.isNotBlank(form.getApiSecret())) {
            entity.setApiSecret(ApiKeyEncryptUtil.encrypt(form.getApiSecret()));
        }

        entity.setUpdateTime(LocalDateTime.now());

        // 如果设置为默认，先清除其他默认标记
        if (Boolean.TRUE.equals(form.getDefaultFlag())) {
            aiConfigDao.clearAllDefaultFlags();
        }

        aiConfigDao.updateById(entity);

        return ResponseDTO.ok();
    }

    /**
     * 删除配置
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(Long id) {
        AIConfigEntity entity = aiConfigDao.selectById(id);
        if (entity == null) {
            return ResponseDTO.userErrorParam("配置不存在");
        }

        // 如果是默认配置，不允许删除
        if (Boolean.TRUE.equals(entity.getDefaultFlag())) {
            return ResponseDTO.userErrorParam("默认配置不允许删除");
        }

        entity.setDeletedFlag(true);
        entity.setUpdateTime(LocalDateTime.now());
        aiConfigDao.updateById(entity);

        return ResponseDTO.ok();
    }

    /**
     * 设置默认模型
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> setDefault(Long id) {
        AIConfigEntity entity = aiConfigDao.selectById(id);
        if (entity == null) {
            return ResponseDTO.userErrorParam("配置不存在");
        }

        // 清除所有默认标记
        aiConfigDao.clearAllDefaultFlags();

        // 设置新的默认
        aiConfigDao.updateDefaultFlag(id, true);

        return ResponseDTO.ok();
    }

    /**
     * 转换为 VO
     */
    private AIConfigVO convertToVO(AIConfigEntity entity) {
        AIConfigVO vo = SmartBeanUtil.copy(entity, AIConfigVO.class);

        // 解密并脱敏 API Key
        if (StringUtils.isNotBlank(entity.getApiKey())) {
            try {
                String decryptedKey = ApiKeyEncryptUtil.decrypt(entity.getApiKey());
                vo.setApiKeyMasked(ApiKeyEncryptUtil.mask(decryptedKey));
                vo.setHasApiKey(true);
            } catch (Exception e) {
                log.warn("解密API Key失败: id={}", entity.getId());
                vo.setApiKeyMasked("****");
                vo.setHasApiKey(false);
            }
        } else {
            vo.setApiKeyMasked("");
            vo.setHasApiKey(false);
        }

        // 设置提供商显示名称
        AIProviderEnum provider = AIProviderEnum.getByValue(entity.getProvider());
        if (provider != null) {
            vo.setProviderDisplayName(provider.getDesc());
        }

        // 转换 BigDecimal 为 String (便于前端显示)
        if (entity.getTemperature() != null) {
            vo.setTemperature(entity.getTemperature().toString());
        }
        if (entity.getPriceInput() != null) {
            vo.setPriceInput(entity.getPriceInput().toString());
        }
        if (entity.getPriceOutput() != null) {
            vo.setPriceOutput(entity.getPriceOutput().toString());
        }

        return vo;
    }

    /**
     * 转换为 ModelConfigVO (简化版)
     */
    private AIModelConfigVO convertToModelConfigVO(AIConfigEntity entity) {
        AIModelConfigVO vo = SmartBeanUtil.copy(entity, AIModelConfigVO.class);

        // 设置提供商显示名称
        AIProviderEnum provider = AIProviderEnum.getByValue(entity.getProvider());
        if (provider != null) {
            vo.setProviderDisplayName(provider.getDesc());
        }

        return vo;
    }
}
