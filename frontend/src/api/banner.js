import request from '@/utils/request'

export const getBannerList = (params) => {
  return request({
    url: '/sysBanner/selectAll',
    method: 'get',
    params
  })
}

export const getBannerById = (id) => {
  return request({
    url: `/sysBanner/${id}`,
    method: 'get'
  })
}

export const createBanner = (data) => {
  return request({
    url: '/sysBanner/insert',
    method: 'post',
    data
  })
}

export const updateBanner = (data) => {
  return request({
    url: '/sysBanner/update',
    method: 'put',
    data
  })
}

export const deleteBanner = (idList) => {
  return request({
    url: '/sysBanner/delete',
    method: 'delete',
    params: { idList },
    paramsSerializer: {
      serialize: (params) => {
        return Object.keys(params)
          .map(key => {
            const value = params[key]
            if (Array.isArray(value)) {
              return `${key}=${value.join(',')}`
            }
            return `${key}=${value}`
          })
          .join('&')
      }
    }
  })
}