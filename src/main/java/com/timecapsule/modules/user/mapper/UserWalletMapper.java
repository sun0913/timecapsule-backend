package com.timecapsule.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.timecapsule.modules.user.entity.UserWallet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户钱包Mapper
 */
@Mapper
public interface UserWalletMapper extends BaseMapper<UserWallet> {
    // BaseMapper已经提供了所有基础CRUD功能
}