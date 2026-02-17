package com.cdp.zwy.buildbody.module.system.controller;

import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.common.utils.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zwy
 * @version 1.0
 * @description: CommonController
 * @date 2026/2/17 11:03
 */
@RestController
@RequestMapping("/common")
@Tag(name = "通用功能接口")
public class CommonController {

    @Resource
    private MinioUtil minioUtil;

    @Operation(summary = "上传文件(MinIO)")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam("folder") String folder) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        String url = minioUtil.upload(file, folder);
        return Result.success(url);
    }
}