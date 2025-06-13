package com.timecapsule.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timecapsule.modules.user.entity.UserVerifyCode;

/**
 * 验证码服务接口
 */
public interface VerifyCodeService extends IService<UserVerifyCode> {

    /**
     * 发送验证码
     * @param target 目标（邮箱或手机号）
     * @param type 类型：email/sms
     * @param purpose 用途：register/reset/bind
     * @param userId 用户ID（可选）
     */
    void sendCode(String target, String type, String purpose, String userId);

    /**
     * 验证验证码
     * @param target 目标
     * @param code 验证码
     * @param purpose 用途
     * @return 是否验证成功
     */
    boolean verifyCode(String target, String code, String purpose);

    /**
     * 生成验证码
     * @param length 长度
     * @return 验证码
     */
    String generateCode(int length);
}