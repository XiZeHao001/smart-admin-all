package net.lab1024.sa.base.module.support.ai.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * API Key 加密工具类
 * 使用 Spring Security Crypto 进行加密
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
public class ApiKeyEncryptUtil {

    /**
     * 加密密钥 (生产环境建议从配置文件读取)
     */
    private static final String SECRET_KEY = "SmartAdmin-1024Lab-AI-Secret-Key-2025";

    /**
     * 盐值 (必须是 Hex 编码的字符串，且字符数必须是偶数)
     * 这里使用 16 字节的 Hex 编码盐值 (32个字符)
     */
    private static final String SALT = "deadbeef1024cafe5678abcd9876fedc";

    private static final TextEncryptor encryptor = Encryptors.text(SECRET_KEY, SALT);

    /**
     * 加密
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return plainText;
        }
        try {
            return encryptor.encrypt(plainText);
        } catch (Exception e) {
            log.error("API Key加密失败", e);
            throw new RuntimeException("API Key加密失败", e);
        }
    }

    /**
     * 解密
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.trim().isEmpty()) {
            return encryptedText;
        }
        try {
            return encryptor.decrypt(encryptedText);
        } catch (Exception e) {
            log.error("API Key解密失败", e);
            throw new RuntimeException("API Key解密失败", e);
        }
    }

    /**
     * 脱敏显示 (只显示前4位和后4位)
     */
    public static String mask(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
