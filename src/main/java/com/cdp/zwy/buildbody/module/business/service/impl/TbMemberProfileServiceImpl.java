package com.cdp.zwy.buildbody.module.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.common.utils.AgeCalculator;
import com.cdp.zwy.buildbody.module.business.controller.VO.MemberDetailVO;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberProfileDao;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.business.service.TbMemberProfileService;
import com.cdp.zwy.buildbody.module.system.dao.SysUserDao;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 会员档案表(TbMemberProfile)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:53:44
 */
@Service("tbMemberProfileService")
public class TbMemberProfileServiceImpl extends ServiceImpl<TbMemberProfileDao, TbMemberProfile> implements TbMemberProfileService {

    @Resource
    private SysUserDao sysUserDao;

    @Override
    public boolean updateById(TbMemberProfile entity) {
        // 如果设置了出生日期，自动计算年龄
        if (entity.getBirthDate() != null) {
            int calculatedAge = AgeCalculator.calculateAge(entity.getBirthDate());
            entity.setAge(calculatedAge);
        }
        return super.updateById(entity);
    }

    /**
     * 查询会员详情信息
     * @param userId 用户ID
     * @return 会员详情视图对象
     */
    public MemberDetailVO getMemberDetailByUserId(Long userId) {
        // 查询会员档案
        TbMemberProfile profile = this.getOne(new QueryWrapper<TbMemberProfile>().eq("user_id", userId));
        if (profile == null) {
            return null;
        }

        // 查询用户基础信息
        SysUser user = sysUserDao.selectById(userId);
        if (user == null) {
            return null;
        }

        // 组装VO对象
        MemberDetailVO vo = new MemberDetailVO();
        vo.setId(profile.getId());
        vo.setUserId(profile.getUserId());
        vo.setRealName(profile.getRealName());
        vo.setGender(profile.getGender());
        vo.setAge(profile.getAge());
        vo.setFaceImgUrl(profile.getFaceImgUrl()); // 关键：包含人脸照片URL
        vo.setBalance(profile.getBalance());
        vo.setPoints(profile.getPoints());
        vo.setIsVip(profile.getIsVip());
        
        // 转换Date到LocalDateTime
        if (profile.getVipExpireTime() != null) {
            vo.setVipExpireTime(profile.getVipExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (profile.getBirthDate() != null) {
            vo.setBirthDate(profile.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (profile.getCreateTime() != null) {
            vo.setCreateTime(profile.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (profile.getUpdateTime() != null) {
            vo.setUpdateTime(profile.getUpdateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        // 设置用户基础信息
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());

        return vo;
    }

    @Override
    public boolean updateByUserId(TbMemberProfile tbMemberProfile) {
        // 根据userId查询现有的会员档案
        TbMemberProfile existingProfile = this.getOne(
            new QueryWrapper<TbMemberProfile>().eq("user_id", tbMemberProfile.getUserId())
        );
        
        if (existingProfile == null) {
            // 如果会员档案不存在，创建新的档案
            return this.save(tbMemberProfile);
        }
        
        // 如果设置了出生日期，自动计算年龄
        if (tbMemberProfile.getBirthDate() != null) {
            int calculatedAge = AgeCalculator.calculateAge(tbMemberProfile.getBirthDate());
            tbMemberProfile.setAge(calculatedAge);
        }
        
        // 设置主键ID，然后使用updateById方法
        tbMemberProfile.setId(existingProfile.getId());
        return this.updateById(tbMemberProfile);
    }
}