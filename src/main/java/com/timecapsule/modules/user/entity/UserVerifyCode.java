package com.timecapsule.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timecapsule.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户验证码实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tc_user_verify_code")
public class UserVerifyCode extends BaseEntity {

    /**
     * 目标（邮箱或手机号）
     */
    private String target;

    /**
     * 验证码
     */
    private String code;

    /**
     * 类型：email/sms
     */
    private String type;

    /**
     * 用途：register/reset/bind
     */
    private String purpose;

    /**
     * 用户ID（已登录用户）
     */
    private String userId;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否已使用：0-否，1-是
     */
    private Integer isUsed;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * 请求IP
     */
    private String ip;
}