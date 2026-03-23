import request from '@/utils/request'

export const getDashboardStats = () => {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

export const getRecentEntries = () => {
  return request({
    url: '/statistics/recent-entries',
    method: 'get'
  })
}