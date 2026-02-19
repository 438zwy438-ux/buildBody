package com.cdp.zwy.buildbody.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.system.controller.VO.MemberCheckVO;
import com.cdp.zwy.buildbody.module.system.entity.TbEntryLog;

import java.util.List;

/**
 * 进出场记录表(TbEntryLog)表服务接口
 *
 * @author makejava
 * @since 2026-02-19 09:46:37
 */
/**
 * 1. 搜索会员并校验入场资格
 */
public interface TbEntryLogService extends IService<TbEntryLog> {
    List<MemberCheckVO> searchMemberForCheckIn(String phone);
    Boolean confirmCheckIn(Long userId, Long adminId);
    Boolean confirmCheckOut(Long userId);
}