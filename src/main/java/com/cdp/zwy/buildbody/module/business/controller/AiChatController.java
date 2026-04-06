package com.cdp.zwy.buildbody.module.business.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.controller.DTO.AiChatDTO;
import com.cdp.zwy.buildbody.module.business.dao.TbCoachProfileDao;
import com.cdp.zwy.buildbody.module.business.dao.TbCourseDao;
import com.cdp.zwy.buildbody.module.business.dao.TbEquipmentDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberCardDao;
import com.cdp.zwy.buildbody.module.business.dao.TbCardTemplateDao;
import com.cdp.zwy.buildbody.module.business.entity.*;
import com.cdp.zwy.buildbody.module.business.service.impl.AiChatServiceImpl;
import com.cdp.zwy.buildbody.module.system.dao.SysOrderDao;
import com.cdp.zwy.buildbody.module.system.dao.TbEntryLogDao;
import com.cdp.zwy.buildbody.module.system.dao.ImgRelationDao;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.entity.TbEntryLog;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;
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

    @Resource
    private TbCoachProfileDao coachProfileDao;

    @Resource
    private TbCourseDao courseDao;

    @Resource
    private TbMemberCardDao memberCardDao;

    @Resource
    private TbCardTemplateDao cardTemplateDao;

    @Resource
    private SysOrderDao orderDao;

    @Resource
    private TbEntryLogDao entryLogDao;

    @Resource
    private ImgRelationDao imgRelationDao;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.model}")
    private String model;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

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
    @GetMapping(value = "/streamChat", produces = "text/event-stream;charset=UTF-8")
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

                if (message.contains("教练") || message.contains("私教") || message.contains("老师")) {
                    QueryWrapper<TbCoachProfile> wrapper = new QueryWrapper<>();
                    wrapper.select("real_name", "specialty", "intro", "entry_date", "certificates");
                    wrapper.eq("status", 1);
                    
                    String coachName = extractCoachName(message);
                    if (coachName != null && !coachName.isEmpty()) {
                        wrapper.like("real_name", coachName);
                    }
                    
                    List<Map<String, Object>> coachList = coachProfileDao.selectMaps(wrapper);

                    JSONArray coachArray = new JSONArray();
                    for (Map<String, Object> coach : coachList) {
                        JSONObject coachObj = new JSONObject();
                        coachObj.put("name", coach.get("real_name"));
                        coachObj.put("specialty", coach.get("specialty"));
                        coachObj.put("intro", coach.get("intro"));
                        coachObj.put("entry_date", coach.get("entry_date"));
                        coachObj.put("certificates", coach.get("certificates"));
                        coachObj.put("type", "coach");
                        coachArray.add(coachObj);
                    }

                    if (coachName != null && !coachName.isEmpty()) {
                        systemPrompt += " 教练[" + coachName + "]的资料如下：" + coachArray.toJSONString() + 
                            "。请按照以下格式回答：\n" +
                            "**教练姓名**：xxx\n" +
                            "**特长**：xxx\n" +
                            "**简介**：xxx\n" +
                            "**入职日期**：xxx\n" +
                            "\n" +
                            "**证书**：\n" +
                            "![证书](图片URL1)\n" +
                            "![证书](图片URL2)\n" +
                            "\n" +
                            "在教练的详细信息后面，立即显示该教练的证书图片（使用Markdown图片语法），不要把所有图片放在最后。";
                    } else {
                        systemPrompt += " 当前健身房在职教练列表如下：" + coachArray.toJSONString() + 
                            "。请按照以下格式回答：\n" +
                            "1. **教练姓名**\n" +
                            "   - 特长：xxx\n" +
                            "   - 简介：xxx\n" +
                            "   - 入职日期：xxx\n" +
                            "   \n" +
                            "   **证书**：\n" +
                            "   ![证书](图片URL1)\n" +
                            "   ![证书](图片URL2)\n" +
                            "   \n" +
                            "2. **教练姓名**\n" +
                            "   ...\n" +
                            "在每个教练的详细信息后面，立即显示该教练的证书图片（使用Markdown图片语法），不要把所有图片放在最后。";
                    }
                }

                if (message.contains("课程") || message.contains("私教课") || message.contains("团课")) {
                    QueryWrapper<TbCourse> wrapper = new QueryWrapper<>();
                    wrapper.select("id", "name", "type", "price", "duration", "description", "cover_img");
                    wrapper.eq("status", 1);
                    List<Map<String, Object>> courseList = courseDao.selectMaps(wrapper);

                    JSONArray courseArray = new JSONArray();
                    for (Map<String, Object> course : courseList) {
                        JSONObject courseObj = new JSONObject();
                        courseObj.put("name", course.get("name"));
                        courseObj.put("type", course.get("type"));
                        courseObj.put("price", course.get("price"));
                        courseObj.put("duration", course.get("duration"));
                        courseObj.put("description", course.get("description"));
                        courseObj.put("cover_img", course.get("cover_img"));
                        courseObj.put("type_label", "course");

                        Long courseId = (Long) course.get("id");
                        QueryWrapper<ImgRelation> imgWrapper = new QueryWrapper<>();
                        imgWrapper.select("img_url");
                        imgWrapper.eq("relation_type", 3);

                        imgWrapper.last("LIMIT 5");
                        List<Map<String, Object>> imgList = imgRelationDao.selectMaps(imgWrapper);

                        JSONArray imgUrls = new JSONArray();
                        for (Map<String, Object> img : imgList) {
                            imgUrls.add(img.get("img_url"));
                        }
                        courseObj.put("images", imgUrls);

                        courseArray.add(courseObj);
                    }

                    systemPrompt += " 当前健身房在售课程列表如下：" + courseArray.toJSONString() + 
                        "。请根据这些信息回答用户关于课程的问题。在回答时，请按照以下格式：\n" +
                        "1. **课程名称**\n" +
                        "   - 价格：xxx\n" +
                        "   - 时长：xxx\n" +
                        "   - 描述：xxx\n" +
                        "   \n" +
                        "   ![课程图片](图片URL1)\n" +
                        "   ![课程图片](图片URL2)\n" +
                        "   \n" +
                        "2. **课程名称**\n" +
                        "   ...\n" +
                        "在每个课程的详细信息后面，立即显示该课程的图片（使用Markdown图片语法），不要把所有图片放在最后。";
                }

                if (message.contains("订单") || message.contains("购买") || message.contains("支付")) {
                    QueryWrapper<SysOrder> wrapper = new QueryWrapper<>();
                    wrapper.select("order_no", "subject", "type", "status", "total_amount", "create_time");
                    wrapper.orderByDesc("create_time");
                    wrapper.last("LIMIT 10");
                    List<Map<String, Object>> orderList = orderDao.selectMaps(wrapper);

                    JSONArray orderArray = new JSONArray();
                    for (Map<String, Object> order : orderList) {
                        JSONObject orderObj = new JSONObject();
                        orderObj.put("order_no", order.get("order_no"));
                        orderObj.put("subject", order.get("subject"));
                        orderObj.put("type", order.get("type"));
                        orderObj.put("status", order.get("status"));
                        orderObj.put("total_amount", order.get("total_amount"));
                        orderObj.put("create_time", order.get("create_time"));
                        orderArray.add(orderObj);
                    }

                    systemPrompt += " 最近订单记录如下：" + orderArray.toJSONString() + "。请根据这些信息回答用户关于订单的问题。";
                }

                if (message.contains("会员卡") || message.contains("卡种") || message.contains("健身卡")) {
                    QueryWrapper<TbCardTemplate> wrapper = new QueryWrapper<>();
                    wrapper.select("id", "name", "type", "price", "duration_days", "times", "description", "status");
                    wrapper.eq("status", 1);
                    wrapper.last("LIMIT 10");
                    List<Map<String, Object>> cardList = cardTemplateDao.selectMaps(wrapper);

                    JSONArray cardArray = new JSONArray();
                    for (Map<String, Object> card : cardList) {
                        JSONObject cardObj = new JSONObject();
                        cardObj.put("id", card.get("id"));
                        cardObj.put("name", card.get("name"));
                        cardObj.put("type", card.get("type"));
                        cardObj.put("price", card.get("price"));
                        cardObj.put("duration_days", card.get("duration_days"));
                        cardObj.put("times", card.get("times"));
                        cardObj.put("description", card.get("description"));
                        cardObj.put("status", card.get("status"));
                        cardArray.add(cardObj);
                    }

                    systemPrompt += " 会员卡种信息如下：" + cardArray.toJSONString() + "。请根据这些信息回答用户关于会员卡的问题。";
                }

                if (message.contains("人多") || message.contains("人流量") || message.contains("拥挤") || 
                    (message.contains("现在") && (message.contains("人") || message.contains("忙")))) {
                    QueryWrapper<TbEntryLog> wrapper = new QueryWrapper<>();
                    wrapper.select("user_name", "entry_time", "exit_time", "status");
                    wrapper.orderByDesc("entry_time");
                    wrapper.last("LIMIT 20");
                    List<Map<String, Object>> entryList = entryLogDao.selectMaps(wrapper);

                    int currentInGym = 0;
                    int totalToday = 0;
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    java.time.LocalDateTime todayStart = now.toLocalDate().atStartOfDay();

                    JSONArray entryArray = new JSONArray();
                    for (Map<String, Object> entry : entryList) {
                        JSONObject entryObj = new JSONObject();
                        entryObj.put("user_name", entry.get("user_name"));
                        entryObj.put("entry_time", entry.get("entry_time"));
                        entryObj.put("exit_time", entry.get("exit_time"));
                        entryObj.put("status", entry.get("status"));
                        entryArray.add(entryObj);

                        java.time.LocalDateTime entryTime = (java.time.LocalDateTime) entry.get("entry_time");
                        java.time.LocalDateTime exitTime = (java.time.LocalDateTime) entry.get("exit_time");

                        if (entryTime != null && entryTime.isAfter(todayStart)) {
                            totalToday++;
                            if (exitTime == null || entryTime.isAfter(exitTime)) {
                                currentInGym++;
                            }
                        }
                    }

                    JSONObject crowdInfo = new JSONObject();
                    crowdInfo.put("current_in_gym", currentInGym);
                    crowdInfo.put("total_today", totalToday);
                    crowdInfo.put("recent_entries", entryArray);

                    String crowdLevel = "空闲";
                    if (currentInGym >= 50) {
                        crowdLevel = "拥挤";
                    } else if (currentInGym >= 30) {
                        crowdLevel = "繁忙";
                    } else if (currentInGym >= 15) {
                        crowdLevel = "适中";
                    }
                    crowdInfo.put("crowd_level", crowdLevel);

                    systemPrompt += " 当前健身房人流情况如下：" + crowdInfo.toJSONString() + 
                        "。请根据这些信息回答用户关于人流量的问题。";
                }

                if (message.contains("入场") || message.contains("签到") || message.contains("打卡") || message.contains("进出")) {
                    QueryWrapper<TbEntryLog> wrapper = new QueryWrapper<>();
                    wrapper.select("user_name", "entry_time", "exit_time", "status", "verify_mode");
                    wrapper.orderByDesc("entry_time");
                    wrapper.last("LIMIT 10");
                    List<Map<String, Object>> entryList = entryLogDao.selectMaps(wrapper);

                    JSONArray entryArray = new JSONArray();
                    for (Map<String, Object> entry : entryList) {
                        JSONObject entryObj = new JSONObject();
                        entryObj.put("user_name", entry.get("user_name"));
                        entryObj.put("entry_time", entry.get("entry_time"));
                        entryObj.put("exit_time", entry.get("exit_time"));
                        entryObj.put("status", entry.get("status"));
                        entryObj.put("verify_mode", entry.get("verify_mode"));
                        entryArray.add(entryObj);
                    }

                    systemPrompt += " 最近入场记录如下：" + entryArray.toJSONString() + "。请根据这些信息回答用户关于入场记录的问题。";
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
                                        JSONObject wrapper = new JSONObject();
                                        wrapper.put("content", content);
                                        emitter.send(SseEmitter.event().data(wrapper.toJSONString()));
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

    private String extractCoachName(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }

        String[] keywords = {"教练", "私教", "老师"};
        String lowerMessage = message.toLowerCase();

        for (String keyword : keywords) {
            int index = lowerMessage.indexOf(keyword);
            if (index != -1) {
                String beforeKeyword = message.substring(0, index).trim();
                String afterKeyword = message.substring(index + keyword.length()).trim();

                if (!beforeKeyword.isEmpty()) {
                    String[] parts = beforeKeyword.split("\\s+");
                    String lastPart = parts[parts.length - 1];
                    if (lastPart.length() >= 2 && lastPart.length() <= 4) {
                        return lastPart;
                    }
                }

                if (!afterKeyword.isEmpty()) {
                    String[] parts = afterKeyword.split("\\s+");
                    String firstPart = parts[0];
                    if (firstPart.length() >= 2 && firstPart.length() <= 4) {
                        return firstPart;
                    }
                }
            }
        }

        return null;
    }
}