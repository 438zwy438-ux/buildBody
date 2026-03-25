<template>
  <div class="equipment-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>器材管理</span>
          <el-button type="primary" @click="handleAdd">添加器材</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="器材名称">
          <el-input v-model="searchForm.name" placeholder="请输入器材名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="维修中" :value="2" />
            <el-option label="已报废" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="器材名称" />
        <el-table-column prop="code" label="编号" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="buyDate" label="购买日期" />
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
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">维修中</el-tag>
            <el-tag v-else type="info">已报废</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="器材名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入器材名称" />
        </el-form-item>
        <el-form-item label="编号" prop="code">
          <el-input v-model="form.code" placeholder="请输入编号" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="购买日期" prop="buyDate">
          <el-date-picker
            v-model="form.buyDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="维修中" :value="2" />
            <el-option label="已报废" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细描述" prop="detailDesc">
          <el-input
            v-model="form.detailDesc"
            type="textarea"
            :rows="4"
            placeholder="请输入详细描述"
          />
        </el-form-item>
        <el-form-item label="设备图片">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadUrl"
            list-type="picture-card"
            :data="{ equipmentId: form.id }"
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
import { getEquipmentList, createEquipment, updateEquipment, deleteEquipment, deleteEquipmentImageByUrl } from '@/api/equipment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加器材')
const formRef = ref(null)
const tableData = ref([])
const fileList = ref([])
const previewVisible = ref(false)
const previewImage = ref('')
const uploadUrl = ref('/api/upload/equipment/')
const uploadedImageUrls = ref([]) // 存储已上传的图片URL

const searchForm = reactive({
  name: '',
  status: null
})

const pagination = reactive({
  page:1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  code: '',
  location: '',
  buyDate: null,
  status:1,
  detailDesc: ''
})

const rules = {
  name: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  buyDate: [{ required: true, message: '请选择购买日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
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
    if (searchForm.status !== null && searchForm.status !== '') {
      params.status = searchForm.status
    }
    const res = await getEquipmentList(params)
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } catch (error) {
    console.error('获取器材列表失败:', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page =1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = null
  pagination.page =1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '添加器材'
  form.id = null
  form.name = ''
  form.code = ''
  form.location = ''
  form.buyDate = null
  form.status =1
  form.detailDesc = ''
  fileList.value = []
  uploadedImageUrls.value = []
  uploadUrl.value = '/api/upload/equipment/'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  console.log('编辑器材数据:', row)
  console.log('row.images:', row.images)
  dialogTitle.value = '编辑器材'
  form.id = row.id
  form.name = row.name
  form.code = row.code
  form.location = row.location
  form.buyDate = row.buyDate
  form.status = row.status
  form.detailDesc = row.detailDesc || ''
  
  // 加载已有图片
  fileList.value = []
  uploadedImageUrls.value = []
  if (row.images && row.images.length > 0) {
    console.log('加载图片列表:', row.images)
    row.images.forEach((imgUrl, index) => {
      fileList.value.push({
        name: `image-${index}.jpg`,
        url: imgUrl,
        uid: Date.now() + index, // 添加唯一标识
        status: 'success'
      })
      uploadedImageUrls.value.push(imgUrl)
    })
  } else {
    console.log('没有找到 images 字段或为空')
  }
  
  uploadUrl.value = `/api/upload/equipment/${row.id}`
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该器材吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteEquipment([row.id])
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.log('取消删除')
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

const handleUploadSuccess = (response, file, fileList) => {
  console.log('上传成功:', response, file, fileList)
  if (response.code === 200) {
    ElMessage.success('上传成功')
    // 更新文件列表中的URL
    const imageUrl = response.data?.url || response.data
    if (imageUrl) {
      file.url = imageUrl
      uploadedImageUrls.value.push(imageUrl)
    }
    
    if (!form.id) {
      const newId = response.data?.equipmentId
      if (newId) {
        form.id = newId
        uploadUrl.value = `/api/upload/equipment/${newId}`
      }
    }
  } else {
    ElMessage.error(response.msg || '上传失败')
    // 上传失败时从文件列表中移除
    const index = fileList.indexOf(file)
    if (index > -1) {
      fileList.splice(index, 1)
    }
  }
}

const handleRemove = async (file) => {
  // 从已上传的图片URL列表中移除
  const index = uploadedImageUrls.value.indexOf(file.url)
  if (index > -1) {
    uploadedImageUrls.value.splice(index, 1)
  }
  
  // 从数据库中删除图片记录
  try {
    await deleteEquipmentImageByUrl(file.url)
  } catch (error) {
    console.error('删除图片失败:', error)
  }
}

const handlePicturePreview = (file) => {
  previewImage.value = file.url
  previewVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateEquipment(form)
        } else {
          await createEquipment(form)
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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  min-height: 40px;
  align-items: center;
}

.image-preview {
  display: flex;
  align-items: center;
}
</style>