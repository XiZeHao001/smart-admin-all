package net.lab1024.sa.admin.util;

import net.lab1024.sa.admin.module.system.fieldpermission.constant.FieldMaskTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * 字段脱敏工具类
 * 提供各种敏感数据的脱敏处理
 *
 * @Author xzh
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
public class FieldMaskUtil {

    private static final String MASK_CHAR = "*";

    /**
     * 根据脱敏类型进行脱敏
     *
     * @param value    原始值
     * @param maskType 脱敏类型
     * @return 脱敏后的值
     */
    public static String mask(String value, FieldMaskTypeEnum maskType) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        if (maskType == null) {
            return value;
        }

        return switch (maskType) {
            case MOBILE -> maskMobile(value);
            case ID_CARD -> maskIdCard(value);
            case BANK_CARD -> maskBankCard(value);
            case EMAIL -> maskEmail(value);
            case NAME -> maskName(value);
            case ADDRESS -> maskAddress(value);
            case CUSTOM -> maskCustom(value, 3, 4);  // 默认保留前3后4
            default -> value;
        };
    }

    /**
     * 手机号脱敏：138****5678
     * 保留前3位和后4位
     */
    public static String maskMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return mobile;
        }
        int length = mobile.length();
        if (length < 7) {
            return mobile;  // 长度不够，不脱敏
        }
        return mobile.substring(0, 3) + MASK_CHAR.repeat(4) + mobile.substring(length - 4);
    }

    /**
     * 身份证号脱敏：110101****1234
     * 保留前6位和后4位
     */
    public static String maskIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return idCard;
        }
        int length = idCard.length();
        if (length < 10) {
            return maskCustom(idCard, 2, 2);
        }
        // 15位或18位身份证号
        return idCard.substring(0, 6) + MASK_CHAR.repeat(length - 10) + idCard.substring(length - 4);
    }

    /**
     * 银行卡号脱敏：6217 **** **** 1234
     * 保留前4位和后4位，中间用空格分隔
     */
    public static String maskBankCard(String bankCard) {
        if (StringUtils.isBlank(bankCard)) {
            return bankCard;
        }
        // 移除空格
        String card = bankCard.replaceAll("\\s+", "");
        int length = card.length();
        if (length < 8) {
            return bankCard;
        }
        String front = card.substring(0, 4);
        String end = card.substring(length - 4);
        int middleLength = length - 8;
        int stars = Math.min(middleLength, 12);  // 最多显示12个星号
        return front + " " + MASK_CHAR.repeat(stars) + " " + end;
    }

    /**
     * 邮箱脱敏：abc***@example.com
     * 保留@前的前3个字符和@后的全部内容
     */
    public static String maskEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 0) {
            return email;  // 不是合法邮箱，不脱敏
        }
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (localPart.length() <= 3) {
            return localPart.charAt(0) + MASK_CHAR.repeat(localPart.length() - 1) + domainPart;
        }
        return localPart.substring(0, 3) + MASK_CHAR.repeat(Math.min(localPart.length() - 3, 3)) + domainPart;
    }

    /**
     * 姓名脱敏：张*，欧阳**
     * 保留姓氏，名字用*代替
     */
    public static String maskName(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        int length = name.length();
        if (length == 1) {
            return name;  // 单字名，不脱敏
        }
        if (length == 2) {
            return name.charAt(0) + MASK_CHAR;
        }
        // 复姓或长名：保留前1个字（或前2个字如果是复姓），其余用*
        // 简单处理：保留第一个字
        return name.charAt(0) + MASK_CHAR.repeat(length - 1);
    }

    /**
     * 地址脱敏：北京市朝阳区******
     * 保留前面的省市，详细地址用*代替
     */
    public static String maskAddress(String address) {
        if (StringUtils.isBlank(address)) {
            return address;
        }
        int length = address.length();
        if (length <= 6) {
            return maskCustom(address, 2, 0);
        }
        // 保留前6个字（通常是省市区），后面用*
        int keepLength = Math.min(6, length - 4);
        return address.substring(0, keepLength) + MASK_CHAR.repeat(Math.min(length - keepLength, 6));
    }

    /**
     * 自定义脱敏
     *
     * @param value      原始值
     * @param frontKeep  保留前几位
     * @param backKeep   保留后几位
     * @return 脱敏后的值
     */
    public static String maskCustom(String value, int frontKeep, int backKeep) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        int length = value.length();
        if (length <= frontKeep + backKeep) {
            return value;  // 长度不够，不脱敏
        }
        String front = length >= frontKeep ? value.substring(0, frontKeep) : "";
        String back = length >= backKeep && backKeep > 0 ? value.substring(length - backKeep) : "";
        int maskLength = length - frontKeep - backKeep;
        return front + MASK_CHAR.repeat(Math.min(maskLength, 8)) + back;
    }

    /**
     * 判断字符串是否已经被脱敏
     *
     * @param value 待判断的字符串
     * @return true=已脱敏，false=未脱敏
     */
    public static boolean isMasked(String value) {
        return StringUtils.isNotBlank(value) && value.contains(MASK_CHAR);
    }
}

