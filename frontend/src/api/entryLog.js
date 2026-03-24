import request from '@/utils/request'

export const getEntryLogList = (params) => {
  return request({
    url: '/tbEntryLog/selectAll',
    method: 'get',
    params
  })
}

export const searchMember = (data) => {
  return request({
    url: '/tbEntryLog/search',
    method: 'post',
    data
  })
}

export const checkIn = (userId) => {
  return request({
    url: '/tbEntryLog/checkIn',
    method: 'post',
    params: { userId }
  })
}

export const checkOut = (id) => {
  return request({
    url: '/tbEntryLog/checkOut',
    method: 'post',
    params: { id }
  })
}

export const getEntryLogById = (id) => {
  return request({
    url: `/tbEntryLog/${id}`,
    method: 'get'
  })
}

export const createEntryLog = (data) => {
  return request({
    url: '/tbEntryLog/insert',
    method: 'post',
    data
  })
}

export const updateEntryLog = (data) => {
  return request({
    url: '/tbEntryLog/update',
    method: 'put',
    data
  })
}

export const deleteEntryLog = (idList) => {
  return request({
    url: '/tbEntryLog/delete',
    method: 'delete',
    params: { idList }
  })
}

export const getMyLogs = () => {
  return request({
    url: '/tbEntryLog/my-logs',
    method: 'get'
  })
}