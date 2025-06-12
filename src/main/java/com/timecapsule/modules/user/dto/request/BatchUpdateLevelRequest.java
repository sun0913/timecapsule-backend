package com.timecapsule.modules.user.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量更新等级请求
 */
@Data
public class BatchUpdateLevelRequest {

    @NotEmpty(message = "用户ID列表不能为空")
    private List<Long> userIds;

    @NotNull(message = "等级不能为空")
    private Integer level;
}