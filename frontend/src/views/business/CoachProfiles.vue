<template>
  <div class="coach-profiles-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教练档案管理</span>
          <el-button type="primary" @click="handleAdd">添加教练</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="教练姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入教练姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="specialty" label="专长" />
        <el-table-column label="图片" width="150">
          <template #default="{ row }">
            <div v-if="row.images && row.images.length > 0" class="image-preview">
              <el-image
                v-for="(img, index) in row.images.slice(0, 3)"
                :key="index"
                :src="img"
                :preview-src-list="row.images"
                style="width: 40px; height: 40px; margin-right: 5px"
                fit="cover"
              />
              <span v-if="row.images.length > 3" style="font-size: 12px; color: #999">+{{ row.images.length - 3 }}</span>
            </div>
            <span v-else style="color: #999; font-size: 12px">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="intro" label="简介" show-overflow-tooltip />
        <el-table-column prop="entryDate" label="入职日期" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '在职' : '离职' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="专长" prop="specialty">
          <el-input v-model="form.specialty" placeholder="请输入专长，多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="简介" prop="intro">
          <el-input v-model="form.intro" type="textarea" :rows="4" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="入职日期" prop="entryDate">
          <el-date-picker
            v-model="form.entryDate"
            type="date"
            placeholder="选择入职日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="教练照片">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadUrl"
            list-type="picture-card"
            :data="{ coachId: form.id }"
            :on-preview="handlePicturePreview"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            :on-change="handleChange"
            :auto-upload="true"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <el-dialog v-model="previewVisible" title="图片预览" width="600px">
            <img :src="previewImage" style="width: 100%" />
          </el-dialog>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCoachList, addCoach, updateCoach, deleteCoach, deleteCoachImageByUrl } from '@/api/coach'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加教练')
const formRef = ref(null)
const tableData = ref([])
const fileList = ref([])
const previewVisible = ref(false)
const previewImage = ref('')
const uploadUrl = ref('/api/upload/coach/')
const uploadedImageUrls = ref([])

const searchForm = reactive({
  realName: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  userId: null,
  realName: '',
  specialty: '',
  intro: '',
  entryDate: '',
  status: 1
})

const rules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入专长', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size
    }
    if (searchForm.realName) {
      params.realName = searchForm.realName
    }
    const res = await getCoachList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取教练列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.realName = ''
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '添加教练'
  form.id = null
  form.userId = null
  form.realName = ''
  form.specialty = ''
  form.intro = ''
  form.entryDate = ''
  form.status = 1
  fileList.value = []
  uploadedImageUrls.value = []
  uploadUrl.value = '/api/upload/coach/'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  console.log('编辑教练数据:', row)
  console.log('row.images:', row.images)
  dialogTitle.value = '编辑教练'
  form.id = row.id
  form.userId = row.userId
  form.realName = row.realName
  form.specialty = row.specialty
  form.intro = row.intro
  form.entryDate = row.entryDate
  form.status = row.status
  
  fileList.value = []
  uploadedImageUrls.value = []
  if (row.images && row.images.length > 0) {
    console.log('加载图片列表:', row.images)
    row.images.forEach((imgUrl, index) => {
      fileList.value.push({
        name: `image-${index}.jpg`,
        url: imgUrl,
        uid: Date.now() + index,
        status: 'success'
      })
      uploadedImageUrls.value.push(imgUrl)
    })
  } else {
    console.log('没有找到 images 字段或为空')
  }
  
  uploadUrl.value = `/api/upload/coach/${row.id}`
  dialogVisible.value = true
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  
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

const handleChange = (file, fileList) => {
  console.log('文件状态变化:', file.status, fileList)
}

const handleUploadSuccess = (response, file, fileList) => {
  console.log('上传成功:', response, file, fileList)
  if (response.code === 200) {
    ElMessage.success('上传成功')
    const imageUrl = response.data
    if (imageUrl) {
      file.url = imageUrl
      uploadedImageUrls.value.push(imageUrl)
    }
    
    if (!form.id) {
      const newId = response.data?.coachId
      if (newId) {
        form.id = newId
        uploadUrl.value = `/api/upload/coach/${newId}`
      }
    }
  } else {
    ElMessage.error(response.msg || '上传失败')
    const index = fileList.indexOf(file)
    if (index > -1) {
      fileList.splice(index, 1)
    }
  }
}

const handleRemove = async (file) => {
  const index = uploadedImageUrls.value.indexOf(file.url)
  if (index > -1) {
    uploadedImageUrls.value.splice(index, 1)
  }
  
  try {
    await deleteCoachImageByUrl(file.url)
  } catch (error) {
    console.error('删除图片失败:', error)
  }
}

const handlePicturePreview = (file) => {
  previewImage.value = file.url
  previewVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该教练档案吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCoach([row.id])
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.log('取消删除')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateCoach(form)
        } else {
          await addCoach(form)
        }
        ElMessage.success(form.id ? '更新成功' : '添加成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('提交失败:', error)
      }
    }
  })
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.el-icon--upload {
  font-size: 67px;
  color: #409eff;
  margin-bottom: 16px;
}
</style>