package com.cdp.zwy.buildbody.module.business.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.business.entity.TbLocker;

import java.util.List;
import java.util.Map;

/**
 * 更衣室储物柜表(TbLocker)表服务接口
 *
 * @author makejava
 * @since 2026-02-16 09:53:06
 */
public interface TbLockerService extends IService<TbLocker> {
    
    Boolean lockLocker(Long id);
    
    Boolean unlockLocker(Long id);
    
    Boolean useLocker(Long userId, Long lockerId);
    
    Boolean releaseLocker(Long userId);
    
    Map<String, Object> verifyMemberByPhone(String phone, Long currentUserId);
    
    List<TbLocker> getAvailableLockers(String areaCode);
    
    Boolean tempOpenByUserId(Long userId, Long lockerId);
    
    Boolean lockByUserId(Long userId, Long lockerId);
    
    TbLocker getMyLocker(Long userId, String areaCode);
    
    Boolean adminRelease(Long lockerId);
}