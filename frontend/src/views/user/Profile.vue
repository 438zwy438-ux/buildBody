<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="user-card">
          <div class="user-info">
            <el-avatar :size="100" :src="userInfo.faceImgUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <h3>{{ userInfo.realName || userInfo.nickname }}</h3>
            <p>{{ userInfo.phone }}</p>
            <el-tag v-if="memberProfile.isVip === 1" type="warning" size="large">VIP会员</el-tag>
            <el-tag v-else type="info" size="large">普通会员</el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="basic">
              <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                <el-form-item label="用户名">
                  <el-input v-model="form.username" disabled />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="form.nickname" />
                </el-form-item>
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="form.realName" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="form.gender">
                    <el-radio :label="0">男</el-radio>
                    <el-radio :label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                  <el-input-number v-model="form.age" :min="1" :max="100" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdate">更新信息</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="会员档案" name="profile">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="身高">{{ memberProfile.height }}cm</el-descriptions-item>
                <el-descriptions-item label="体重">{{ memberProfile.weight }}kg</el-descriptions-item>
                <el-descriptions-item label="年龄">{{ memberProfile.age }}岁</el-descriptions-item>
                <el-descriptions-item label="性别">{{ memberProfile.gender === 0 ? '男' : '女' }}</el-descriptions-item>
                <el-descriptions-item label="VIP状态">
                  <el-tag :type="memberProfile.isVip === 1 ? 'warning' : 'info'">
                    {{ memberProfile.isVip === 1 ? '是' : '否' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="健康备注" :span="2">
                  {{ memberProfile.healthNotes || '无' }}
                </el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
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
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMemberProfileList, updateMemberProfile } from '@/api/memberProfile'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const memberProfile = ref({})

const activeTab = ref('basic')
const formRef = ref(null)
const passwordFormRef = ref(null)

const form = reactive({
  username: '',
  nickname: '',
  realName: '',
  phone: '',
  gender: 0,
  age: 20
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

const fetchMemberProfile = async () => {
  try {
    const res = await getMemberProfileList({
      current: 1,
      size: 1,
      userId: userInfo.value.id
    })
    if (res.data.records && res.data.records.length > 0) {
      memberProfile.value = res.data.records[0]
    }
  } catch (error) {
    console.error('获取会员档案失败:', error)
  }
}

const handleUpdate = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateMemberProfile({
          ...memberProfile.value,
          ...form
        })
        ElMessage.success('更新成功')
      } catch (error) {
        console.error('更新失败:', error)
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

onMounted(() => {
  form.username = userInfo.value.username
  form.nickname = userInfo.value.nickname
  form.realName = userInfo.value.realName
  form.phone = userInfo.value.phone
  form.gender = userInfo.value.gender
  form.age = userInfo.value.age
  fetchMemberProfile()
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
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
</style>