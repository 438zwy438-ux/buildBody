import request from '@/utils/request'

export const getFixLogList = (params) => {
  return request({
    url: '/tbFixLog/selectAll',
    method: 'get',
    params
  })
}

export const getFixLogById = (id) => {
  return request({
    url: `/tbFixLog/${id}`,
    method: 'get'
  })
}

export const createFixLog = (data) => {
  return request({
    url: '/tbFixLog/insertAndUpdateStatus',
    method: 'post',
    data
  })
}

export const updateFixLog = (data) => {
  return request({
    url: '/tbFixLog/update',
    method: 'put',
    data
  })
}

export const deleteFixLog = (idList) => {
  return request({
    url: '/tbFixLog/delete',
    method: 'delete',
    params: { idList }
  })
}