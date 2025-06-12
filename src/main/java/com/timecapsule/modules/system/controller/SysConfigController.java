// SysConfigController.java - 优化版
package com.timecapsule.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.timecapsule.common.result.PageResult;
import com.timecapsule.common.result.Result;
import com.timecapsule.modules.system.entity.SysConfig;
import com.timecapsule.modules.system.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器 - 优化版
 * 充分利用 MyBatis Plus 提供的功能
 */
@RestController
@RequestMapping("/api/v1/system/config")
@RequiredArgsConstructor
@Validated
@Tag(name = "系统配置", description = "系统配置管理接口")
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @GetMapping("/page")
    @Operation(summary = "分页查询系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResult<SysConfig> pageQuery(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) Integer status) {

        // 直接使用 MyBatis Plus 的分页和查询
        Page<SysConfig> page = sysConfigService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<SysConfig>()
                        .eq(module != null, SysConfig::getModule, module)
                        .like(configKey != null, SysConfig::getConfigKey, configKey)
                        .eq(status != null, SysConfig::getStatus, status)
                        .orderByAsc(SysConfig::getModule, SysConfig::getSortOrder)
        );

        return PageResult.success(
                page.getRecords(),
                page.getTotal(),
                pageNum,
                pageSize
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取配置详情")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SysConfig> getById(@PathVariable Long id) {
        // 直接使用 getById
        return Result.success(sysConfigService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> save(@Valid @RequestBody SysConfig config) {
        // 检查配置键是否已存在
        boolean exists = sysConfigService.lambdaQuery()
                .eq(SysConfig::getConfigKey, config.getConfigKey())
                .exists();
        if (exists) {
            return Result.fail("配置键已存在");
        }

        // 直接使用 save
        sysConfigService.save(config);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysConfig config) {
        config.setId(id);
        // 直接使用 updateById
        sysConfigService.updateById(config);
        sysConfigService.refreshCache();
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        // 直接使用 removeById
        sysConfigService.removeById(id);
        sysConfigService.refreshCache();
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        // 直接使用 removeByIds
        sysConfigService.removeByIds(ids);
        sysConfigService.refreshCache();
        return Result.success();
    }

    // 业务方法保持不变
    @GetMapping("/value/{configKey}")
    @Operation(summary = "根据配置键获取配置值")
    public Result<String> getConfigValue(@PathVariable String configKey) {
        return Result.success(sysConfigService.getConfigValue(configKey));
    }

    @GetMapping("/module/{module}")
    @Operation(summary = "根据模块获取配置")
    public Result<Map<String, String>> getConfigsByModule(@PathVariable String module) {
        return Result.success(sysConfigService.getConfigsByModule(module));
    }

    @GetMapping("/frontend")
    @Operation(summary = "获取前端可见配置")
    public Result<Map<String, String>> getFrontendConfigs() {
        return Result.success(sysConfigService.getFrontendConfigs());
    }

    @PostMapping("/refresh-cache")
    @Operation(summary = "刷新配置缓存")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> refreshCache() {
        sysConfigService.refreshCache();
        return Result.success();
    }
}