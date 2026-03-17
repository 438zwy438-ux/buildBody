import request from '@/utils/request'

export const getCardTemplateList = (params) => {
  return request({
    url: '/tbCardTemplate/selectAll',
    method: 'get',
    params
  })
}

export const getCardTemplateById = (id) => {
  return request({
    url: `/tbCardTemplate/${id}`,
    method: 'get'
  })
}

export const createCardTemplate = (data) => {
  return request({
    url: '/tbCardTemplate/insert',
    method: 'post',
    data
  })
}

export const updateCardTemplate = (data) => {
  return request({
    url: '/tbCardTemplate/update',
    method: 'put',
    data
  })
}

export const deleteCardTemplate = (idList) => {
  return request({
    url: '/tbCardTemplate/delete',
    method: 'delete',
    params: { idList }
  })
}