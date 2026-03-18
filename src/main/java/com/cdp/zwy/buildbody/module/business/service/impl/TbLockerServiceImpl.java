package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.dao.TbLockerDao;
import com.cdp.zwy.buildbody.module.business.entity.TbLocker;
import com.cdp.zwy.buildbody.module.business.service.TbLockerService;
import org.springframework.stereotype.Service;

/**
 * 更衣室储物柜表(TbLocker)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:53:06
 */
@Service("tbLockerService")
public class TbLockerServiceImpl extends ServiceImpl<TbLockerDao, TbLocker> implements TbLockerService {

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
}