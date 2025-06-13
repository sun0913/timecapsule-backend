package com.timecapsule.modules.user.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求
 */
@Data
public class ChangePasswordRequest {

    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    private String newPassword;
}