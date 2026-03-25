<template>
  <div class="courses-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="handleAdd">添加课程</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.name" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="searchForm.type" placeholder="请选择课程类型" clearable style="width: 150px">
            <el-option label="私教" :value="1" />
            <el-option label="团课" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
9        <el-table-column label="封面图" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImg"
              :src="row.coverImg"
              :preview-src-list="[row.coverImg]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px"
            />
            <span v-else style="color: #909399; font-size: 12px">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="type" label="课程类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'warning'">{{ row.type === 1 ? '私教' : '团课' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="courseTimes" label="服务次数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ row.createTime ? dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">私教</el-radio>
            <el-radio :label="2">团课</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="关联教练" prop="coachUserId">
          <el-select v-model="form.coachUserId" placeholder="请选择教练（私教课必选）" clearable style="width: 100%">
            <el-option
              v-for="coach in coachList"
              :key="coach.id"
              :label="coach.realName"
              :value="coach.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单次时长(分钟)" prop="duration">
          <el-input-number v-model="form.duration" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="课程服务次数" prop="courseTimes">
          <el-input-number v-model="form.courseTimes" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImg">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadUrl"
            list-type="picture-card"
            :on-preview="handlePicturePreview"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            :auto-upload="true"
            :limit="1"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png文件，且不超过5MB
              </div>
            </template>
          </el-upload>
          <el-dialog v-model="previewVisible" title="图片预览" width="600px">
            <img :src="previewImage" style="width: 100%" />
          </el-dialog>
        </el-form-item>
        <el-form-item label="封面图URL" prop="coverImg" style="display: none">
          <el-input v-model="form.coverImg" />
        </el-form-item>
        <el-form-item label="课程详情" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入课程详情" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
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
import { Plus } from '@element-plus/icons-vue'
import { getCourseList, createCourse, updateCourse, deleteCourse } from '@/api/course'
import { getCoachProfileList } from '@/api/coachProfile'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加课程')
const formRef = ref(null)
const tableData = ref([])
const fileList = ref([])
const previewVisible = ref(false)
const previewImage = ref('')
const uploadUrl = ref('/api/upload/course/')
const tempCourseId = ref(null)
const coachList = ref([])

const searchForm = reactive({
  name: '',
  type: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  type: 1,
  coachUserId: null,
  price: 0,
  duration: 60,
  courseTimes: 1,
  coverImg: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  price: [{ required: true, message: '请输入课程单价', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入单次时长', trigger: 'blur' }],
  courseTimes: [{ required: true, message: '请输入课程服务次数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchCoachList = async () => {
  try {
    const res = await getCoachProfileList({ current: 1, size: 1000 })
    coachList.value = res.data?.records || []
  } catch (error) {
    console.error('获取教练列表失败:', error)
    coachList.value = []
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size
    }
    if (searchForm.name) {
      params.name = searchForm.name
    }
    if (searchForm.type) {
      params.type = searchForm.type
    }
    const res = await getCourseList(params)
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } catch (error) {
    console.error('获取课程列表失败:', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.type = null
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '添加课程'
  form.id = null
  form.name = ''
  form.type = 1
  form.coachUserId = null
  form.price = 0
  form.duration = 60
  form.courseTimes = 1
  form.coverImg = ''
  form.description = ''
  form.status = 1
  fileList.value = []
  uploadUrl.value = '/api/upload/course/'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑课程'
  form.id = row.id
  form.name = row.name
  form.type = row.type
  form.coachUserId = row.coachUserId
  form.price = row.price
  form.duration = row.duration
  form.courseTimes = row.courseTimes
  form.coverImg = row.coverImg
  form.description = row.description
  form.status = row.status
  
  fileList.value = []
  if (row.coverImg) {
    fileList.value = [{
      name: '封面图',
      url: row.coverImg
    }]
  }
  uploadUrl.value = `/api/upload/course/${row.id}`
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCourse([row.id])
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
        const submitData = {
          id: form.id,
          name: form.name,
          type: Number(form.type),
          coachUserId: form.coachUserId,
          price: Number(form.price).toFixed(2),
          duration: Number(form.duration),
          courseTimes: Number(form.courseTimes),
          coverImg: form.coverImg,
          description: form.description,
          status: Number(form.status)
        }
        
        if (form.id) {
          await updateCourse(submitData)
          ElMessage.success('更新成功')
        } else {
          await createCourse(submitData)
          ElMessage.success('创建成功')
        }
        
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

const handlePicturePreview = (file) => {
  previewImage.value = file.url
  previewVisible.value = true
}

const handleUploadSuccess = (response, file, fileList) => {
  console.log('上传成功:', response, file, fileList)
  if (response.code === 200) {
    ElMessage.success('上传成功')
    const imageUrl = response.data
    if (imageUrl) {
      form.coverImg = imageUrl
      file.url = imageUrl
      
      // 立即更新数据库中的cover_img字段
      if (form.id) {
        updateCourse({
          id: form.id,
          coverImg: imageUrl
        }).then(() => {
          console.log('封面图URL已保存到数据库')
        }).catch(err => {
          console.error('保存封面图URL失败:', err)
        })
      }
    }
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const handleRemove = (file, fileList) => {
  console.log('删除图片:', file, fileList)
  form.coverImg = ''
  
  // 如果课程已存在，立即更新数据库清除封面图
  if (form.id) {
    updateCourse({
      id: form.id,
      coverImg: ''
    }).then(() => {
      console.log('已清除数据库中的封面图URL')
    }).catch(err => {
      console.error('清除封面图URL失败:', err)
    })
  }
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

onMounted(() => {
  fetchCoachList()
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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>