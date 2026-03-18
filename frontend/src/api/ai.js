import request from '@/utils/request'

export const aiChat = (data) => {
  return request({
    url: '/ai/chat',
    method: 'post',
    data
  })
}