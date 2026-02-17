package com.cdp.zwy.buildbody.common.utils;

import com.cdp.zwy.buildbody.common.config.MinioProp;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.io.InputStream;

/**
 * @author zwy
 * @version 1.0
 * @description: MinioUtil
 * @date 2026/2/17 11:01
 */
@Component
public class MinioUtil {

    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioProp minioProp;

    /**
     * 上传文件并返回访问URL
     */
    public String upload(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        // 生成唯一文件名，防止覆盖：UUID + 后缀
        String fileName = UUID.randomUUID().toString().replace("-", "") +
                originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 如果指定了文件夹，则在文件名前加上文件夹路径
        if (folder != null && !folder.trim().isEmpty()) {
            // 确保文件夹路径以/结尾，避免路径拼接问题
            if (!folder.endsWith("/")) {
                folder = folder + "/";
            }
            fileName = folder + fileName;
        }
        
        try {
            InputStream inputStream = file.getInputStream();
            // 上传到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProp.getBucketName())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 拼接返回 URL (假设 Bucket 是 Public 的)
            // 格式: http://localhost:9000/gym-face/folder/xxxx.jpg
            return minioProp.getEndpoint() + "/" + minioProp.getBucketName() + "/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}