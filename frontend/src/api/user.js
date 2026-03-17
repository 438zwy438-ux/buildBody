import request from '@/utils/request'

export const login = (data) => {
  return request({
    url: '/sysUser/login',
    method: 'post',
    data
  })
}

export const register = (data) => {
  return request({
    url: '/sysUser/register',
    method: 'post',
    data
  })
}

export const addCoach = (data) => {
  return request({
    url: '/sysUser/addCoach',
    method: 'post',
    data
  })
}

export const getUserList = (params) => {
  return request({
    url: '/sysUser/selectAll',
    method: 'get',
    params
  })
}

export const getUserById = (id) => {
  return request({
    url: `/sysUser/${id}`,
    method: 'get'
  })
}

export const updateUser = (data) => {
  return request({
    url: '/sysUser/update',
    method: 'put',
    data
  })
}

export const deleteUser = (idList) => {
  return request({
    url: '/sysUser/delete',
    method: 'delete',
    params: { idList }
  })
}