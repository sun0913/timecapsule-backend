package com.timecapsule.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.timecapsule.modules.system.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置Mapper
 * 继承 BaseMapper 后，所有基础 CRUD 都不需要写
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
    // 不需要任何额外方法，BaseMapper 已经够用
}