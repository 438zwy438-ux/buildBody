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
            <el-table-column prop="totalAmount" label="金额" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 0" type="warning">待支付</el-tag>
                <el-tag v-else-if="row.status === 1" type="success">已支付</el-tag>
                <el-tag v-else-if="row.status === 2" type="info">已取消</el-tag>
                <el-tag v-else-if="row.status === 3" type="danger">已退款</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button v-if="row.status === 0" type="primary" size="small" @click="handlePay(row)">支付</el-button>
                <el-button v-if="row.status === 0" type="danger" size="small" @click="handleCancel(row)">取消</el-button>
                <el-button v-if="row.status === 1" type="warning" size="small" @click="handleRefund(row)">退款</el-button>
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
            <el-table-column prop="totalAmount" label="金额" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handlePay(row)">支付</el-button>
                <el-button type="danger" size="small" @click="handleCancel(row)">取消</el-button>
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
            <el-table-column prop="totalAmount" label="金额" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 0" type="warning">待支付</el-tag>
                <el-tag v-else-if="row.status === 1" type="success">已支付</el-tag>
                <el-tag v-else-if="row.status === 2" type="info">已取消</el-tag>
                <el-tag v-else-if="row.status === 3" type="danger">已退款</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button v-if="row.status === 1" type="warning" size="small" @click="handleRefund(row)">退款</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已取消" name="cancelled">
          <el-table :data="cancelledOrders" v-loading="loading" border>
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="type" label="订单类型">
              <template #default="{ row }">
                <el-tag v-if="row.type === 1" type="success">会员卡</el-tag>
                <el-tag v-else-if="row.type === 2" type="warning">私教课</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="金额" />
            <el-table-column prop="cancelTime" label="取消时间">
              <template #default="{ row }">
                {{ formatDateTime(row.cancelTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已退款" name="refunded">
          <el-table :data="refundedOrders" v-loading="loading" border>
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="type" label="订单类型">
              <template #default="{ row }">
                <el-tag v-if="row.type === 1" type="success">会员卡</el-tag>
                <el-tag v-else-if="row.type === 2" type="warning">私教课</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="原金额" />
            <el-table-column prop="refundAmount" label="退款金额">
              <template #default="{ row }">
                <span style="color: #f56c6c; font-weight: bold;">¥{{ row.refundAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="refundTime" label="退款时间">
              <template #default="{ row }">
                {{ formatDateTime(row.refundTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyOrders, cancelOrder, refundOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const activeTab = ref('all')
const allOrders = ref([])

const pendingOrders = computed(() => allOrders.value.filter(order => order.status === 0))
const completedOrders = computed(() => allOrders.value.filter(order => order.status === 1))
const cancelledOrders = computed(() => allOrders.value.filter(order => order.status === 2))
const refundedOrders = computed(() => allOrders.value.filter(order => order.status === 3))

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMyOrders()
    allOrders.value = res.data
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const handlePay = (order) => {
  ElMessage.info('支付功能待实现')
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

const handleRefund = async (order) => {
  try {
    await ElMessageBox.confirm('确定要退款吗? 退款金额将按剩余次数/天数计算并打8折。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await refundOrder(order.id)
    ElMessage.success('退款成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退款失败:', error)
      ElMessage.error('退款失败')
    }
  }
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