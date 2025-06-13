package com.timecapsule.modules.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户钱包信息VO
 */
@Data
public class UserWalletVO {

    private String walletAddress;

    private Integer chainId;

    private LocalDateTime bindTime;

    private String balances; // 余额信息（后续实现）

    private Boolean isPrimary;
}