package com.cdp.zwy.buildbody.module.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.jwt.JWTUtil;
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
import com.cdp.zwy.buildbody.module.system.controller.DTO.LoginDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.RegisterDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.LoginVO;
import com.cdp.zwy.buildbody.module.system.dao.SysUserDao;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import com.cdp.zwy.buildbody.module.system.entity.SysUserRole;
import com.cdp.zwy.buildbody.module.system.service.SysUserService;
import com.cdp.zwy.buildbody.module.system.service.SysUserRoleService;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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


    // 硬编码一个密钥，毕设足够了
    private static final byte[] JWT_KEY = "buildbody_secret_key_2026".getBytes(StandardCharsets.UTF_8);

    /**
     * 备注：一个账号只有一个角色
     * @param loginDTO
     * @return
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 校验账号是否存在
        SysUser user = this.getOne(new QueryWrapper<SysUser>().eq("username", loginDTO.getUsername()));
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        // 2. 校验密码 (数据库是BCrypt加密，所以用checkpw校验)
        // 注意：如果你数据库里的密码是明文 123456，这里会报错。
        // 如果是明文，暂时改成 if (!user.getPassword().equals(loginDTO.getPassword()))
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已停用");
        }

        // 3. 判断角色 (简单粗暴版)
        String role = "UNKNOWN";
        if ("admin".equals(user.getUsername())) {
            role = "ADMIN";
        } else {
            // 查是不是会员
            Long memberCount = memberProfileDao.selectCount(new QueryWrapper<TbMemberProfile>().eq("user_id", user.getUserId()));
            if (memberCount > 0) {
                role = "MEMBER";
            } else {
                // 查是不是教练
                Long coachCount = coachProfileDao.selectCount(new QueryWrapper<TbCoachProfile>().eq("user_id", user.getUserId()));
                if (coachCount > 0) {
                    role = "COACH";
                }
            }
        }

        // 4. 生成 Token (Hutool)
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", user.getUserId());
        payload.put("role", role);
        // 默认过期时间写在 JWTUtil 内部逻辑里或者自己封装，这里简单生成
        String token = JWTUtil.createToken(payload, JWT_KEY);

        // 5. 封装返回
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getUserId());
        vo.setNickname(user.getNickname());
        vo.setRole(role);
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
        userRole.setRoleId(2L); // 角色id 2表示会员
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

        // 3. 关联角色 (假设教练角色 ID 为 3)
         SysUserRole userRole = new SysUserRole();
         userRole.setUserId(user.getUserId());
         userRole.setRoleId(3L);
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
}