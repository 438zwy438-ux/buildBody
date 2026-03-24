import request from '@/utils/request'

export function getCoachList(params) {
  return request({
    url: '/tbCoachProfile/selectAll',
    method: 'get',
    params
  })
}

export function getCoachDetail(id) {
  return request({
    url: `/tbCoachProfile/detail/${id}`,
    method: 'get'
  })
}

export function addCoach(data) {
  return request({
    url: '/tbCoachProfile/insert',
    method: 'post',
    data
  })
}

export function updateCoach(data) {
  return request({
    url: '/tbCoachProfile/update',
    method: 'put',
    data
  })
}

export function deleteCoach(idList) {
  return request({
    url: '/tbCoachProfile/delete',
    method: 'delete',
    params: { idList }
  })
}

export function deleteCoachImageByUrl(imgUrl) {
  return request({
    url: '/imgRelation/deleteByUrl',
    method: 'delete',
    params: { imgUrl }
  })
}