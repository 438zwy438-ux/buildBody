import request from '@/utils/request'

export const verifyMemberByPhone = (data) => {
  return request({
    url: '/tbLocker/verify',
    method: 'post',
    data
  })
}

export const getAvailableLockers = (params) => {
  return request({
    url: '/tbLocker/available',
    method: 'get',
    params
  })
}

export const useLocker = (data) => {
  return request({
    url: '/tbLocker/use',
    method: 'post',
    data
  })
}

export const tempOpenLocker = (data) => {
  return request({
    url: '/tbLocker/tempOpen',
    method: 'post',
    data
  })
}

export const returnLocker = () => {
  return request({
    url: '/tbLocker/return',
    method: 'post'
  })
}

export const getMyLocker = (params) => {
  return request({
    url: '/tbLocker/myLocker',
    method: 'get',
    params
  })
}

export const lockLocker = (data) => {
  return request({
    url: '/tbLocker/lock',
    method: 'post',
    data
  })
}

export const getLockerList = (params) => {
  return request({
    url: '/tbLocker/selectAll',
    method: 'get',
    params
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

export const lockLockerAdmin = (id) => {
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

export const adminUnlockLocker = (id) => {
  return request({
    url: `/tbLocker/adminUnlock/${id}`,
    method: 'put'
  })
}

export const adminReleaseLocker = (id) => {
  return request({
    url: `/tbLocker/adminRelease/${id}`,
    method: 'post'
  })
}