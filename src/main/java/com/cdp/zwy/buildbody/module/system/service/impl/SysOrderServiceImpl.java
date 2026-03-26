package com.cdp.zwy.buildbody.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbCardTemplateDao;
import com.cdp.zwy.buildbody.module.business.dao.TbCoachProfileDao;
import com.cdp.zwy.buildbody.module.business.dao.TbCourseDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberCardDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCardTemplate;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;
import com.cdp.zwy.buildbody.module.business.entity.TbCourse;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberCard;
import com.cdp.zwy.buildbody.module.system.controller.VO.CourseOrderVO;
import com.cdp.zwy.buildbody.module.system.dao.SysOrderDao;
import com.cdp.zwy.buildbody.module.system.dao.SysUserDao;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 系统订单表(SysOrder)表服务实现类
 *
 * @author makejava
 * @since 2026-02-21 13:05:15
 */
@Service("sysOrderService")
public class SysOrderServiceImpl extends ServiceImpl<SysOrderDao, SysOrder> implements SysOrderService {

    @Resource
    private TbCardTemplateDao tbCardTemplateDao;

    @Resource
    private TbMemberCardDao tbMemberCardDao;

    @Resource
    private SysUserDao sysUserDao;

    @Resource
    private TbCourseDao tbCourseDao;

    @Resource
    private TbCoachProfileDao tbCoachProfileDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCourseOrder(Long userId, Long courseId, Integer courseTimes, Double amount) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();
        
        // 2. 创建订单
        SysOrder order = new SysOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setSubject("私教课购买");
        order.setType(2); // 2-私教课
        order.setTotalAmount(amount);
        order.setStatus(0); // 0-待支付
        order.setTotalCount(courseTimes);
        order.setRemainCount(courseTimes);
        order.setPayType(1); // 1-微信支付
        order.setCreateTime(new Date());
        order.setCourseId(courseId); // 设置课程ID
        
        this.save(order);
        return order.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMemberCardOrder(Long userId, Integer cardTimes, Double amount) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();
        
        // 2. 创建订单
        SysOrder order = new SysOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setSubject("会员卡购买");
        order.setType(1); // 1-会员卡
        order.setTotalAmount(amount);
        order.setStatus(0); // 0-待支付
        order.setTotalCount(cardTimes);
        order.setRemainCount(cardTimes);
        order.setPayType(1); // 1-微信支付
        order.setCreateTime(new Date());
        // 注意：这里不设置cardId，因为创建订单时还没有创建会员卡
        // 在创建会员卡后需要更新订单的cardId
        
        this.save(order);
        return order.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payOrder(Long orderId) {
        SysOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单已支付或已退款");
        }
        
        // 更新订单状态为已支付
        order.setStatus(1); // 1-已支付
        order.setPayTime(new Date());
        
        return this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long orderId) {
        SysOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("只能取消待支付的订单");
        }
        
        // 更新订单状态为已取消
        order.setStatus(2); // 2-已取消
        order.setCancelTime(new Date());
        
        return this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean refundOrder(Long orderId) {
        SysOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 1) {
            throw new RuntimeException("只能退款已支付的订单");
        }
        
        // 计算退款金额
        Double refundAmount = calculateRefundAmount(order);
        
        // 更新订单状态为已退款
        order.setStatus(3); // 3-已退款
        order.setRefundTime(new Date());
        order.setRefundAmount(refundAmount);
        
        // 如果是会员卡订单，冻结会员卡并检查用户状态
        if (order.getType() == 1 && order.getCardId() != null) {
            TbMemberCard memberCard = tbMemberCardDao.selectById(order.getCardId());
            if (memberCard != null) {
                // 冻结会员卡
                memberCard.setStatus(2); // 2-冻结
                tbMemberCardDao.updateById(memberCard);
                
                // 检查用户是否还有其他有效的会员卡
                QueryWrapper<TbMemberCard> cardQuery = new QueryWrapper<>();
                cardQuery.eq("user_id", order.getUserId());
                cardQuery.eq("status", 1); // 1-正常
                cardQuery.ne("id", memberCard.getId()); // 排除当前冻结的卡
                Long activeCardCount = tbMemberCardDao.selectCount(cardQuery);
                
                // 如果没有其他有效会员卡，停用用户
                if (activeCardCount == 0) {
                    SysUser user = sysUserDao.selectById(order.getUserId());
                    if (user != null) {
                        user.setStatus(0); // 0-停用
                        sysUserDao.updateById(user);
                    }
                }
            }
        }
        
        return this.updateById(order);
    }

    private Double calculateRefundAmount(SysOrder order) {
        if (order.getType() == 2) {
            // 私教课：按次数退款
            // 退款金额 = 总金额/总次数*剩余次数*80%
            if (order.getTotalCount() != null && order.getTotalCount() > 0) {
                return order.getTotalAmount() / order.getTotalCount() * order.getRemainCount() * 0.8;
            }
            return 0.0;
        } else if (order.getType() == 1 && order.getCardId() != null) {
            // 会员卡：需要查询会员卡模板类型
            TbMemberCard memberCard = tbMemberCardDao.selectById(order.getCardId());
            if (memberCard != null) {
                TbCardTemplate template = tbCardTemplateDao.selectById(memberCard.getTemplateId());
                if (template != null) {
                    if (template.getType() == 2) {
                        // 次卡：总金额/总次数*剩余次数*80%
                        if (order.getTotalCount() != null && order.getTotalCount() > 0) {
                            return order.getTotalAmount() / order.getTotalCount() * order.getRemainCount() * 0.8;
                        }
                    } else if (template.getType() == 1) {
                        // 时间卡：总金额/总天数*剩余天数*80%
                        if (memberCard.getActiveTime() != null && memberCard.getExpireTime() != null) {
                            LocalDateTime activeTime = memberCard.getActiveTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalDateTime expireTime = memberCard.getExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalDateTime now = LocalDateTime.now();
                            
                            long totalDays = ChronoUnit.DAYS.between(activeTime, expireTime);
                            long remainDays = ChronoUnit.DAYS.between(now, expireTime);
                            
                            if (totalDays > 0 && remainDays > 0) {
                                return order.getTotalAmount() / totalDays * remainDays * 0.8;
                            }
                        }
                    }
                }
            }
        }
        return 0.0;
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        return "ORD" + timeStr + String.format("%04d", randomNum);
    }

    @Override
    public List<CourseOrderVO> getMyCourseOrders(Long userId) {
        // 1. 查询用户的私教课订单
        QueryWrapper<SysOrder> orderQuery = new QueryWrapper<>();
        orderQuery.eq("user_id", userId);
        orderQuery.eq("type", 2); // 2-私教课
        orderQuery.eq("status", 1); // 1-已支付
        orderQuery.orderByDesc("create_time");
        List<SysOrder> orders = this.list(orderQuery);

        if (orders.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 构建结果列表
        List<CourseOrderVO> result = new ArrayList<>();

        for (SysOrder order : orders) {
            CourseOrderVO vo = new CourseOrderVO();

            // 复制订单字段
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setUserId(order.getUserId());
            vo.setSubject(order.getSubject());
            vo.setRemainCount(order.getRemainCount());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setRefundAmount(order.getRefundAmount());
            vo.setPayType(order.getPayType());
            vo.setStatus(order.getStatus());
            vo.setPayTime(order.getPayTime());
            vo.setCancelTime(order.getCancelTime());
            vo.setRefundTime(order.getRefundTime());
            vo.setCreateTime(order.getCreateTime());
            vo.setType(order.getType());
            vo.setTotalCount(order.getTotalCount());
            vo.setCourseId(order.getCourseId());
            vo.setCardId(order.getCardId());

            // 查询课程信息
            if (order.getCourseId() != null) {
                TbCourse course = tbCourseDao.selectById(order.getCourseId());
                if (course != null) {
                    vo.setCourseName(course.getName());
                    vo.setCoachUserId(course.getCoachUserId());

                    // 查询教练信息
                    if (course.getCoachUserId() != null) {
                        QueryWrapper<TbCoachProfile> coachQuery = new QueryWrapper<>();
                        coachQuery.eq("user_id", course.getCoachUserId());
                        coachQuery.eq("status", 1); // 1-在职
                        TbCoachProfile coach = tbCoachProfileDao.selectOne(coachQuery);
                        if (coach != null) {
                            vo.setCoachRealName(coach.getRealName());
                        }
                    }
                }
            }

            result.add(vo);
        }

        return result;
    }
}