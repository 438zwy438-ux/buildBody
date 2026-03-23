import request from '@/utils/request'

export const getCoachProfileList = (params) => {
  return request({
    url: '/tbCoachProfile/selectAll',
    method: 'get',
    params
  })
}

export const getCoachProfileById = (id) => {
  return request({
    url: `/tbCoachProfile/${id}`,
    method: 'get'
  })
}

export const createCoachProfile = (data) => {
  return request({
    url: '/tbCoachProfile/insert',
    method: 'post',
    data
  })
}

export const updateCoachProfile = (data) => {
  return request({
    url: '/tbCoachProfile/update',
    method: 'put',
    data
  })
}

export const deleteCoachProfile = (idList) => {
  return request({
    url: '/tbCoachProfile/delete',
    method: 'delete',
    params: { idList }
  })
}

export const getCoachProfileByUserId = (userId) => {
  return request({
    url: `/tbCoachProfile/by-user-id/${userId}`,
    method: 'get'
  })
}

export const updateCoachProfileByUserId = (data) => {
  return request({
    url: '/tbCoachProfile/update',
    method: 'put',
    data
  })
}