package com.timecapsule.modules.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
public class UserVO {

    private Long id;

    private String userId;

    private String username;

    private String nickname;

    private String avatar;

    private String email;

    private Boolean emailVerified;

    private String phone;

    private Boolean phoneVerified;

    private Integer gender;

    private String birthday;

    private String bio;

    private Integer level;

    private Integer exp;

    private Integer reputation;

    private LocalDateTime createTime;

    private LocalDateTime lastLoginTime;
}