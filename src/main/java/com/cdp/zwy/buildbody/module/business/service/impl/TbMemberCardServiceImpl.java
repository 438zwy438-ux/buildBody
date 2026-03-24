package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.controller.DTO.MemberCardVO;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberCardDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCardTemplate;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberCard;
import com.cdp.zwy.buildbody.module.business.service.TbCardTemplateService;
import com.cdp.zwy.buildbody.module.business.service.TbMemberCardService;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("tbMemberCardService")
public class TbMemberCardServiceImpl extends ServiceImpl<TbMemberCardDao, TbMemberCard> implements TbMemberCardService {

    @Resource
    private TbCardTemplateService tbCardTemplateService;

    @Resource
    private SysOrderService sysOrderService;

    @Override
    public List<MemberCardVO> getMyCardsWithDetails(Long userId) {
        QueryWrapper<TbMemberCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        List<TbMemberCard> memberCards = this.list(queryWrapper);

        List<MemberCardVO> result = new ArrayList<>();
        for (TbMemberCard card : memberCards) {
            MemberCardVO vo = new MemberCardVO();
            vo.setId(card.getId());
            vo.setCardNo(card.getCardNo());
            vo.setTemplateId(card.getTemplateId());
            vo.setTotalCount(card.getTotalCount());
            vo.setRemainCount(card.getRemainCount());
            vo.setActiveTime(card.getActiveTime());
            vo.setExpireTime(card.getExpireTime());
            vo.setStatus(card.getStatus());
            vo.setCreateTime(card.getCreateTime());

            TbCardTemplate template = tbCardTemplateService.getById(card.getTemplateId());
            if (template != null) {
                vo.setCardName(template.getName());
                vo.setCardType(template.getType());
            }

            QueryWrapper<SysOrder> orderQuery = new QueryWrapper<>();
            orderQuery.eq("card_id", card.getId());
            orderQuery.eq("type", 1);
            orderQuery.orderByDesc("create_time");
            orderQuery.last("LIMIT 1");
            SysOrder order = sysOrderService.getOne(orderQuery);
            if (order != null) {
                vo.setOrderNo(order.getOrderNo());
                vo.setTotalAmount(order.getTotalAmount());
                vo.setPayTime(order.getPayTime());
            }

            result.add(vo);
        }
        return result;
    }
}