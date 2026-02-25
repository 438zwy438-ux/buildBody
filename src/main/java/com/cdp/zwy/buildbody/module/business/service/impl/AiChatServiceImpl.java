package com.cdp.zwy.buildbody.module.business.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cdp.zwy.buildbody.module.business.controller.DTO.AiChatDTO;
import com.cdp.zwy.buildbody.module.business.dao.TbCoachProfileDao;
import com.cdp.zwy.buildbody.module.business.dao.TbCourseDao;
import com.cdp.zwy.buildbody.module.business.dao.TbEquipmentDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberProfileDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;
import com.cdp.zwy.buildbody.module.business.entity.TbCourse;
import com.cdp.zwy.buildbody.module.business.entity.TbEquipment;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.system.dao.SysOrderDao;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author zwy
 * @version 1.0
 * @description: AiChatServiceImpl
 * @date 2026/2/23 10:30
 */
@Service
public class AiChatServiceImpl {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.model}")
    private String model;

    @Resource
    private TbMemberProfileDao memberProfileDao;

    @Resource
    private TbCourseDao courseDao;

    @Resource
    private SysOrderDao sysOrderDao;

    @Resource
    private TbCoachProfileDao coachProfileDao;

    @Resource
    private TbEquipmentDao equipmentDao;

    /**
     * 核心对话方法
     */
    public String chat(AiChatDTO dto) {
        if (dto.getUserId() == null) throw new RuntimeException("必须提供用户ID进行安全隔离");

        // 1. 构建严格遵守 OpenAI 规范的消息数组
        JSONArray messages = new JSONArray();
        messages.add(new JSONObject().set("role", "system").set("content", "你是BuildBody健身房的智能前台。你可以调用工具查询系统数据来回答用户。如果用户的提问无关健身或本系统，请礼貌拒绝。"));

        // 装载历史记录
        if (CollUtil.isNotEmpty(dto.getHistory())) {
            for (AiChatDTO.HistoryMsg msg : dto.getHistory()) {
                messages.add(new JSONObject().set("role", msg.getRole()).set("content", msg.getContent()));
            }
        }
        // 装载当前用户最新问题
        messages.add(new JSONObject().set("role", "user").set("content", dto.getMessage()));

        // 2. 发起第一轮 LLM 请求 (携带可用工具列表)
        JSONObject firstResponse = callLLM(messages, buildTools());
        JSONObject responseMessage = firstResponse.getJSONObject("message");

        // 3. 核心判断：大模型是否决定调用 API 接口 (Function Calling)
        if (responseMessage.containsKey("tool_calls")) {
            // 【严谨性要求】：必须把大模型想调用的这个原始请求原封不动塞回历史中，否则后续会报格式错误
            messages.add(responseMessage);

            JSONArray toolCalls = responseMessage.getJSONArray("tool_calls");
            for (int i = 0; i < toolCalls.size(); i++) {
                JSONObject toolCall = toolCalls.getJSONObject(i);
                String toolCallId = toolCall.getStr("id");
                String functionName = toolCall.getJSONObject("function").getStr("name");

                // 4. 后端路由并执行本地真实数据库查询
                String functionResult = executeLocalFunction(functionName, dto.getUserId());

                // 5. 将数据库查到的结果，封装为 role="tool" 塞回消息数组
                JSONObject toolMessage = new JSONObject();
                toolMessage.set("role", "tool");
                toolMessage.set("tool_call_id", toolCallId);
                toolMessage.set("name", functionName);
                toolMessage.set("content", functionResult);
                messages.add(toolMessage);
            }

            // 6. 带着本地数据库的结果，发起第二轮 LLM 请求 (大模型进行总结)
            JSONObject secondResponse = callLLM(messages, null);
            return secondResponse.getJSONObject("message").getStr("content");
        }

        // 7. 如果没有 tool_calls，说明大模型觉得靠自己就能回答，直接返回内容
        return responseMessage.getStr("content");
    }

    /**
     * 定义暴露给 AI 的 API 接口描述 (JSON Schema)
     */
    private JSONArray buildTools() {
        JSONArray tools = new JSONArray();

        // 工具1：查询个人资产
        tools.add(JSONUtil.createObj()
                .set("type", "function")
                .set("function", JSONUtil.createObj()
                        .set("name", "get_my_fitness_profile")
                        .set("description", "获取当前用户的会员档案，包括剩余私教课节数、账户余额、是否为VIP。")
                        .set("parameters", JSONUtil.createObj().set("type", "object").set("properties", JSONUtil.createObj()))
                )
        );

        // 工具2：查询健身房课程
        tools.add(JSONUtil.createObj()
                .set("type", "function")
                .set("function", JSONUtil.createObj()
                        .set("name", "get_gym_courses")
                        .set("description", "获取当前健身房正在售卖的私教课程列表。")
                        .set("parameters", JSONUtil.createObj().set("type", "object").set("properties", JSONUtil.createObj()))
                )
        );

        // 工具3：查询教练列表
        tools.add(JSONUtil.createObj()
                .set("type", "function")
                .set("function", JSONUtil.createObj()
                        .set("name", "get_gym_coaches")
                        .set("description", "获取当前健身房在职教练列表，包括姓名、特长和简介。")
                        .set("parameters", JSONUtil.createObj().set("type", "object").set("properties", JSONUtil.createObj()))
                )
        );

        // 工具4：查询健身器材
        tools.add(JSONUtil.createObj()
                .set("type", "function")
                .set("function", JSONUtil.createObj()
                        .set("name", "get_gym_equipments")
                        .set("description", "获取当前健身房正常状态的健身器材列表，包括名称、位置和状态。")
                        .set("parameters", JSONUtil.createObj().set("type", "object").set("properties", JSONUtil.createObj()))
                )
        );

        // 工具5：查询用户订单
        tools.add(JSONUtil.createObj()
                .set("type", "function")
                .set("function", JSONUtil.createObj()
                        .set("name", "get_user_orders")
                        .set("description", "获取当前用户的订单列表，包括会员卡和私教课订单。")
                        .set("parameters", JSONUtil.createObj().set("type", "object").set("properties", JSONUtil.createObj()))
                )
        );

        return tools;
    }

    /**
     * 本地方法路由分发器
     */
    private String executeLocalFunction(String functionName, Long userId) {
        if ("get_my_fitness_profile".equals(functionName)) {
            TbMemberProfile profile = memberProfileDao.selectOne(new QueryWrapper<TbMemberProfile>().eq("user_id", userId));
            SysOrder sysOrder = sysOrderDao.selectOne(new QueryWrapper<SysOrder>().eq("user_id", userId).eq("type", 2));
            if (profile == null) return "查询失败：该用户还未办理会员。";

            JSONObject result = new JSONObject();
            result.set("balance", profile.getBalance() != null ? profile.getBalance() : 0);
            result.set("course_count", sysOrder.getRemainCount() != null ? sysOrder.getRemainCount() : 0);
            result.set("is_vip", profile.getIsVip() == 1);
            return result.toString(); // 必须返回字符串格式

        } else if ("get_gym_courses".equals(functionName)) {
            List<TbCourse> courses = courseDao.selectList(new QueryWrapper<TbCourse>()
                    .eq("type", 1).eq("status", 1));
            if (CollUtil.isEmpty(courses)) return "目前没有在售课程。";

            JSONArray result = new JSONArray();
            for (TbCourse c : courses) {
                result.add(new JSONObject().set("course_name", c.getName()).set("price", c.getPrice()).set("description", c.getDescription()));
            }
            return result.toString();
            
        } else if ("get_gym_coaches".equals(functionName)) {
            List<TbCoachProfile> coaches = coachProfileDao.selectList(new QueryWrapper<TbCoachProfile>()
                    .eq("status", 1));
            if (CollUtil.isEmpty(coaches)) return "目前没有在职教练。";

            JSONArray result = new JSONArray();
            for (TbCoachProfile coach : coaches) {
                JSONObject coachInfo = new JSONObject();
                coachInfo.set("name", coach.getRealName());
                coachInfo.set("specialty", coach.getSpecialty());
                coachInfo.set("intro", coach.getIntro());
                result.add(coachInfo);
            }
            return result.toString();
            
        } else if ("get_gym_equipments".equals(functionName)) {
            List<TbEquipment> equipments = equipmentDao.selectList(new QueryWrapper<TbEquipment>()
                    .eq("status", 1));
            if (CollUtil.isEmpty(equipments)) return "目前没有可用的健身器材。";

            JSONArray result = new JSONArray();
            for (TbEquipment equipment : equipments) {
                JSONObject equipmentInfo = new JSONObject();
                equipmentInfo.set("name", equipment.getName());
                equipmentInfo.set("code", equipment.getCode());
                equipmentInfo.set("location", equipment.getLocation());
                result.add(equipmentInfo);
            }
            return result.toString();
            
        } else if ("get_user_orders".equals(functionName)) {
            List<SysOrder> orders = sysOrderDao.selectList(new QueryWrapper<SysOrder>()
                    .eq("user_id", userId).orderByDesc("create_time"));
            if (CollUtil.isEmpty(orders)) return "您还没有任何订单。";

            JSONArray result = new JSONArray();
            for (SysOrder order : orders) {
                JSONObject orderInfo = new JSONObject();
                orderInfo.set("order_no", order.getOrderNo());
                orderInfo.set("subject", order.getSubject());
                orderInfo.set("type", order.getType() == 1 ? "会员卡" : "私教课");
                orderInfo.set("status", getOrderStatusText(order.getStatus()));
                orderInfo.set("total_amount", order.getTotalAmount());
                orderInfo.set("create_time", order.getCreateTime());
                if (order.getType() == 2) {
                    orderInfo.set("remain_count", order.getRemainCount());
                    orderInfo.set("total_count", order.getTotalCount());
                }
                result.add(orderInfo);
            }
            return result.toString();
        }
        return "未知错误：未定义的接口。";
    }
    
    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已取消";
            case 3: return "已退款";
            default: return "未知状态";
        }
    }

    /**
     * 底层 HTTP 请求封装 (Hutool)
     */
    private JSONObject callLLM(JSONArray messages, JSONArray tools) {
        JSONObject body = new JSONObject();
        body.set("model", model);
        body.set("messages", messages);
        body.set("temperature", 0.3); // 调低温度，让 AI 做数据决策时更严谨

        if (tools != null) {
            body.set("tools", tools);
            body.set("tool_choice", "auto");
        }

        try (HttpResponse response = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(40000) // function calling 过程较长，超时放宽到40秒
                .execute()) {

            if (!response.isOk()) {
                throw new RuntimeException("AI接口异常: " + response.body());
            }

            JSONObject jsonRes = JSONUtil.parseObj(response.body());
            return jsonRes.getJSONArray("choices").getJSONObject(0);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("连接大模型失败：" + e.getMessage());
        }
    }
}