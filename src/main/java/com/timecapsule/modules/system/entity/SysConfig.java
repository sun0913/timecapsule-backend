package com.timecapsule.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.timecapsule.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tc_sys_config")
public class SysConfig extends BaseEntity {

    /**
     * 模块名称（system/user/letter/nft/token）
     */
    private String module;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置类型（string/number/boolean/json）
     */
    private String configType;

    /**
     * 配置说明
     */
    private String configDesc;

    /**
     * 配置选项（JSON格式）
     */
    private String configOptions;

    /**
     * 是否前端可见（0-否，1-是）
     */
    private Integer isFrontend;

    /**
     * 是否公开配置（0-否，1-是）
     */
    private Integer isPublic;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;
}