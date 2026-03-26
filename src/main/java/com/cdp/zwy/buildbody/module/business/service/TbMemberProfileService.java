package com.cdp.zwy.buildbody.module.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.business.controller.VO.MemberDetailVO;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;

/**
 * 会员档案表(TbMemberProfile)表服务接口
 *
 * @author makejava
 * @since 2026-02-16 09:53:44
 */
public interface TbMemberProfileService extends IService<TbMemberProfile> {

    /**
     * 查询会员详情信息
     * @param userId 用户ID
     * @return 会员详情视图对象
     */
    MemberDetailVO getMemberDetailByUserId(Long userId);

    /**
     * 根据用户ID更新会员档案
     * @param tbMemberProfile 会员档案信息
     * @return 是否更新成功
     */
    boolean updateByUserId(TbMemberProfile tbMemberProfile);
}