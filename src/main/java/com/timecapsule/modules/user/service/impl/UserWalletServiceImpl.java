package com.timecapsule.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timecapsule.common.exception.BusinessException;
import com.timecapsule.common.result.ResultCode;
import com.timecapsule.modules.user.dto.request.WalletBindRequest;
import com.timecapsule.modules.user.entity.UserWallet;
import com.timecapsule.modules.user.mapper.UserWalletMapper;
import com.timecapsule.modules.user.service.UserWalletService;
import com.timecapsule.modules.user.vo.UserWalletVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 用户钱包服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements UserWalletService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserWalletVO bindWallet(String userId, WalletBindRequest request) {
        // 1. 验证签名
        if (!verifyWalletSignature(request.getWalletAddress(), request.getMessage(), request.getSignature())) {
            throw new BusinessException("钱包签名验证失败");
        }

        // 2. 检查钱包是否已被其他用户绑定
        boolean walletExists = this.lambdaQuery()
                .eq(UserWallet::getWalletAddress, request.getWalletAddress())
                .eq(UserWallet::getStatus, 1)
                .exists();
        if (walletExists) {
            throw new BusinessException("该钱包地址已被其他用户绑定");
        }

        // 3. 检查用户是否已有绑定的钱包
        UserWallet existingWallet = this.lambdaQuery()
                .eq(UserWallet::getUserId, userId)
                .eq(UserWallet::getStatus, 1)
                .one();

        if (existingWallet != null) {
            // 如果已有钱包，将旧钱包设为非主钱包
            this.lambdaUpdate()
                    .eq(UserWallet::getUserId, userId)
                    .set(UserWallet::getIsPrimary, 0)
                    .update();
        }

        // 4. 创建新的钱包绑定记录
        UserWallet wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.setWalletAddress(request.getWalletAddress().toLowerCase());
        wallet.setChainId(request.getChainId());
        wallet.setWalletType("metamask"); // 默认MetaMask
        wallet.setIsPrimary(1);
        wallet.setBindTime(LocalDateTime.now());
        wallet.setStatus(1);

        this.save(wallet);

        // 5. 返回钱包信息
        UserWalletVO vo = new UserWalletVO();
        BeanUtil.copyProperties(wallet, vo);

        // TODO: 后续实现查询钱包余额功能
        // vo.setBalances(getWalletBalances(request.getWalletAddress()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindWallet(String userId) {
        // 将用户所有钱包状态设为已解绑
        boolean updated = this.lambdaUpdate()
                .eq(UserWallet::getUserId, userId)
                .eq(UserWallet::getStatus, 1)
                .set(UserWallet::getStatus, 0)
                .update();

        if (!updated) {
            throw new BusinessException("未找到绑定的钱包");
        }

        log.info("用户 {} 解绑钱包成功", userId);
    }

    @Override
    public UserWalletVO getUserWallet(String userId) {
        // 获取用户的主钱包
        UserWallet wallet = this.lambdaQuery()
                .eq(UserWallet::getUserId, userId)
                .eq(UserWallet::getStatus, 1)
                .eq(UserWallet::getIsPrimary, 1)
                .one();

        if (wallet == null) {
            return null;
        }

        UserWalletVO vo = new UserWalletVO();
        BeanUtil.copyProperties(wallet, vo);

        // TODO: 查询钱包余额
        // vo.setBalances(getWalletBalances(wallet.getWalletAddress()));

        return vo;
    }

    @Override
    public boolean verifyWalletSignature(String walletAddress, String message, String signature) {
        try {
            // 1. 对消息进行哈希处理
            String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
            String prefixedMessage = prefix + message;
            byte[] messageHash = Hash.sha3(prefixedMessage.getBytes(StandardCharsets.UTF_8));

            // 2. 解析签名
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            if (signatureBytes.length != 65) {
                log.error("签名长度不正确: {}", signatureBytes.length);
                return false;
            }

            // 3. 提取 r, s, v
            byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
            byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);
            byte v = signatureBytes[64];

            // 4. 调整 v 值（如果需要）
            if (v < 27) {
                v += 27;
            }

            // 5. 创建签名数据
            Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);

            // 6. 从签名恢复公钥
            BigInteger publicKey = Sign.signedMessageHashToKey(messageHash, signatureData);
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            // 7. 比较地址（忽略大小写）
            boolean isValid = recoveredAddress.equalsIgnoreCase(walletAddress);

            if (!isValid) {
                log.error("签名验证失败 - 期望地址: {}, 恢复地址: {}", walletAddress, recoveredAddress);
            }

            return isValid;

        } catch (Exception e) {
            log.error("验证钱包签名异常", e);
            return false;
        }
    }
}