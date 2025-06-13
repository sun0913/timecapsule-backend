package com.timecapsule.modules.user.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 钱包绑定请求
 */
@Data
public class WalletBindRequest {

    @NotBlank(message = "钱包地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "钱包地址格式不正确")
    private String walletAddress;

    @NotBlank(message = "签名不能为空")
    private String signature;

    @NotBlank(message = "签名消息不能为空")
    private String message;

    @NotNull(message = "链ID不能为空")
    private Integer chainId = 137; // 默认Polygon
}