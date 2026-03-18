import request from '@/utils/request'

export const getEquipmentList = (params) => {
  return request({
    url: '/tbEquipment/selectAll',
    method: 'get',
    params
  })
}

export const getEquipmentById = (id) => {
  return request({
    url: `/tbEquipment/${id}`,
    method: 'get'
  })
}

export const getEquipmentDetail = (id) => {
  return request({
    url: `/tbEquipment/detail/${id}`,
    method: 'get'
  })
}

export const createEquipment = (data) => {
  return request({
    url: '/tbEquipment/insert',
    method: 'post',
    data
  })
}

export const updateEquipment = (data) => {
  return request({
    url: '/tbEquipment/update',
    method: 'put',
    data
  })
}

export const deleteEquipment = (idList) => {
  return request({
    url: '/tbEquipment/delete',
    method: 'delete',
    params: { idList }
  })
}