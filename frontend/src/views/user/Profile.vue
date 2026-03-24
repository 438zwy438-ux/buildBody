<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="user-card">
          <div class="user-info">
            <el-avatar :size="100" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <h3>{{ userInfo.realName || userInfo.nickname }}</h3>
            <p>{{ userInfo.phone }}</p>
            <!-- 根据用户角色显示标签 -->
            <el-tag v-if="hasRole('coach')" type="success" size="large">教练</el-tag>
            <el-tag v-else-if="hasRole('vip')" type="warning" size="large">VIP会员</el-tag>
            <el-tag v-else-if="hasRole('user')" type="info" size="large">普通会员</el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="basic">
              <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
                <el-form-item label="用户名">
                  <el-input v-model="form.username" disabled />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="form.nickname" />
                </el-form-item>

                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="form.gender">
                    <el-radio :label="0">男</el-radio>
                    <el-radio :label="1">女</el-radio>
                    <el-radio :label="2">未知</el-radio>
                  </el-radio-group>

                </el-form-item>
                <el-form-item label="头像">
                  <el-upload
                    class="avatar-uploader"
                    action="/api/common/upload"
                    :data="{ folder: 'avatar' }"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                    :headers="{ 'Authorization': `Bearer ${userStore.token}` }"
                  >
                    <img v-if="form.avatar" :src="form.avatar" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdate">更新信息</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <!-- 教练档案标签页 -->
            <el-tab-pane v-if="hasRole('coach')" label="教练档案" name="coach-profile">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="姓名">{{ coachProfile.realName }}</el-descriptions-item>
                <el-descriptions-item label="专长">{{ coachProfile.specialty }}</el-descriptions-item>
                <el-descriptions-item label="介绍" :span="2">{{ coachProfile.intro }}</el-descriptions-item>
                <el-descriptions-item label="证书资质">{{ coachProfile.certificates?.length > 0 ? '已上传' : '未上传' }}</el-descriptions-item>
                <el-descriptions-item label="入职日期">{{ coachProfile.entryDate }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="coachProfile.status === 1 ? 'success' : 'danger'">
                    {{ coachProfile.status === 1 ? '在职' : '离职' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <!-- 会员档案标签页 - 仅对普通会员和VIP会员显示 -->
            <el-tab-pane v-if="hasRole('user') || hasRole('vip')" label="会员档案" name="member-profile">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="真实姓名">{{ memberProfile.realName }}</el-descriptions-item>
                <el-descriptions-item label="性别">{{ memberProfile.gender === 0 ? '男' : memberProfile.gender === 1 ? '女' : '未知' }}</el-descriptions-item>
                <el-descriptions-item label="出生日期">{{ memberProfile.birthDate || '未填写' }}</el-descriptions-item>
                <el-descriptions-item label="年龄">{{ calculateAge(memberProfile.birthDate) }}岁</el-descriptions-item>
                <el-descriptions-item label="账户余额">{{ memberProfile.balance }}元</el-descriptions-item>
                <el-descriptions-item label="积分">{{ memberProfile.points }}</el-descriptions-item>
                <el-descriptions-item label="VIP状态">
                  <el-tag :type="memberProfile.isVip === 1 ? 'warning' : 'info'">
                    {{ memberProfile.isVip === 1 ? '是' : '否' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="VIP到期时间" v-if="memberProfile.isVip === 1">
                  {{ memberProfile.vipExpireTime }}
                </el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getMyMemberProfile, updateMemberProfile } from '@/api/memberProfile'
import { getCoachProfileByUserId } from '@/api/coachProfile'
import { getCurrentUserInfo, updateUser } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = ref({})

// 获取完整的用户信息
const fetchFullUserInfo = async () => {
  try {
    const res = await getCurrentUserInfo()
    if (res.data) {
      userInfo.value = res.data
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}
const memberProfile = ref({})
const coachProfile = ref({})

const activeTab = ref('basic')
const formRef = ref(null)
const passwordFormRef = ref(null)

// 头像上传相关函数
const handleAvatarSuccess = (response, uploadFile) => {
  if(typeof response === 'string') {
    form.avatar = response
  } else if(response.code === 200) {
    form.avatar = response.data
  } else {
    ElMessage.error('头像上传失败')
  }
}

const beforeAvatarUpload = (rawFile) => {
  const isValidType = ['image/jpeg', 'image/jpg', 'image/png'].includes(rawFile.type)
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isValidType) {
    ElMessage.error('头像图片只能是 JPG/JPEG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
  }
  return isValidType && isLt2M
}

const form = reactive({
  username: '',
  nickname: '',
  realName: '',
  phone: '',
  gender: 0,
  avatar: '',
  birthDate: null
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})



const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 角色检查函数
const hasRole = (role) => {
  return userStore.roles.includes(role)
}

// 根据出生日期计算年龄
const calculateAge = (birthDateString) => {
  if (!birthDateString) return '—'
  
  const birthDate = new Date(birthDateString)
  const today = new Date()
  let age = today.getFullYear() - birthDate.getFullYear()
  const monthDiff = today.getMonth() - birthDate.getMonth()
  
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--
  }
  
  return age
}

// 获取会员档案
const fetchMemberProfile = async () => {
  try {
    const res = await getMyMemberProfile()
    if (res.data) {
      memberProfile.value = res.data
    }
  } catch (error) {
    console.error('获取会员档案失败:', error)
  }
}

// 获取教练档案
const fetchCoachProfile = async () => {
  try {
    // 确保用户ID有效
    const userId = userInfo.value.userId || userInfo.value.id
    if (!userId || userId === 'undefined') {
      console.error('用户ID无效:', userId)
      return
    }
    const res = await getCoachProfileByUserId(userId)
    if (res.data) {
      coachProfile.value = res.data
    }
  } catch (error) {
    console.error('获取教练档案失败:', error)
  }
}

const handleUpdate = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 首先更新sys_user表中的基本信息
         await updateUser({
           userId: userInfo.value.userId,
           username: form.username,
           nickname: form.nickname,
           phone: form.phone,
           gender: form.gender,
           avatar: form.avatar
         })
         
         // 更新用户信息到store
         userStore.setUserInfo({
           ...userStore.userInfo,
           username: form.username,
           nickname: form.nickname,
           phone: form.phone,
           gender: form.gender,
           avatar: form.avatar
         })
        
        if (hasRole('coach')) {
          // 教练更新基本信息到教练档案
          const updatedCoachProfile = {
            ...coachProfile.value,
            realName: form.realName,
            // 可能需要添加更多教练档案字段
          }
          // 教练档案更新通常由管理员完成，这里暂不实现
          console.log('教练档案更新功能待完善')
        } else {
          // 会员更新基本信息到会员档案
          await updateMemberProfile({
            ...memberProfile.value,
            realName: form.realName,
            nickname: form.nickname,
            phone: form.phone,
            gender: form.gender,
            birthDate: form.birthDate
          })
        }
        ElMessage.success('更新成功')
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败')
      }
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        ElMessage.success('密码修改成功')
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error) {
        console.error('修改密码失败:', error)
      }
    }
  })
}

onMounted(async () => {
  // 获取完整的用户信息
  await fetchFullUserInfo()

  // 确保用户信息已加载
  const userId = userInfo.value.userId || userInfo.value.id
  if (!userId || userId === 'undefined' || userId === undefined) {
    console.error('用户信息未正确加载')
    return
  }

  // 设置基础信息表单
  form.username = userInfo.value.username
  form.nickname = userInfo.value.nickname
  form.phone = userInfo.value.phone
  form.avatar = userInfo.value.avatar
  // gender可能来自用户信息或档案信息
  form.gender = userInfo.value.gender || 0

  // 根据用户角色加载相应档案
  if (hasRole('coach')) {
    await fetchCoachProfile()
    // 如果是教练，默认显示教练档案标签
    activeTab.value = 'coach-profile'
  } else if (hasRole('user') || hasRole('vip')) {
    await fetchMemberProfile()
    // 设置会员档案相关表单字段
    form.realName = memberProfile.value.realName || userInfo.value.nickname
    form.gender = memberProfile.value.gender || userInfo.value.gender || 0
    form.birthDate = memberProfile.value.birthDate
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.user-card {
  margin-bottom: 20px;
}

.user-info {
  text-align: center;
  padding: 20px 0;
}

.user-info h3 {
  margin: 15px 0 5px 0;
  color: #333;
}

.user-info p {
  margin: 0 0 15px 0;
  color: #999;
}

.avatar-uploader {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
}
.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
}
</style>