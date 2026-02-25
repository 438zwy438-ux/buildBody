package com.cdp.zwy.buildbody.module.business.controller.DTO;

import lombok.Data;

import java.util.List;

/**
 * @author zwy
 * @version 1.0
 * @description: AiChatDTO
 * @date 2026/2/23 10:22
 */
@Data
public class AiChatDTO {
    private Long userId; // 必须传入，用于数据隔离
    private String message; // 用户当前输入的新问题
    // 简化前端难度，前端不需要维护复杂的 tool 历史，只传纯文本对话历史即可
    private List<HistoryMsg> history;

    @Data
    public static class HistoryMsg {
        private String role; // "user" 或 "assistant"
        private String content;
    }
}