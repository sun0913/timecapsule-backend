package com.timecapsule.modules.user.vo;

import lombok.Data;

/**
 * 用户登录响应VO
 */
@Data
public class UserLoginVO {

    private String userId;

    private String username;

    private String nickname;

    private String avatar;

    private String token;

    private String refreshToken;

    private Long expiresIn;

    private Boolean isNewUser = false;

    private Boolean needBindWallet = true;
}