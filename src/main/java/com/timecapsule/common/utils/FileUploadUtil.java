package com.timecapsule.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.timecapsule.common.config.FileUploadConfig;
import com.timecapsule.common.exception.BusinessException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * 文件上传工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadUtil {

    private final FileUploadConfig fileUploadConfig;

    @PostConstruct
    public void init() {
        // 创建上传目录
        createDirectory(fileUploadConfig.getUploadPath());
        createDirectory(fileUploadConfig.getUploadPath() + fileUploadConfig.getAvatarPath());
        createDirectory(fileUploadConfig.getUploadPath() + fileUploadConfig.getAttachmentPath());
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file, String userId) {
        // 验证文件
        validateImageFile(file, fileUploadConfig.getMaxAvatarSize());

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String fileName = "avatar_" + userId + "_" + IdUtil.simpleUUID() + "." + suffix;

        // 按日期分目录
        String dateDir = DateUtil.format(new Date(), "yyyy/MM/dd");
        String relativePath = fileUploadConfig.getAvatarPath() + "/" + dateDir + "/" + fileName;
        String fullPath = fileUploadConfig.getUploadPath() + relativePath;

        // 保存文件
        try {
            FileUtil.writeBytes(file.getBytes(), fullPath);
            log.info("头像上传成功: {}", fullPath);

            // 返回访问路径
            return fileUploadConfig.getAccessPath() + relativePath;
        } catch (IOException e) {
            log.error("头像上传失败", e);
            throw new BusinessException("头像上传失败");
        }
    }

    /**
     * 上传文件
     */
    public FileUploadResult uploadFile(MultipartFile file, String userId, String type) {
        // 验证文件
        validateFile(file, fileUploadConfig.getMaxFileSize());

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String fileName = type + "_" + userId + "_" + IdUtil.simpleUUID() + "." + suffix;

        // 按日期分目录
        String dateDir = DateUtil.format(new Date(), "yyyy/MM/dd");
        String relativePath = fileUploadConfig.getAttachmentPath() + "/" + dateDir + "/" + fileName;
        String fullPath = fileUploadConfig.getUploadPath() + relativePath;

        // 保存文件
        try {
            FileUtil.writeBytes(file.getBytes(), fullPath);
            log.info("文件上传成功: {}", fullPath);

            // 返回结果
            FileUploadResult result = new FileUploadResult();
            result.setFileName(originalFilename);
            result.setFilePath(fileUploadConfig.getAccessPath() + relativePath);
            result.setFileSize(file.getSize());
            result.setFileType(suffix);

            return result;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return false;
        }

        // 转换为实际路径
        String realPath = filePath.replace(fileUploadConfig.getAccessPath(), fileUploadConfig.getUploadPath());

        try {
            return FileUtil.del(realPath);
        } catch (Exception e) {
            log.error("删除文件失败: {}", realPath, e);
            return false;
        }
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file, Long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 检查文件大小
        if (file.getSize() > maxSize * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过" + maxSize + "MB");
        }

        // 检查文件类型
        String suffix = FileUtil.getSuffix(file.getOriginalFilename()).toLowerCase();
        if (!Arrays.asList(fileUploadConfig.getAllowedImageTypes()).contains(suffix)) {
            throw new BusinessException("不支持的图片格式");
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file, Long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 检查文件大小
        if (file.getSize() > maxSize * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过" + maxSize + "MB");
        }

        // 检查文件类型
        String suffix = FileUtil.getSuffix(file.getOriginalFilename()).toLowerCase();
        if (!Arrays.asList(fileUploadConfig.getAllowedFileTypes()).contains(suffix)) {
            throw new BusinessException("不支持的文件格式");
        }
    }

    /**
     * 创建目录
     */
    private void createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
            log.info("创建目录: {}", path);
        }
    }

    /**
     * 文件上传结果
     */
    @Data
    public static class FileUploadResult {
        private String fileName;
        private String filePath;
        private Long fileSize;
        private String fileType;
    }
}