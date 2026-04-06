package com.cdp.zwy.buildbody.module.business.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.controller.DTO.AiChatDTO;
import com.cdp.zwy.buildbody.module.business.dao.TbEquipmentDao;
import com.cdp.zwy.buildbody.module.business.entity.TbEquipment;
import com.cdp.zwy.buildbody.module.business.service.impl.AiChatServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zwy
 * @version 1.0
 * @description: AiChatController
 * @date 2026/2/23 10:47
 */
@RestController
@RequestMapping("/ai")
@Tag(name = "AI 智能助理")
public class AiChatController {

    @Resource
    private AiChatServiceImpl aiChatService;

    @Resource
    private TbEquipmentDao equipmentDao;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.model}")
    private String model;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final OkHttpClient httpClient = new OkHttpClient();

    @Operation(summary = "发送对话")
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody AiChatDTO dto) {
        try {
            return Result.success(aiChatService.chat(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "流式对话")
    @GetMapping("/streamChat")
    public SseEmitter streamChat(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter(60000L);

        emitter.onTimeout(() -> {
            emitter.complete();
        });

        emitter.onCompletion(() -> {
        });

        executorService.execute(() -> {
            try {
                String systemPrompt = "你是BuildBody健身房的智能前台。你可以回答用户的健身相关问题。";

                if (message.contains("器材") || message.contains("器械") || message.contains("跑步机") || message.contains("设备")) {
                    QueryWrapper<TbEquipment> wrapper = new QueryWrapper<>();
                    wrapper.select("name", "status");
                    List<Map<String, Object>> equipmentList = equipmentDao.selectMaps(wrapper);

                    JSONArray equipmentArray = new JSONArray();
                    for (Map<String, Object> equipment : equipmentList) {
                        JSONObject equipmentObj = new JSONObject();
                        equipmentObj.put("name", equipment.get("name"));
                        equipmentObj.put("status", equipment.get("status"));
                        equipmentArray.add(equipmentObj);
                    }

                    systemPrompt += " 当前健身房器材实时状态如下：" + equipmentArray.toJSONString() + "。请根据这些信息回答用户关于器材的问题。";
                }

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model);
                requestBody.put("stream", true);

                JSONArray messages = new JSONArray();
                messages.add(new JSONObject().fluentPut("role", "system").fluentPut("content", systemPrompt));
                messages.add(new JSONObject().fluentPut("role", "user").fluentPut("content", message));
                requestBody.put("messages", messages);

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(okhttp3.RequestBody.create(MediaType.parse("application/json"), requestBody.toJSONString()))
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new RuntimeException("AI接口异常: " + response.code());
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                emitter.complete();
                                break;
                            }

                            try {
                                JSONObject dataObj = JSON.parseObject(data);
                                JSONArray choices = dataObj.getJSONArray("choices");
                                if (choices != null && !choices.isEmpty()) {
                                    JSONObject choice = choices.getJSONObject(0);
                                    JSONObject delta = choice.getJSONObject("delta");
                                    if (delta != null && delta.containsKey("content")) {
                                        String content = delta.getString("content");
                                        emitter.send(SseEmitter.event().data(content));
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                    emitter.complete();
                }
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().data("抱歉，发生了错误：" + e.getMessage()));
                } catch (Exception ex) {
                }
                emitter.complete();
            }
        });

        return emitter;
    }
}