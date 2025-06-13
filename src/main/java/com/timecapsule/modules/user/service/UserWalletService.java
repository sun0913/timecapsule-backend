package com.timecapsule.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timecapsule.modules.user.dto.request.WalletBindRequest;
import com.timecapsule.modules.user.entity.UserWallet;
import com.timecapsule.modules.user.vo.UserWalletVO;

/**
 * 用户钱包服务接口
 */
public interface UserWalletService extends IService<UserWallet> {

    /**
     * 绑定钱包
     */
    UserWalletVO bindWallet(String userId, WalletBindRequest request);

    /**
     * 解绑钱包
     */
    void unbindWallet(String userId);

    /**
     * 获取用户钱包信息
     */
    UserWalletVO getUserWallet(String userId);

    /**
     * 验证钱包签名
     */
    boolean verifyWalletSignature(String walletAddress, String message, String signature);
}