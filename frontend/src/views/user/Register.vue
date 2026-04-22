<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <el-icon class="logo-icon"><Trophy /></el-icon>
        <h2>健身俱乐部</h2>
        <p>会员注册</p>
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio :label="0">男</el-radio>
            <el-radio :label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="人脸照片" prop="faceImgUrl" required>
          <el-upload
            class="face-uploader"
            action="/api/common/upload"
            :data="{ folder: 'face' }"
            :show-file-list="false"
            :on-success="handleFaceSuccess"
            :before-upload="beforeFaceUpload"
            :headers="{}"
            :auto-upload="true"
          >
            <div class="face-uploader-content">
              <img v-if="registerForm.faceImgUrl" :src="registerForm.faceImgUrl" class="face-image" />
              <div v-else class="face-uploader-placeholder">
                <el-icon><Camera /></el-icon>
                <div class="upload-text">上传本人清晰正面照</div>
                <div class="upload-hint">用于入场核验，请确保照片清晰</div>
              </div>
            </div>
          </el-upload>
          <div class="upload-tip">请上传本人清晰正面照以便入场核验（支持 JPG/PNG 格式，不超过 5MB）</div>
        </el-form-item>
        <el-form-item label="会员卡" prop="cardTemplateId">
          <el-select v-model="registerForm.cardTemplateId" placeholder="请选择会员卡" style="width: 100%" :loading="cardTemplatesLoading">
            <el-option 
              v-for="template in cardTemplates" 
              :key="template.id" 
              :label="`${template.name} - ¥${template.price}`" 
              :value="template.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="selectedTemplate">
          <el-alert :title="`会员卡详情：${selectedTemplate.description || '暂无描述'}`" type="info" :closable="false" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">注册并办卡</el-button>
        </el-form-item>
        <el-form-item>
          <div class="register-footer">
            <span>已有账号?</span>
            <el-link type="primary" @click="goToLogin">立即登录</el-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { register } from '@/api/user'
import { getCardTemplateList } from '@/api/cardTemplate'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)
const cardTemplates = ref([])
const cardTemplatesLoading = ref(false)
const faceUploading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  realName: '',
  gender: 0,
  age: 20,
  faceImgUrl: '',
  cardTemplateId: null
})

const selectedTemplate = computed(() => {
  if (!registerForm.cardTemplateId) return null
  return cardTemplates.value.find(t => t.id === registerForm.cardTemplateId)
})

// 人脸照片上传相关函数
const handleFaceSuccess = (response, uploadFile) => {
  faceUploading.value = false
  console.log('人脸照片上传响应:', response) // 添加调试日志
  
  if (typeof response === 'string') {
    registerForm.faceImgUrl = response
    ElMessage.success('人脸照片上传成功')
  } else if (response.code === 200) {
    registerForm.faceImgUrl = response.data
    ElMessage.success('人脸照片上传成功')
  } else {
    ElMessage.error('人脸照片上传失败')
  }
  
  console.log('当前人脸照片URL:', registerForm.faceImgUrl) // 添加调试日志
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

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  faceImgUrl: [{ required: true, message: '请上传本人清晰正面照以便入场核验', trigger: 'change' }],
  cardTemplateId: [{ required: true, message: '请选择会员卡', trigger: 'change' }]
}

const fetchCardTemplates = async () => {
  cardTemplatesLoading.value = true
  try {
    const res = await getCardTemplateList({
      current: 1,
      size: 100,
      status: 1
    })
    if (res.data.records) {
      cardTemplates.value = res.data.records
    }
  } catch (error) {
    console.error('获取会员卡模板失败:', error)
    ElMessage.error('获取会员卡模板失败')
  } finally {
    cardTemplatesLoading.value = false
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register(registerForm)
        ElMessage.success('注册成功,请登录')

      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/user/login')
}

onMounted(() => {
  fetchCardTemplates()
})
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background-image: url("/images/登录注册背景图.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-box {
  background: white;
  border-radius: 10px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 10px;
}

.register-header h2 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 24px;
}

.register-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.register-footer {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-footer span {
  margin-right: 10px;
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
</style>