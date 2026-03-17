import request from '@/utils/request'

export const getLockerList = (params) => {
  return request({
    url: '/tbLocker/selectAll',
    method: 'get',
    params
  })
}

export const getLockerById = (id) => {
  return request({
    url: `/tbLocker/${id}`,
    method: 'get'
  })
}

export const createLocker = (data) => {
  return request({
    url: '/tbLocker/insert',
    method: 'post',
    data
  })
}

export const updateLocker = (data) => {
  return request({
    url: '/tbLocker/update',
    method: 'put',
    data
  })
}

export const deleteLocker = (idList) => {
  return request({
    url: '/tbLocker/delete',
    method: 'delete',
    params: { idList }
  })
}

export const lockLocker = (id) => {
  return request({
    url: `/tbLocker/lock/${id}`,
    method: 'put'
  })
}

export const unlockLocker = (id) => {
  return request({
    url: `/tbLocker/unlock/${id}`,
    method: 'put'
  })
}