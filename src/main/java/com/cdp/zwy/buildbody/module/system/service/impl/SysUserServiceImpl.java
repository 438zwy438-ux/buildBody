package com.cdp.zwy.buildbody.module.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.cdp.zwy.buildbody.common.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbCardTemplateDao;
import com.cdp.zwy.buildbody.module.business.dao.TbCoachProfileDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberCardDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberProfileDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCardTemplate;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberCard;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.system.controller.DTO.CoachAddDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.CoachRegisterDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.LoginDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.RegisterDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.LoginVO;
import com.cdp.zwy.buildbody.module.system.dao.SysRoleDao;
import com.cdp.zwy.buildbody.module.system.dao.SysUserDao;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.entity.SysRole;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import com.cdp.zwy.buildbody.module.system.entity.SysUserRole;
import com.cdp.zwy.buildbody.module.system.service.SysUserService;
import com.cdp.zwy.buildbody.module.system.service.SysUserRoleService;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统用户表(SysUser)表服务实现类
 *
 * @author makejava
 * @since 2026-02-15 09:14:42
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    @Resource
    private TbMemberProfileDao memberProfileDao; // 注入会员Dao查身份

    @Resource
    private TbCoachProfileDao coachProfileDao;   // 注入教练Dao查身份

    @Resource
    private TbMemberCardDao memberCardDao; // 注入会员Dao查卡信息
    @Resource
    private TbCardTemplateDao cardTemplateDao;
    
    @Resource
    private SysUserRoleService sysUserRoleService;
    
    @Resource
    private SysOrderService sysOrderService;
    
    @Resource
    private SysRoleDao sysRoleDao;
    
    @Resource
    private com.cdp.zwy.buildbody.module.system.service.ImgRelationService imgRelationService;


    /**
     * 备注：一个账号只有一个角色
     * @param loginDTO
     * @return
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        SysUser user = this.getOne(new QueryWrapper<SysUser>().eq("username", loginDTO.getUsername()));
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已停用");
        }

        java.util.List<String> roles = getUserRoles(user.getUserId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getUserId());
        payload.put("roles", roles);
        String token = JwtUtils.createToken(payload);

        LoginVO vo = new LoginVO();
        vo.setUserId(user.getUserId());
        vo.setNickname(user.getNickname());
        vo.setRoles(roles);
        vo.setAvatar(user.getAvatar());
        vo.setToken(token);

        return vo;
    }
    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，任何异常都回滚
    public Boolean registerMember(RegisterDTO dto) {
        // 1. 校验账号/手机号是否重复
        Long count = this.baseMapper.selectCount(new QueryWrapper<SysUser>()
                .eq("username", dto.getUsername()).or().eq("phone", dto.getPhone()));
        if (count > 0) {
            throw new RuntimeException("账号或手机号已存在！");
        }

        // 2. 校验卡种是否存在
        TbCardTemplate cardTemplate = cardTemplateDao.selectById(dto.getCardTemplateId());
        if (cardTemplate == null) {
            throw new RuntimeException("选择的会员卡类型不存在！");
        }

        // 3. 插入系统账号 (sys_user)
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        // 密码加密
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"); // 默认头像
        user.setStatus(1);
        user.setCreateTime(new Date());
        this.baseMapper.insert(user); // 插入后 user.getUserId() 会自动回填

        // 4. 插入会员档案 (tb_member_profile)
        TbMemberProfile profile = new TbMemberProfile();
        profile.setUserId(user.getUserId());
        profile.setRealName(dto.getRealName());
        profile.setGender(dto.getGender());
        profile.setAge(dto.getAge());
        profile.setFaceImgUrl(dto.getFaceImgUrl()); // <--- 关键点：保存 MinIO 返回的 URL
        profile.setBalance(0.0);
        profile.setPoints(0);
        profile.setIsVip(0); // 刚办卡默认是普通会员，买私教课才升VIP
        memberProfileDao.insert(profile);


        // 5. 生成会员卡订单并支付
        Integer cardTimes = 0;
        // 判断会员卡类型，如果是type=2为次卡 将times赋值给remainCount，如果type=1为时间卡不变动
        if (cardTemplate.getType() == 2) { // 次卡
            cardTimes = cardTemplate.getTimes();
        }
        
        Long orderId = sysOrderService.createMemberCardOrder(user.getUserId(), cardTimes, cardTemplate.getPrice().doubleValue());
        // 自动支付订单（实际项目中应该有支付流程）
        sysOrderService.payOrder(orderId);

        // 6. 插入会员卡 (tb_member_card)
        TbMemberCard card = new TbMemberCard();
        card.setUserId(user.getUserId());
        card.setTemplateId(cardTemplate.getId());
        card.setCardNo("NO" + IdUtil.getSnowflakeNextIdStr()); // 生成唯一卡号

        // 计算有效期
        Date now = new Date();
        card.setCreateTime(now);
        card.setActiveTime(now); // 默认立即激活
        // 有效期 = 当前时间 + 卡种天数
        card.setExpireTime(DateUtil.offsetDay(now, cardTemplate.getDurationDays()));

        // 处理次卡逻辑
        if (cardTemplate.getType() == 2) { // 假设2是次卡
            card.setTotalCount(cardTemplate.getTimes());
            card.setRemainCount(cardTemplate.getTimes());
        } else {
            card.setTotalCount(0);
            card.setRemainCount(0);
        }

        card.setStatus(1); // 正常
        memberCardDao.insert(card);

        // 更新订单的cardId
        SysOrder order = sysOrderService.getById(orderId);
        if (order != null) {
            order.setCardId(card.getId());
            sysOrderService.updateById(order);
        }

        // 7. 插入用户角色关系表 (sys_user_role)，角色id 2表示会员
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(2L); // 会员角色ID
        userRole.setRoleCode("user"); // 保持向后兼容
        sysUserRoleService.save(userRole);

        return true;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addCoach(CoachAddDTO dto) {
        // 1. 校验账号防重
        Long count = this.baseMapper.selectCount(new QueryWrapper<SysUser>()
                .eq("username", dto.getUsername()).or().eq("phone", dto.getPhone()));
        if (count > 0) {
            throw new RuntimeException("该教练账号或手机号已存在！");
        }

        // 2. 创建系统账号 (sys_user)
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword())); // 必须加密
        user.setNickname(dto.getRealName() + "教练");
        user.setPhone(dto.getPhone());
        user.setStatus(1);
        this.baseMapper.insert(user);

        // 3. 关联角色 (教练角色 ID 为 3)
         SysUserRole userRole = new SysUserRole();
         userRole.setUserId(user.getUserId());
         userRole.setRoleId(3L); // 教练角色ID
         userRole.setRoleCode("coach"); // 保持向后兼容
         sysUserRoleService.save(userRole);

        // 4. 创建教练档案 (tb_coach_profile)
        TbCoachProfile profile = new TbCoachProfile();
        profile.setUserId(user.getUserId());
        profile.setRealName(dto.getRealName());
        profile.setSpecialty(dto.getSpecialty());
        profile.setIntro(dto.getIntro());
        profile.setStatus(1); // 1在职
        coachProfileDao.insert(profile);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean registerCoach(CoachRegisterDTO dto) {
        // 1. 校验账号/手机号是否重复
        Long count = this.baseMapper.selectCount(new QueryWrapper<SysUser>()
                .eq("username", dto.getUsername()).or().eq("phone", dto.getPhone()));
        if (count > 0) {
            throw new RuntimeException("账号或手机号已存在！");
        }

        // 2. 插入系统账号 (sys_user)
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        user.setStatus(1);
        user.setCreateTime(new Date());
        this.baseMapper.insert(user);

        // 3. 插入教练档案 (tb_coach_profile)
        TbCoachProfile profile = new TbCoachProfile();
        profile.setUserId(user.getUserId());
        profile.setRealName(dto.getRealName());
        profile.setSpecialty(dto.getSpecialty());
        profile.setIntro(dto.getIntro());
        profile.setEntryDate(new Date());
        profile.setStatus(1);
        
        // 证书图片列表转为JSON字符串保存到certificates字段
        if (dto.getCertificates() != null && !dto.getCertificates().isEmpty()) {
            profile.setCertificates(cn.hutool.json.JSONUtil.toJsonStr(dto.getCertificates()));
        }
        
        coachProfileDao.insert(profile);

        // 4. 插入用户角色关系表 (sys_user_role)，角色id 3表示教练
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(3L);
        userRole.setRoleCode("coach");
        sysUserRoleService.save(userRole);

        // 5. 插入教练美照到 img_relation 表
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            imgRelationService.batchSave(2, profile.getId(), dto.getImages());
        }

        return true;
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        // 根据userId查询sys_user_role表获取所有记录
        List<SysUserRole> userRoles = sysUserRoleService.list(
            new QueryWrapper<SysUserRole>().eq("user_id", userId)
        );
        
        if (userRoles.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        // 提取所有role_id并去重
        Set<Long> roleIds = userRoles.stream()
            .map(SysUserRole::getRoleId)
            .filter(Objects::nonNull) // 过滤掉null值
            .collect(Collectors.toSet());
        
        // 批量查询sys_role表获取对应的role_key
        List<SysRole> roles = sysRoleDao.selectBatchIds(roleIds);
        
        // 提取role_key并返回，过滤掉null和空字符串
        List<String> roleKeys = roles.stream()
            .map(SysRole::getRoleKey)
            .filter(roleKey -> roleKey != null && !roleKey.trim().isEmpty()) // 过滤掉null和空字符串
            .collect(Collectors.toList());
        
        return roleKeys;
    }

    @Override
    public Boolean isVip(Long userId) {
        SysOrder vipOrder = sysOrderService.getOne(new QueryWrapper<SysOrder>()
                .eq("user_id", userId)
                .eq("type", 2)
                .eq("status", 1)
                .gt("remain_count", 0)
                .orderByDesc("create_time")
                .last("LIMIT 1"));
        
        if (vipOrder != null && vipOrder.getRemainCount() != null && vipOrder.getRemainCount() > 0) {
            TbMemberProfile profile = memberProfileDao.selectOne(new QueryWrapper<TbMemberProfile>().eq("user_id", userId));
            if (profile != null) {
                profile.setIsVip(1);
                memberProfileDao.updateById(profile);
            }
            return true;
        }
        
        TbMemberProfile profile = memberProfileDao.selectOne(new QueryWrapper<TbMemberProfile>().eq("user_id", userId));
        if (profile != null && profile.getIsVip() == 1) {
            profile.setIsVip(0);
            memberProfileDao.updateById(profile);
        }
        
        return false;
    }

    @Override
    public Boolean logout(Long userId) {
        // 退出登录时，可以在这里做一些清理工作
        // 比如清除服务器端的会话信息、记录日志等
        // 目前只是返回成功，因为主要的信息存储在前端
        return true;
    }
}