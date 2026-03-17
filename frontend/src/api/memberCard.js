import request from '@/utils/request'

export const getMemberCardList = (params) => {
  return request({
    url: '/tbMemberCard/selectAll',
    method: 'get',
    params
  })
}

export const getMemberCardById = (id) => {
  return request({
    url: `/tbMemberCard/${id}`,
    method: 'get'
  })
}

export const createMemberCard = (data) => {
  return request({
    url: '/tbMemberCard/insert',
    method: 'post',
    data
  })
}

export const updateMemberCard = (data) => {
  return request({
    url: '/tbMemberCard/update',
    method: 'put',
    data
  })
}

export const deleteMemberCard = (idList) => {
  return request({
    url: '/tbMemberCard/delete',
    method: 'delete',
    params: { idList }
  })
}