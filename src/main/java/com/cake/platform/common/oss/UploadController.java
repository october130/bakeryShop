package com.cake.platform.common.oss;

import com.cake.platform.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@Slf4j
@Tag(name = "文件上传")
public class UploadController {

    @Resource
    private OssService ossService;

    @PostMapping
    @Operation(summary = "上传图片，返回图片URL")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("上传图片: {}", file.getOriginalFilename());
        String url = ossService.upload(file);
        return Result.success("上传成功", url);
    }
}
