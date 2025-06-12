package com.timecapsule.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.timecapsule.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tc_user")
public class User extends BaseEntity {

    /**
     * 用户唯一标识
     */
    private String userId;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱是否验证（0-未验证，1-已验证）
     */
    private Integer emailVerified;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 手机号是否验证（0-未验证，1-已验证）
     */
    private Integer phoneVerified;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 用户等级
     */
    private Integer level;

    /**
     * 经验值
     */
    private Integer exp;

    /**
     * 信誉值
     */
    private Integer reputation;

    /**
     * 状态（0-禁用，1-正常，2-注销）
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 注册IP
     */
    private String registerIp;

    /**
     * 注册来源（web/mobile/admin）
     */
    private String registerSource;

    /**
     * 是否在线（不存储数据库）
     */
    @TableField(exist = false)
    private Boolean online;
}