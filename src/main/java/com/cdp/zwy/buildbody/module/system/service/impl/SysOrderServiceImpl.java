package com.cdp.zwy.buildbody.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbCardTemplateDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberCardDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCardTemplate;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberCard;
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
import java.util.Date;
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
}