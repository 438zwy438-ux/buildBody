package com.cdp.zwy.buildbody.module.business.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.business.controller.DTO.MemberCardVO;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberCard;

import java.util.List;


public interface TbMemberCardService extends IService<TbMemberCard> {

    List<MemberCardVO> getMyCardsWithDetails(Long userId);

}