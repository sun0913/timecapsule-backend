package com.timecapsule.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * 文件上传配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.file")
public class FileUploadConfig {

    /**
     * 文件上传根路径
     */
    private String uploadPath = "./uploads";

    /**
     * 头像上传路径
     */
    private String avatarPath = "/avatar";

    /**
     * 信件附件路径
     */
    private String attachmentPath = "/attachment";

    /**
     * 允许的图片格式
     */
    private String[] allowedImageTypes = {"jpg", "jpeg", "png", "gif", "webp"};

    /**
     * 允许的文件格式
     */
    private String[] allowedFileTypes = {"jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx"};

    /**
     * 最大文件大小（MB）
     */
    private Long maxFileSize = 10L;

    /**
     * 最大头像大小（MB）
     */
    private Long maxAvatarSize = 5L;

    /**
     * 访问路径前缀
     */
    private String accessPath = "/upload";
}