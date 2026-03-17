import request from '@/utils/request'

export const uploadFile = (file, folder) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)
  
  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}