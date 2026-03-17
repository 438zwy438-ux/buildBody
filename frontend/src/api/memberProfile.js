import request from '@/utils/request'

export const getMemberProfileList = (params) => {
  return request({
    url: '/tbMemberProfile/selectAll',
    method: 'get',
    params
  })
}

export const getMemberProfileById = (id) => {
  return request({
    url: `/tbMemberProfile/${id}`,
    method: 'get'
  })
}

export const createMemberProfile = (data) => {
  return request({
    url: '/tbMemberProfile/insert',
    method: 'post',
    data
  })
}

export const updateMemberProfile = (data) => {
  return request({
    url: '/tbMemberProfile/update',
    method: 'put',
    data
  })
}

export const deleteMemberProfile = (idList) => {
  return request({
    url: '/tbMemberProfile/delete',
    method: 'delete',
    params: { idList }
  })
}