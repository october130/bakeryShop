package com.cake.platform.common.oss;

import com.aliyun.oss.OSS;
import com.cake.platform.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Component
public class OssService {

    @Resource
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    /** 限制 5MB */
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    /** 只允许传图片 */
    private static final Set<String> ALLOW_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp");

    /**
     * 上传图片到 OSS，返回公网访问地址（bucket 需开启公共读）
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOW_TYPES.contains(contentType)) {
            throw new BusinessException("仅支持 jpg/png/gif/webp/bmp 图片");
        }

        // 取原文件后缀
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        // 按日期分目录 + UUID 文件名，避免重名/覆盖
        String key = "images/" + LocalDate.now() + "/" + UUID.randomUUID() + ext;

        try {
            ossClient.putObject(bucketName, key, file.getInputStream());
        } catch (IOException e) {
            throw new BusinessException("上传失败: " + e.getMessage());
        }

        // 拼接公网访问地址（去掉 endpoint 里可能带的 http:// 前缀）
        String domain = endpoint.replaceFirst("^https?://", "");
        return "https://" + bucketName + "." + domain + "/" + key;
    }
}
