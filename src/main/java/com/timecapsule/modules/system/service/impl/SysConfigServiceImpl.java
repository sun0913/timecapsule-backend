package com.timecapsule.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.timecapsule.modules.system.entity.SysConfig;
import com.timecapsule.modules.system.mapper.SysConfigMapper;
import com.timecapsule.modules.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private static final String CACHE_KEY = "sysConfig";

    @Override
    @Cacheable(value = CACHE_KEY, key = "#configKey")
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    @Cacheable(value = CACHE_KEY, key = "#configKey")
    public String getConfigValue(String configKey, String defaultValue) {
        // 使用 lambdaQuery
        SysConfig config = this.lambdaQuery()
                .eq(SysConfig::getConfigKey, configKey)
                .eq(SysConfig::getStatus, 1)
                .one();

        return config != null ? config.getConfigValue() : defaultValue;
    }

    @Override
    @Cacheable(value = CACHE_KEY, key = "#module")
    public Map<String, String> getConfigsByModule(String module) {
        // 使用 lambdaQuery 和 stream
        return this.lambdaQuery()
                .eq(SysConfig::getModule, module)
                .eq(SysConfig::getStatus, 1)
                .orderByAsc(SysConfig::getSortOrder)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        SysConfig::getConfigKey,
                        SysConfig::getConfigValue
                ));
    }

    @Override
    @Cacheable(value = CACHE_KEY, key = "'frontend'")
    public Map<String, String> getFrontendConfigs() {
        return this.lambdaQuery()
                .eq(SysConfig::getIsFrontend, 1)
                .eq(SysConfig::getStatus, 1)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        SysConfig::getConfigKey,
                        SysConfig::getConfigValue
                ));
    }

    @Override
    @CacheEvict(value = CACHE_KEY, allEntries = true)
    public void refreshCache() {
        log.info("系统配置缓存已刷新");
    }
}