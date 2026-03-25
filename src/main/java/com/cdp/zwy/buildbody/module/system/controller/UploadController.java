package com.cdp.zwy.buildbody.module.system.controller;

import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.common.utils.MinioUtil;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;
import com.cdp.zwy.buildbody.module.system.service.ImgRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传相关接口")
public class UploadController {

    @Resource
    private ImgRelationService imgRelationService;
    @Resource
    private MinioUtil minioUtil;

    @Operation(summary = "上传文件")
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String fileUrl = minioUtil.upload(file, null);
            return Result.success(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }

    @Operation(summary = "上传设备图片")
    @PostMapping("/equipment/{equipmentId}")
    public Result<String> uploadEquipmentImage(
            @PathVariable Long equipmentId,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String fileUrl = minioUtil.upload(file, "equipment");
            
            ImgRelation imgRelation = new ImgRelation();
            imgRelation.setRelationType(1);
            imgRelation.setRelationId(equipmentId);
            imgRelation.setImgUrl(fileUrl);
            imgRelation.setCreateTime(new Date());
            
            imgRelationService.save(imgRelation);
            
            return Result.success(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }

    @Operation(summary = "上传教练图片")
    @PostMapping("/coach/{coachId}")
    public Result<String> uploadCoachImage(
            @PathVariable Long coachId,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String fileUrl = minioUtil.upload(file, "coach");
            
            ImgRelation imgRelation = new ImgRelation();
            imgRelation.setRelationType(2);
            imgRelation.setRelationId(coachId);
            imgRelation.setImgUrl(fileUrl);
            imgRelation.setCreateTime(new Date());
            
            imgRelationService.save(imgRelation);
            
            return Result.success(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }

    @Operation(summary = "上传课程图片")
    @PostMapping("/course/{courseId}")
    public Result<String> uploadCourseImage(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            String fileUrl = minioUtil.upload(file, "course");
            
            ImgRelation imgRelation = new ImgRelation();
            imgRelation.setRelationType(3);
            imgRelation.setRelationId(courseId);
            imgRelation.setImgUrl(fileUrl);
            imgRelation.setCreateTime(new Date());
            
            imgRelationService.save(imgRelation);
            
            return Result.success(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}