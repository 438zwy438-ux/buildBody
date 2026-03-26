package com.cdp.zwy.buildbody.module.system.controller.VO;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 私教课订单VO（包含多表查询结果）
 *
 * @author zwy
 * @version 1.0
 * @description: CourseOrderVO
 * @date 2026/3/26
 */
@Data
public class CourseOrderVO {
    // ====== SysOrder 所有字段 ======
    private Long id;
    private String orderNo;
    private Long userId;
    private String subject;
    private Integer remainCount;
    private Double totalAmount;
    private Double refundAmount;
    private Integer payType;
    private Integer status;
    private Date payTime;
    private Date cancelTime;
    private Date refundTime;
    private Date createTime;
    private Integer type;
    private Integer totalCount;
    private Long courseId;
    private Long cardId;

    // ====== TbCourse 字段 ======
    private String courseName;
    private Long coachUserId;

    // ====== TbCoachProfile 字段 ======
    private String coachRealName;
}