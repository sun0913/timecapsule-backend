package com.timecapsule.modules.user.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Size;

/**
 * 用户信息更新请求
 */
@Data
public class UserUpdateRequest {

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    private String avatar;

    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;

    private String email;

    private String phone;

    private Integer gender;

    private String birthday;
}