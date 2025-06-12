package com.timecapsule.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.timecapsule.modules.system.entity.SysConfig;
import java.util.Map;

public interface SysConfigService extends IService<SysConfig> {

    // 只定义业务方法
    String getConfigValue(String configKey);

    String getConfigValue(String configKey, String defaultValue);

    Map<String, String> getConfigsByModule(String module);

    Map<String, String> getFrontendConfigs();

    void refreshCache();
}