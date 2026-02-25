package com.cdp.zwy.buildbody.module.business.controller;

import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.controller.DTO.AiChatDTO;
import com.cdp.zwy.buildbody.module.business.service.impl.AiChatServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zwy
 * @version 1.0
 * @description: AiChatController
 * @date 2026/2/23 10:47
 */
@RestController
@RequestMapping("/ai")
@Tag(name = "AI 智能助理(Function Calling 版)")
public class AiChatController {

    @Resource
    private AiChatServiceImpl aiChatService;

    @Operation(summary = "发送对话")
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody AiChatDTO dto) {
        try {
            return Result.success(aiChatService.chat(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}