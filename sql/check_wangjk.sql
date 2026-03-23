-- 查找王俊凯的用户ID和角色
USE build_body;

SELECT * FROM sys_user WHERE nickname LIKE '%王俊凯%' OR username LIKE '%王俊凯%';

-- 查看所有用户的角色分配
SELECT sur.user_id, su.username, su.nickname, sur.role_id, sr.role_name, sr.role_key
FROM sys_user_role sur
LEFT JOIN sys_role sr ON sur.role_id = sr.role_id
LEFT JOIN sys_user su ON sur.user_id = su.user_id
ORDER BY sur.user_id;

-- 查看王俊凯的详细信息
SELECT sur.user_id, su.username, su.nickname, sur.role_id, sr.role_name, sr.role_key, sur.role_code
FROM sys_user_role sur
LEFT JOIN sys_role sr ON sur.role_id = sr.role_id
LEFT JOIN sys_user su ON sur.user_id = su.user_id
WHERE su.nickname LIKE '%王俊凯%' OR su.username LIKE '%王俊凯%';

-- 查看tb_member_profile表中是否有王俊凯的信息
SELECT * FROM tb_member_profile WHERE user_id IN (
    SELECT user_id FROM sys_user_role WHERE role_id = 3
);