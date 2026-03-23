-- 查看所有用户的角色分配
USE build_body;

SELECT sur.user_id, su.username, su.nickname, sur.role_id, sr.role_name, sr.role_key
FROM sys_user_role sur
LEFT JOIN sys_role sr ON sur.role_id = sr.role_id
LEFT JOIN sys_user su ON sur.user_id = su.user_id
ORDER BY sur.user_id;

-- 查看tb_member_profile表中的用户
SELECT * FROM tb_member_profile;

-- 查看tb_coach_profile表中的用户
SELECT * FROM tb_coach_profile;

-- 检查用户ID为2024852867898269698的详细信息（从之前的查询看到这个人既是教练又是VIP）
SELECT sur.user_id, su.username, su.nickname, sur.role_id, sr.role_name, sr.role_key, sur.role_code
FROM sys_user_role sur
LEFT JOIN sys_role sr ON sur.role_id = sr.role_id
LEFT JOIN sys_user su ON sur.user_id = su.user_id
WHERE sur.user_id = 2024852867898269698;