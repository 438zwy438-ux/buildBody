<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <el-icon class="logo-icon"><Trophy /></el-icon>
        <h2>健身俱乐部</h2>
        <p>教练注册</p>
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="100px">
        <el-divider content-position="left">账号信息</el-divider>
        
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-divider content-position="left">教练信息</el-divider>
        
        <el-form-item label="特长标签" prop="specialty">
          <el-input v-model="registerForm.specialty" placeholder="请输入特长标签，如：减脂,增肌" />
        </el-form-item>
        <el-form-item label="个人简介" prop="intro">
          <el-input v-model="registerForm.intro" type="textarea" :rows="4" placeholder="请输入个人简介" />
        </el-form-item>
        
        <el-divider content-position="left">证书图片</el-divider>
        
        <el-form-item label="证书图片" prop="certificates">
          <el-upload
            v-model:file-list="certificateFileList"
            action="/api/common/upload"
            list-type="picture-card"
            :data="{ folder: 'certificate' }"
            :on-success="handleCertificateSuccess"
            :on-remove="handleCertificateRemove"
            :before-upload="beforeUpload"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">请上传教练证书图片（支持 JPG/PNG 格式，不超过 5MB）</div>
        </el-form-item>
        
        <el-divider content-position="left">教练美照</el-divider>
        
        <el-form-item label="教练美照" prop="images">
          <el-upload
            v-model:file-list="imageFileList"
            action="/api/common/upload"
            list-type="picture-card"
            :data="{ folder: 'coach' }"
            :on-success="handleImageSuccess"
            :on-remove="handleImageRemove"
            :before-upload="beforeUpload"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">请上传教练美照（支持 JPG/PNG 格式，不超过 5MB）</div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">注册</el-button>
        </el-form-item>
        <el-form-item>
          <div class="register-footer">
            <span>返回教练档案管理</span>
            <el-link type="primary" @click="goToCoachProfiles">点击返回</el-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerCoach } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Plus, Trophy } from '@element-plus/icons-vue'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const certificateFileList = ref([])
const imageFileList = ref([])

const registerForm = reactive({
  username: '',
  password: '',
  phone: '',
  realName: '',
  specialty: '',
  intro: '',
  certificates: [],
  images: []
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入特长标签', trigger: 'blur' }],
  intro: [{ required: true, message: '请输入个人简介', trigger: 'blur' }]
}

const beforeUpload = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  const isLt5M = rawFile.size / 1024 / 1024 < 5
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

const handleCertificateSuccess = (response, file, fileList) => {
  console.log('证书图片上传响应:', response)
  if (response.code === 200) {
    registerForm.certificates.push(response.data)
    ElMessage.success('证书图片上传成功')
  } else {
    ElMessage.error(response.msg || '证书图片上传失败')
  }
}

const handleCertificateRemove = (file, fileList) => {
  const index = registerForm.certificates.indexOf(file.url)
  if (index > -1) {
    registerForm.certificates.splice(index, 1)
  }
}

const handleImageSuccess = (response, file, fileList) => {
  console.log('教练美照上传响应:', response)
  if (response.code === 200) {
    registerForm.images.push(response.data)
    ElMessage.success('教练美照上传成功')
  } else {
    ElMessage.error(response.msg || '教练美照上传失败')
  }
}

const handleImageRemove = (file, fileList) => {
  const index = registerForm.images.indexOf(file.url)
  if (index > -1) {
    registerForm.images.splice(index, 1)
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await registerCoach(registerForm)
        ElMessage.success('教练注册成功')
        goToCoachProfiles()
      } catch (error) {
        console.error('教练注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const goToCoachProfiles = () => {
  router.push('/admin/coach-profiles')
}
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
  width: 600px;
  max-width: 100%;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
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

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  line-height: 1.4;
}

:deep(.el-divider__text) {
  font-weight: 500;
  color: #667eea;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>