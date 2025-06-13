package com.timecapsule.modules.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timecapsule.common.exception.BusinessException;
import com.timecapsule.modules.user.entity.UserVerifyCode;
import com.timecapsule.modules.user.mapper.UserVerifyCodeMapper;
import com.timecapsule.modules.user.service.VerifyCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 验证码服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl extends ServiceImpl<UserVerifyCodeMapper, UserVerifyCode> implements VerifyCodeService {

    @Value("${app.verify-code.expire-minutes:5}")
    private Integer expireMinutes;

    @Value("${app.verify-code.daily-limit:10}")
    private Integer dailyLimit;

    @Override
    public void sendCode(String target, String type, String purpose, String userId) {
        // 1. 检查发送频率（1分钟内只能发送一次）
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        boolean recentlySent = this.lambdaQuery()
                .eq(UserVerifyCode::getTarget, target)
                .eq(UserVerifyCode::getPurpose, purpose)
                .ge(UserVerifyCode::getCreateTime, oneMinuteAgo)
                .exists();

        if (recentlySent) {
            throw new BusinessException("验证码发送过于频繁，请稍后再试");
        }

        // 2. 检查每日发送次数限制
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayCount = this.lambdaQuery()
                .eq(UserVerifyCode::getTarget, target)
                .ge(UserVerifyCode::getCreateTime, todayStart)
                .count();

        if (todayCount >= dailyLimit) {
            throw new BusinessException("今日验证码发送次数已达上限");
        }

        // 3. 生成验证码
        String code = generateCode(6);

        // 4. 保存验证码记录
        UserVerifyCode verifyCode = new UserVerifyCode();
        verifyCode.setTarget(target);
        verifyCode.setCode(code);
        verifyCode.setType(type);
        verifyCode.setPurpose(purpose);
        verifyCode.setUserId(userId);
        verifyCode.setExpireTime(LocalDateTime.now().plusMinutes(expireMinutes));
        verifyCode.setIsUsed(0);
        verifyCode.setIp("127.0.0.1"); // TODO: 获取真实IP

        this.save(verifyCode);

        // 5. 发送验证码
        if ("email".equals(type)) {
            sendEmailCode(target, code);
        } else if ("sms".equals(type)) {
            sendSmsCode(target, code);
        }

        log.info("验证码发送成功 - 目标: {}, 类型: {}, 用途: {}", target, type, purpose);
    }

    @Override
    public boolean verifyCode(String target, String code, String purpose) {
        // 查找有效的验证码
        UserVerifyCode verifyCode = this.lambdaQuery()
                .eq(UserVerifyCode::getTarget, target)
                .eq(UserVerifyCode::getCode, code)
                .eq(UserVerifyCode::getPurpose, purpose)
                .eq(UserVerifyCode::getIsUsed, 0)
                .gt(UserVerifyCode::getExpireTime, LocalDateTime.now())
                .orderByDesc(UserVerifyCode::getCreateTime)
                .last("limit 1")
                .one();

        if (verifyCode == null) {
            return false;
        }

        // 标记为已使用
        verifyCode.setIsUsed(1);
        verifyCode.setUsedTime(LocalDateTime.now());
        this.updateById(verifyCode);

        return true;
    }

    @Override
    public String generateCode(int length) {
        return RandomUtil.randomNumbers(length);
    }

    /**
     * 发送邮箱验证码
     */
    private void sendEmailCode(String email, String code) {
        // TODO: 实现邮件发送逻辑
        log.info("【时光信笺】您的验证码是: {}，{}分钟内有效。", code, expireMinutes);
    }

    /**
     * 发送短信验证码
     */
    private void sendSmsCode(String phone, String code) {
        // TODO: 实现短信发送逻辑
        log.info("【时光信笺】您的验证码是: {}，{}分钟内有效。", code, expireMinutes);
    }
}