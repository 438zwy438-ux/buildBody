package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.dao.TbLockerDao;
import com.cdp.zwy.buildbody.module.business.entity.TbLocker;
import com.cdp.zwy.buildbody.module.business.service.TbLockerService;
import com.cdp.zwy.buildbody.module.system.dao.SysUserDao;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberProfileDao;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 更衣室储物柜表(TbLocker)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:53:06
 */
@Service("tbLockerService")
public class TbLockerServiceImpl extends ServiceImpl<TbLockerDao, TbLocker> implements TbLockerService {

    @Resource
    private SysUserDao sysUserDao;

    @Resource
    private TbMemberProfileDao memberProfileDao;

    @Override
    public Boolean lockLocker(Long id) {
        TbLocker locker = this.getById(id);
        if (locker == null) {
            return false;
        }
        locker.setIsLocker(1);
        return this.updateById(locker);
    }

    @Override
    public Boolean unlockLocker(Long id) {
        TbLocker locker = this.getById(id);
        if (locker == null) {
            return false;
        }
        locker.setIsLocker(0);
        return this.updateById(locker);
    }

    @Override
    public Boolean useLocker(Long userId, Long lockerId) {
        TbLocker locker = this.getById(lockerId);
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        if (locker.getStatus() != 0) {
            throw new RuntimeException("储物柜已被占用");
        }

        locker.setCurrentUserId(userId);
        locker.setStatus(1);
        locker.setIsLocker(1);
        return this.updateById(locker);
    }

    @Override
    public Boolean releaseLocker(Long userId) {
        TbLocker locker = this.getOne(new QueryWrapper<TbLocker>().eq("current_user_id", userId));
        if (locker == null) {
            throw new RuntimeException("未找到使用的储物柜");
        }

        locker.setCurrentUserId(null);
        locker.setStatus(0);
        locker.setIsLocker(0);
        return this.updateById(locker);
    }

    @Override
    public Map<String, Object> verifyMemberByPhone(String phone, Long currentUserId) {
        SysUser user = sysUserDao.selectOne(new QueryWrapper<SysUser>().eq("phone", phone));
        if (user == null) {
            throw new RuntimeException("未找到该手机号对应的会员");
        }
        

        
        if (currentUserId != null && !user.getUserId().equals(currentUserId)) {
            throw new RuntimeException("手机号与当前用户不匹配");
        }
        
        TbMemberProfile profile = memberProfileDao.selectOne(
            new QueryWrapper<TbMemberProfile>().eq("user_id", user.getUserId())
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("phone", user.getPhone());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("realName", profile != null ? profile.getRealName() : "");
        result.put("faceImgUrl", profile != null ? profile.getFaceImgUrl() : "");
        result.put("isVip", profile != null ? profile.getIsVip() : 0);
        
        return result;
    }

    @Override
    public List<TbLocker> getAvailableLockers(String areaCode) {
        return this.list(new QueryWrapper<TbLocker>()
                .eq("area_code", areaCode)
                .eq("status", 0)
                .orderByAsc("box_no"));
    }



    @Override
    public Boolean tempOpenByUserId(Long userId, Long lockerId) {
        TbLocker locker = this.getById(lockerId);
        
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        if (!locker.getCurrentUserId().equals(userId)) {
            throw new RuntimeException("只能操作自己使用的储物柜");
        }
        
        locker.setIsLocker(0);
        Boolean b = this.updateById(locker);
        locker.setIsLocker(1);
        this.updateById(locker);
        return b;
    }

    @Override
    public Boolean lockByUserId(Long userId, Long lockerId) {
        TbLocker locker = this.getById(lockerId);
        
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        if (!locker.getCurrentUserId().equals(userId)) {
            throw new RuntimeException("只能操作自己使用的储物柜");
        }
        
        locker.setIsLocker(1);
        return this.updateById(locker);
    }

    @Override
    public TbLocker getMyLocker(Long userId, String areaCode) {
        return this.getOne(new QueryWrapper<TbLocker>()
                .eq("current_user_id", userId)
                .eq("area_code", areaCode));
    }

    @Override
    public Boolean adminRelease(Long lockerId) {
        TbLocker locker = this.getById(lockerId);
        if (locker == null) {
            throw new RuntimeException("储物柜不存在");
        }
        
        locker.setCurrentUserId(null);
        locker.setStatus(0);
        locker.setIsLocker(0);
        return this.updateById(locker);
    }
}