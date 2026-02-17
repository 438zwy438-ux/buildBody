package com.cdp.zwy.buildbody.module.system.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbCoachProfileDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberProfileDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.system.controller.DTO.LoginDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.LoginVO;
import com.cdp.zwy.buildbody.module.system.dao.SysUserDao;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import com.cdp.zwy.buildbody.module.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
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

    // 硬编码一个密钥，毕设足够了
    private static final byte[] JWT_KEY = "buildbody_secret_key_2026".getBytes(StandardCharsets.UTF_8);

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
}
