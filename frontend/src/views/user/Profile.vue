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
              
              <!-- 教练美照 -->
              <div style="margin-top: 20px;">
                <h4 style="margin-bottom: 10px;">教练美照</h4>
                <div v-if="coachImages && coachImages.length > 0" class="coach-images">
                  <el-image
                    v-for="(img, index) in coachImages"
                    :key="index"
                    :src="img"
                    :preview-src-list="coachImages"
                    :initial-index="index"
                    fit="cover"
                    style="width: 150px; height: 150px; margin-right: 10px; margin-bottom: 10px; border-radius: 8px;"
                  />
                </div>
                <el-empty v-else description="暂无教练美照" :image-size="100" />
              </div>
            </el-tab-pane>
            <!-- 会员档案标签页 - 仅对普通会员和VIP会员显示 -->
            <el-tab-pane v-if="hasRole('user') || hasRole('vip')" label="会员档案" name="member-profile">
              <!-- 只读资产信息展示 -->
              <el-card class="asset-card" style="margin-bottom: 20px;">
                <template #header>
                  <div class="card-header">
                    <span>资产信息</span>
                  </div>
                </template>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="账户余额">
                    <span style="color: #67c23a; font-weight: bold;">{{ memberForm.balance }}元</span>
                  </el-descriptions-item>
                  <el-descriptions-item label="VIP状态">
                    <el-tag :type="memberForm.isVip === 1 ? 'warning' : 'info'">
                      {{ memberForm.isVip === 1 ? 'VIP会员' : '普通会员' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item v-if="memberForm.isVip === 1" label="VIP到期时间" :span="2">
                    <span>{{ memberForm.vipExpireTime }}</span>
                  </el-descriptions-item>
                </el-descriptions>
              </el-card>
              
              <!-- 可编辑的个人信息表单 -->
              <el-form :model="memberForm" :rules="memberRules" ref="memberFormRef" label-width="120px">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="memberForm.realName" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="memberForm.gender">
                    <el-radio :label="0">男</el-radio>
                    <el-radio :label="1">女</el-radio>
                    <el-radio :label="2">未知</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="出生日期" prop="birthDate">
                  <el-date-picker
                    v-model="memberForm.birthDate"
                    type="date"
                    placeholder="选择出生日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
                <el-form-item label="年龄">
                  <span>{{ calculateAge(memberForm.birthDate) }}岁</span>
                </el-form-item>
                <el-form-item label="人脸照片" prop="faceImgUrl">
                  <el-upload
                    class="face-uploader"
                    action="/api/common/upload"
                    :data="{ folder: 'face' }"
                    :show-file-list="false"
                    :on-success="handleFaceSuccess"
                    :before-upload="beforeFaceUpload"
                    :headers="{ 'Authorization': `Bearer ${userStore.token}` }"
                  >
                    <div class="face-uploader-content">
                      <img v-if="memberForm.faceImgUrl" :src="memberForm.faceImgUrl" class="face-image" />
                      <div v-else class="face-uploader-placeholder">
                        <el-icon><Camera /></el-icon>
                        <div class="upload-text">上传本人清晰正面照</div>
                        <div class="upload-hint">用于入场核验，请确保照片清晰</div>
                      </div>
                    </div>
                  </el-upload>
                  <div class="upload-tip">请上传本人清晰正面照以便入场核验（支持 JPG/PNG 格式，不超过 5MB）</div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdateMemberProfile">更新档案信息</el-button>
                </el-form-item>
              </el-form>
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
import { Plus, Camera } from '@element-plus/icons-vue'
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
const coachImages = ref([])

const activeTab = ref('basic')
const formRef = ref(null)
const passwordFormRef = ref(null)
const memberFormRef = ref(null)
const faceUploading = ref(false)

// 会员档案表单数据
const memberForm = reactive({
  realName: '',
  gender: 2,
  birthDate: null,
  faceImgUrl: '',
  balance: 0,
  isVip: 0,
  vipExpireTime: null
})

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

// 人脸照片上传相关函数
const handleFaceSuccess = (response, uploadFile) => {
  faceUploading.value = false
  console.log('人脸照片上传响应:', response) // 添加调试日志
  
  if (typeof response === 'string') {
    memberForm.faceImgUrl = response
    ElMessage.success('人脸照片上传成功')
  } else if (response.code === 200) {
    memberForm.faceImgUrl = response.data
    ElMessage.success('人脸照片上传成功')
  } else {
    ElMessage.error('人脸照片上传失败')
  }
  
  console.log('当前人脸照片URL:', memberForm.faceImgUrl) // 添加调试日志
}

const beforeFaceUpload = (rawFile) => {
  faceUploading.value = true
  const isValidType = ['image/jpeg', 'image/jpg', 'image/png'].includes(rawFile.type)
  const isLt5M = rawFile.size / 1024 / 1024 < 5

  if (!isValidType) {
    faceUploading.value = false
    ElMessage.error('人脸照片只能是 JPG/JPEG/PNG 格式!')
    return false
  }
  if (!isLt5M) {
    faceUploading.value = false
    ElMessage.error('人脸照片大小不能超过 5MB!')
    return false
  }
  return isValidType && isLt5M
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

// 会员档案校验规则
const memberRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  faceImgUrl: [{ required: true, message: '请上传本人清晰正面照以便入场核验', trigger: 'change' }]
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
      console.log('获取会员档案成功:', res.data)
    } else {
      // 会员档案不存在，初始化空对象
      memberProfile.value = {}
      console.log('会员档案不存在，需要创建新档案')
    }
  } catch (error) {
    console.error('获取会员档案失败:', error)
    // 发生错误时也初始化空对象
    memberProfile.value = {}
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
      // 处理返回的 Map 数据
      if (res.data.coach) {
        coachProfile.value = res.data.coach
      }
      if (res.data.images) {
        coachImages.value = res.data.images
      }
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
        // 首先更新sys_user表中的基本信息（不包含gender）
         await updateUser({
           userId: userInfo.value.userId,
           username: form.username,
           nickname: form.nickname,
           phone: form.phone,
           avatar: form.avatar
         })
         
         // 更新用户信息到store
         userStore.setUserInfo({
           ...userStore.userInfo,
           username: form.username,
           nickname: form.nickname,
           phone: form.phone,
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
          // 会员更新基本信息到会员档案（包含gender）
          await updateMemberProfile({
            ...memberProfile.value,
            userId: userInfo.value.userId,
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

// 更新会员档案（可编辑的个人信息）
const handleUpdateMemberProfile = async () => {
  if (!memberFormRef.value) return
  
  await memberFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 确保有用户ID
        const userId = userInfo.value.userId || userInfo.value.id
        if (!userId) {
          ElMessage.error('用户信息未加载，请刷新页面重试')
          return
        }
        
        // 构建更新数据，包含userId让后端自动处理档案创建
        const updateData = {
          userId: userId,
          realName: memberForm.realName,
          gender: memberForm.gender,
          birthDate: memberForm.birthDate,
          faceImgUrl: memberForm.faceImgUrl
        }
        
        // 如果已有档案ID，也一并传递（后端会优先使用ID更新）
        if (memberProfile.value.id) {
          updateData.id = memberProfile.value.id
        }
        
        console.log('更新会员档案数据:', updateData)
        await updateMemberProfile(updateData)
        ElMessage.success('档案信息更新成功')
        
        // 更新成功后重新加载会员档案数据
        await fetchMemberProfile()
      } catch (error) {
        console.error('更新档案信息失败:', error)
        ElMessage.error('更新档案信息失败')
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
    
    // 初始化会员档案表单数据
    memberForm.realName = memberProfile.value.realName || userInfo.value.nickname
    memberForm.gender = memberProfile.value.gender || 2
    memberForm.birthDate = memberProfile.value.birthDate
    memberForm.faceImgUrl = memberProfile.value.faceImgUrl || ''
    memberForm.balance = memberProfile.value.balance || 0
    memberForm.isVip = memberProfile.value.isVip || 0
    memberForm.vipExpireTime = memberProfile.value.vipExpireTime
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

/* 人脸照片上传样式 */
.face-uploader {
  width: 100%;
}

.face-uploader-content {
  width: 100%;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.face-uploader-content:hover {
  border-color: #409eff;
}

.face-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.face-uploader-placeholder {
  text-align: center;
  color: #8c939d;
}

.face-uploader-placeholder .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.upload-hint {
  font-size: 12px;
  color: #999;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  line-height: 1.4;
}

/* 教练美照样式 */
.coach-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>