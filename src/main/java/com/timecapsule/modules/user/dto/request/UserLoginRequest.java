package com.timecapsule.modules.user.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest {

    @NotBlank(message = "登录账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String captcha;

    private String captchaId;
}