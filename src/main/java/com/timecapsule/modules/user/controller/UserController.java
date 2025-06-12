package com.timecapsule.modules.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.timecapsule.common.result.PageResult;
import com.timecapsule.common.result.Result;
import com.timecapsule.modules.user.dto.request.*;
import com.timecapsule.modules.user.entity.User;
import com.timecapsule.modules.user.service.UserService;
import com.timecapsule.modules.user.vo.UserLoginVO;
import com.timecapsule.modules.user.vo.UserStatsVO;
import com.timecapsule.modules.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户控制器 - 优化版
 *
 * @author 时光信笺
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    // ========== 认证相关接口 ==========

    @PostMapping("/auth/register")
    @Operation(summary = "用户注册")
    public Result<UserLoginVO> register(@Valid @RequestBody UserRegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @PostMapping("/auth/login")
    @Operation(summary = "用户登录")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginRequest request) {
        return Result.success(userService.login(request));
    }

    @PostMapping("/auth/refresh")
    @Operation(summary = "刷新Token")
    public Result<UserLoginVO> refreshToken(@NotBlank(message = "刷新Token不能为空") @RequestParam String refreshToken) {
        return Result.success(userService.refreshToken(refreshToken));
    }

    @PostMapping("/auth/logout")
    @Operation(summary = "退出登录")
    public Result<Void> logout() {
        // JWT无状态，前端清除Token即可
        return Result.success();
    }

    // ========== 用户信息接口（需要认证） ==========

    @GetMapping("/user/profile")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取指定用户信息")
    public Result<User> getUserById(@PathVariable String userId) {
        // 使用 MyBatis Plus 的 lambdaQuery
        User user = userService.lambdaQuery()
                .eq(User::getUserId, userId)
                .select(User::getUserId, User::getUsername, User::getNickname,
                        User::getAvatar, User::getBio, User::getLevel,
                        User::getReputation, User::getCreateTime)  // 只查询公开字段
                .one();

        if (user == null) {
            return Result.fail("用户不存在");
        }

        return Result.success(user);
    }

    @PutMapping("/user/profile")
    @Operation(summary = "更新当前用户信息")
    public Result<Void> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        UserVO currentUser = userService.getCurrentUser();

        // 构建更新对象
        User updateUser = new User();
        updateUser.setId(currentUser.getId());  // 需要在 UserVO 中添加 id 字段

        // 设置需要更新的字段
        if (request.getNickname() != null) {
            updateUser.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            updateUser.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            updateUser.setBio(request.getBio());
        }
        if (request.getGender() != null) {
            updateUser.setGender(request.getGender());
        }

        // 使用 MyBatis Plus 的 updateById
        userService.updateById(updateUser);

        return Result.success();
    }

    @PostMapping("/user/password/change")
    @Operation(summary = "修改密码")
    public Result<Void> changePassword(
            @NotBlank(message = "原密码不能为空") @RequestParam String oldPassword,
            @NotBlank(message = "新密码不能为空") @RequestParam String newPassword) {
        userService.changePassword(oldPassword, newPassword);
        return Result.success();
    }

    // ========== 用户管理接口（管理员权限） ==========

    @GetMapping("/admin/users")
    @Operation(summary = "分页查询用户列表")
    // @PreAuthorize("hasRole('ADMIN')")
    public PageResult<User> pageUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 使用 MyBatis Plus 的分页查询
        Page<User> page = userService.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<User>()
                        .and(keyword != null, wrapper -> wrapper
                                .like(User::getUsername, keyword)
                                .or()
                                .like(User::getNickname, keyword)
                                .or()
                                .like(User::getEmail, keyword)
                        )
                        .eq(status != null, User::getStatus, status)
                        .ge(startDate != null, User::getCreateTime, startDate)
                        .le(endDate != null, User::getCreateTime, endDate)
                        .orderByDesc(User::getCreateTime)
        );

        return PageResult.success(
                page.getRecords(),
                page.getTotal(),
                pageNum,
                pageSize
        );
    }

    @PutMapping("/admin/users/{id}/status")
    @Operation(summary = "启用/禁用用户")
    // @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {

        // 使用 lambdaUpdate
        userService.lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getStatus, status)
                .update();

        return Result.success();
    }

    @GetMapping("/admin/users/count")
    @Operation(summary = "获取用户统计信息")
    // @PreAuthorize("hasRole('ADMIN')")
    public Result<UserStatsVO> getUserStats() {
        UserStatsVO stats = new UserStatsVO();

        // 使用 MyBatis Plus 的 count 方法
        stats.setTotalUsers(userService.count());
        stats.setActiveUsers(userService.count(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 1)
        ));
        stats.setTodayNewUsers(userService.count(
                new LambdaQueryWrapper<User>()
                        .ge(User::getCreateTime, LocalDate.now())
        ));

        return Result.success(stats);
    }

    @DeleteMapping("/admin/users/{id}")
    @Operation(summary = "删除用户（逻辑删除）")
    // @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        // MyBatis Plus 会自动处理逻辑删除
        userService.removeById(id);
        return Result.success();
    }

    @PostMapping("/admin/users/batch-delete")
    @Operation(summary = "批量删除用户")
    // @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> ids) {
        // 批量删除
        userService.removeByIds(ids);
        return Result.success();
    }

    // ========== 其他功能接口 ==========

    @GetMapping("/user/check-username")
    @Operation(summary = "检查用户名是否存在")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        // 使用 exists 判断
        boolean exists = userService.lambdaQuery()
                .eq(User::getUsername, username)
                .exists();
        return Result.success(exists);
    }

    @GetMapping("/user/check-email")
    @Operation(summary = "检查邮箱是否存在")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.lambdaQuery()
                .eq(User::getEmail, email)
                .exists();
        return Result.success(exists);
    }

    @PostMapping("/user/batch-update-level")
    @Operation(summary = "批量更新用户等级")
    // @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchUpdateLevel(@Valid @RequestBody BatchUpdateLevelRequest request) {
        // 批量更新
        userService.lambdaUpdate()
                .in(User::getId, request.getUserIds())
                .set(User::getLevel, request.getLevel())
                .update();

        return Result.success();
    }
}