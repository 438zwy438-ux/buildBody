package com.cdp.zwy.buildbody.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbCardTemplateDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberCardDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberProfileDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCardTemplate;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberCard;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.system.controller.VO.MemberCheckVO;
import com.cdp.zwy.buildbody.module.system.dao.SysUserDao;
import com.cdp.zwy.buildbody.module.system.dao.TbEntryLogDao;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import com.cdp.zwy.buildbody.module.system.entity.TbEntryLog;
import com.cdp.zwy.buildbody.module.system.service.TbEntryLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 进出场记录表(TbEntryLog)表服务实现类
 *
 * @author makejava
 * @since 2026-02-19 09:46:38
 */
@Service("tbEntryLogService")
public class TbEntryLogServiceImpl extends ServiceImpl<TbEntryLogDao, TbEntryLog> implements TbEntryLogService {
    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private TbMemberProfileDao memberProfileDao;
    @Resource
    private TbMemberCardDao memberCardDao;
    @Resource
    private TbCardTemplateDao cardTemplateDao;
    @Override
    public List<MemberCheckVO> searchMemberForCheckIn(String phone) {
        // A. 模糊查询用户 (支持手机号后四位)
        List<SysUser> users = sysUserDao.selectList(new QueryWrapper<SysUser>()
                .like("phone", phone)); // 简单实现，如果数据量大需优化

        if (users.isEmpty()) {
            throw new RuntimeException("未找到该手机号对应的会员");
        }
        
        List<MemberCheckVO> result = new ArrayList<>();
        
        // 遍历所有匹配的用户
        for (SysUser user : users) {
            // B. 查询档案
            TbMemberProfile profile = memberProfileDao.selectOne(new QueryWrapper<TbMemberProfile>().eq("user_id", user.getUserId()));
            if (profile == null) {
                continue; // 跳过没有档案的用户
            }

            // C. 查询持卡信息 (查最新的一张卡)
            TbMemberCard card = memberCardDao.selectOne(new QueryWrapper<TbMemberCard>()
                    .eq("user_id", user.getUserId())
                    .orderByDesc("create_time")
                    .last("LIMIT 1"));

            // D. 组装 VO
            MemberCheckVO vo = new MemberCheckVO();
            vo.setUserId(user.getUserId());
            vo.setRealName(profile.getRealName());
            vo.setPhone(user.getPhone());
            vo.setFaceImgUrl(profile.getFaceImgUrl());
            vo.setBalance(profile.getBalance());

            // E. 核心校验逻辑
            checkCardStatus(card, vo);

            result.add(vo);
        }
        
        return result;
    }
    /**
     * 辅助方法：校验卡状态
     */
    private void checkCardStatus(TbMemberCard card, MemberCheckVO vo) {
        if (card == null) {
            vo.setCardStatusStr("无会员卡");
            vo.setCanEntry(false);
            return;
        }

        TbCardTemplate template = cardTemplateDao.selectById(card.getTemplateId());
        vo.setCardName(template.getName());
        vo.setExpireTime(card.getExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        vo.setRemainCount(card.getRemainCount());

        LocalDateTime now = LocalDateTime.now();
        // 1. 检查状态位
        if (card.getStatus() != 1) { // 假设1是正常
            vo.setCardStatusStr("卡状态异常(冻结/作废)");
            vo.setCanEntry(false);
            return;
        }
        // 2. 检查过期时间
        if (card.getExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isBefore(now)) {
            vo.setCardStatusStr("已过期");
            vo.setCanEntry(false);
            return;
        }
        // 3. 检查次数 (如果是次卡，类型假设为2)
        if (template.getType() == 2 && card.getRemainCount() <= 0) {
            vo.setCardStatusStr("剩余次数不足");
            vo.setCanEntry(false);
            return;
        }

        vo.setCardStatusStr("正常");
        vo.setCanEntry(true);
    }

    /**
     * 2. 执行入场 (扣次、记录日志)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmCheckIn(Long userId, Long adminId) {
        // 复查卡状态（防止并发）
        TbMemberCard card = memberCardDao.selectOne(new QueryWrapper<TbMemberCard>()
                .eq("user_id", userId).eq("status", 1)
                .orderByDesc("create_time").last("LIMIT 1"));

        if (card == null) throw new RuntimeException("无有效会员卡");

        TbCardTemplate template = cardTemplateDao.selectById(card.getTemplateId());

        // 查找最近一条未出场的记录
        TbEntryLog existingLog = this.baseMapper.selectOne(new QueryWrapper<TbEntryLog>()
                .eq("user_id", userId)
                .isNull("exit_time")
                .orderByDesc("entry_time")
                .last("LIMIT 1"));

        if (existingLog != null) {
            // 如果已有未出场的记录，更新入场时间和验证方式
            existingLog.setEntryTime(LocalDateTime.now());
            existingLog.setVerifyMode(3); // 3-人工核验
            existingLog.setAdminId(adminId);
            this.baseMapper.updateById(existingLog);
            return true;
        }

        // 如果没有未出场的记录，扣减次数并插入新记录
        // 如果是次卡，扣减次数
        if (template.getType() == 2) {
            if (card.getRemainCount() <= 0) throw new RuntimeException("次数不足");
            card.setRemainCount(card.getRemainCount() - 1);
            memberCardDao.updateById(card);
        }

        // 写入入场日志
        TbEntryLog log = new TbEntryLog();
        log.setUserId(userId);
        log.setUserType(1); // 会员
        log.setEntryTime(LocalDateTime.now());
        log.setVerifyMode(3); // 3-人工核验 (对应任务书场景)
        log.setAdminId(adminId);
        this.baseMapper.insert(log);

        return true;
    }

    /**
     * 3. 执行出场
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmCheckOut(Long userId) {
        // 查找最近一条未出场的记录
        TbEntryLog lastLog = this.baseMapper.selectOne(new QueryWrapper<TbEntryLog>()
                .eq("user_id", userId)
                .isNull("exit_time")
                .orderByDesc("entry_time")
                .last("LIMIT 1"));

        if (lastLog != null) {
            lastLog.setExitTime(LocalDateTime.now());
            this.baseMapper.updateById(lastLog);
        }
        return true;
    }

}