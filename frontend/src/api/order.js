import request from '@/utils/request'

export const getOrderList = (params) => {
  return request({
    url: '/sysOrder/selectAll',
    method: 'get',
    params
  })
}

export const getOrderById = (id) => {
  return request({
    url: `/sysOrder/${id}`,
    method: 'get'
  })
}

export const createOrder = (data) => {
  return request({
    url: '/sysOrder/insert',
    method: 'post',
    data
  })
}

export const updateOrder = (data) => {
  return request({
    url: '/sysOrder/update',
    method: 'put',
    data
  })
}

export const deleteOrder = (idList) => {
  return request({
    url: '/sysOrder/delete',
    method: 'delete',
    params: { idList }
  })
}

export const getMyOrders = () => {
  return request({
    url: '/sysOrder/my-orders',
    method: 'get'
  })
}

export const cancelOrder = (orderId) => {
  return request({
    url: '/sysOrder/cancel',
    method: 'post',
    params: { orderId }
  })
}

export const refundOrder = (orderId) => {
  return request({
    url: '/sysOrder/refund',
    method: 'post',
    params: { orderId }
  })
}