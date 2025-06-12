package com.timecapsule.modules.user.vo;

import lombok.Data;

/**
 * 用户统计VO
 */
@Data
public class UserStatsVO {
    private Long totalUsers;
    private Long activeUsers;
    private Long todayNewUsers;
    private Long monthNewUsers;
    private Double activeRate;
}