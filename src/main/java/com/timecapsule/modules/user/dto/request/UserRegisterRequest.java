package com.timecapsule.modules.user.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

/**
 * 用户注册请求
 */
@Data
public class UserRegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String inviteCode;

    @NotBlank(message = "注册平台不能为空")
    @Pattern(regexp = "^(web|mobile|admin)$", message = "注册平台只能是web、mobile或admin")
    private String platform = "web";
}