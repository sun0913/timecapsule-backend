package com.timecapsule.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.timecapsule.modules.user.entity.UserVerifyCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验证码Mapper
 */
@Mapper
public interface UserVerifyCodeMapper extends BaseMapper<UserVerifyCode> {
}