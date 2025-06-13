package com.timecapsule.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timecapsule.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户钱包实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tc_user_wallet")
public class UserWallet extends BaseEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 钱包地址
     */
    private String walletAddress;

    /**
     * 链ID（137-Polygon）
     */
    private Integer chainId;

    /**
     * 钱包类型：metamask/walletconnect
     */
    private String walletType;

    /**
     * 是否主钱包：0-否，1-是
     */
    private Integer isPrimary;

    /**
     * 绑定时间
     */
    private LocalDateTime bindTime;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUseTime;

    /**
     * 状态：0-已解绑，1-正常
     */
    private Integer status;
}