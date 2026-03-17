<template>
  <div class="orders-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部订单" name="all">
          <el-table :data="allOrders" v-loading="loading" border>
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="type" label="订单类型">
              <template #default="{ row }">
                <el-tag v-if="row.type === 1" type="success">会员卡</el-tag>
                <el-tag v-else-if="row.type === 2" type="warning">私教课</el-tag>
                <el-tag v-else type="info">其他</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 0" type="warning">待支付</el-tag>
                <el-tag v-else-if="row.status === 1" type="success">已支付</el-tag>
                <el-tag v-else type="info">已取消</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button v-if="row.status === 0" type="primary" size="small" @click="handlePay(row)">支付</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="待支付" name="pending">
          <el-table :data="pendingOrders" v-loading="loading" border>
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="type" label="订单类型">
              <template #default="{ row }">
                <el-tag v-if="row.type === 1" type="success">会员卡</el-tag>
                <el-tag v-else-if="row.type === 2" type="warning">私教课</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handlePay(row)">支付</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已完成" name="completed">
          <el-table :data="completedOrders" v-loading="loading" border>
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="type" label="订单类型">
              <template #default="{ row }">
                <el-tag v-if="row.type === 1" type="success">会员卡</el-tag>
                <el-tag v-else-if="row.type === 2" type="warning">私教课</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" />
            <el-table-column prop="createTime" label="创建时间" />
          </el-table>
        </el-tab-pane>
      </el-tabs>

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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getOrderList } from '@/api/order'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const loading = ref(false)
const activeTab = ref('all')
const allOrders = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const pendingOrders = computed(() => allOrders.value.filter(order => order.status === 0))
const completedOrders = computed(() => allOrders.value.filter(order => order.status === 1))

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      current: pagination.page,
      size: pagination.size,
      userId: userInfo.value.id
    })
    allOrders.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePay = (order) => {
  ElMessage.info('支付功能待实现')
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
.orders-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>