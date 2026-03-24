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

export const batchSaveEquipmentImages = (equipmentId, imgUrls) => {
  return request({
    url: '/imgRelation/batchSave',
    method: 'post',
    params: { relationType: 1, relationId: equipmentId },
    data: imgUrls
  })
}

export const deleteEquipmentImages = (equipmentId) => {
  return request({
    url: '/imgRelation/deleteByRelation',
    method: 'delete',
    params: { relationType: 1, relationId: equipmentId }
  })
}

export const deleteEquipmentImageByUrl = (imgUrl) => {
  return request({
    url: '/imgRelation/deleteByUrl',
    method: 'delete',
    params: { imgUrl }
  })
}