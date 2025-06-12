package com.timecapsule.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    FAILED(400, "操作失败"),
    VALIDATE_FAILED(400, "参数验证失败"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    LOCKED(423, "请求失败，请稍后重试"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    // 服务端错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误 1xxx
    // 用户相关 10xx
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户已存在"),
    USERNAME_ALREADY_EXIST(1003, "用户名已存在"),
    EMAIL_ALREADY_EXIST(1004, "邮箱已被使用"),
    PHONE_ALREADY_EXIST(1005, "手机号已被使用"),
    PASSWORD_ERROR(1006, "密码错误"),
    ACCOUNT_DISABLED(1007, "账号已被禁用"),
    ACCOUNT_LOCKED(1008, "账号已被锁定"),
    ACCOUNT_EXPIRED(1009, "账号已过期"),
    OLD_PASSWORD_ERROR(1010, "原密码错误"),

    // 认证相关 11xx
    TOKEN_INVALID(1101, "Token无效"),
    TOKEN_EXPIRED(1102, "Token已过期"),
    TOKEN_CREATE_ERROR(1103, "Token生成失败"),
    REFRESH_TOKEN_EXPIRED(1104, "刷新Token已过期"),

    // 验证码相关 12xx
    CAPTCHA_ERROR(1201, "验证码错误"),
    CAPTCHA_EXPIRED(1202, "验证码已过期"),
    CAPTCHA_FREQUENT(1203, "验证码发送过于频繁"),

    // 权限相关 13xx
    NO_PERMISSION(1301, "无访问权限"),
    ROLE_NOT_EXIST(1302, "角色不存在"),
    PERMISSION_NOT_EXIST(1303, "权限不存在"),

    // 参数验证 14xx
    PARAM_MISSING(1401, "缺少必要参数"),
    PARAM_TYPE_ERROR(1402, "参数类型错误"),
    PARAM_VALUE_ERROR(1403, "参数值错误"),
    PARAM_FORMAT_ERROR(1404, "参数格式错误"),

    // 业务操作 15xx
    OPERATION_FAILED(1501, "操作失败"),
    DATA_NOT_EXIST(1502, "数据不存在"),
    DATA_ALREADY_EXIST(1503, "数据已存在"),
    DATA_CONSTRAINT_ERROR(1504, "数据约束错误"),

    // 文件相关 16xx
    FILE_NOT_EXIST(1601, "文件不存在"),
    FILE_UPLOAD_FAILED(1602, "文件上传失败"),
    FILE_TYPE_ERROR(1603, "文件类型错误"),
    FILE_SIZE_EXCEED(1604, "文件大小超出限制"),

    // 第三方服务 17xx
    THIRD_PARTY_ERROR(1701, "第三方服务异常"),
    SMS_SEND_FAILED(1702, "短信发送失败"),
    EMAIL_SEND_FAILED(1703, "邮件发送失败");

    private final Integer code;
    private final String message;
}