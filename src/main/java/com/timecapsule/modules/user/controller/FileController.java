package com.timecapsule.modules.user.controller;

import com.timecapsule.common.result.Result;
import com.timecapsule.common.utils.FileUploadUtil;
import com.timecapsule.modules.user.entity.User;
import com.timecapsule.modules.user.service.UserService;
import com.timecapsule.modules.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "文件上传相关接口")
public class FileController {

    private final FileUploadUtil fileUploadUtil;
    private final UserService userService;

    @PostMapping("/avatar")
    @Operation(summary = "上传头像")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // 获取当前用户
        UserVO currentUser = userService.getCurrentUser();

        // 上传头像
        String avatarUrl = fileUploadUtil.uploadAvatar(file, currentUser.getUserId());

        // 更新用户头像
        userService.lambdaUpdate()
                .eq(User::getUserId, currentUser.getUserId())
                .set(User::getAvatar, avatarUrl)
                .update();

        return Result.success(avatarUrl);
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<FileUploadUtil.FileUploadResult> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "attachment") String type) {

        // 获取当前用户
        UserVO currentUser = userService.getCurrentUser();

        // 上传文件
        FileUploadUtil.FileUploadResult result = fileUploadUtil.uploadFile(file, currentUser.getUserId(), type);

        return Result.success(result);
    }

    @DeleteMapping
    @Operation(summary = "删除文件")
    public Result<Void> deleteFile(@RequestParam String filePath) {
        boolean success = fileUploadUtil.deleteFile(filePath);
        return success ? Result.success() : Result.fail("删除失败");
    }
}