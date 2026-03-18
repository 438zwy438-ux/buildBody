package com.cdp.zwy.buildbody.module.system.controller;

import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;
import com.cdp.zwy.buildbody.module.system.service.ImgRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传相关接口")
public class UploadController {

    @Resource
    private ImgRelationService imgRelationService;

    @Operation(summary = "上传文件")
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;
            
            String uploadPath = "D:/study/buildbody/uploads/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            File dest = new File(uploadPath + newFileName);
            file.transferTo(dest);
            
            String fileUrl = "http://localhost:8080/uploads/" + newFileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }

    @Operation(summary = "上传设备图片")
    @PostMapping("/equipment/{equipmentId}")
    public Result<Boolean> uploadEquipmentImage(
            @PathVariable Long equipmentId,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;
            
            String uploadPath = "D:/study/buildbody/uploads/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            File dest = new File(uploadPath + newFileName);
            file.transferTo(dest);
            
            String fileUrl = "http://localhost:8080/uploads/" + newFileName;
            
            ImgRelation imgRelation = new ImgRelation();
            imgRelation.setRelationType(1);
            imgRelation.setRelationId(equipmentId.intValue());
            imgRelation.setImgUrl(fileUrl);
            imgRelation.setCreateTime(new Date());
            
            imgRelationService.save(imgRelation);
            
            return Result.success(true);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}