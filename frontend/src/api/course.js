import request from '@/utils/request'

export const getCourseList = (params) => {
  return request({
    url: '/tbCourse/selectAll',
    method: 'get',
    params
  })
}

export const getCourseById = (id) => {
  return request({
    url: `/tbCourse/${id}`,
    method: 'get'
  })
}

export const createCourse = (data) => {
  return request({
    url: '/tbCourse/insert',
    method: 'post',
    data
  })
}

export const updateCourse = (data) => {
  return request({
    url: '/tbCourse/update',
    method: 'put',
    data
  })
}

export const deleteCourse = (idList) => {
  return request({
    url: '/tbCourse/delete',
    method: 'delete',
    params: { idList }
  })
}

export const addPrivateCourse = (data) => {
  return request({
    url: '/tbCourse/addPrivate',
    method: 'post',
    data
  })
}

export const purchaseCourse = (data) => {
  return request({
    url: '/tbCourse/purchase',
    method: 'post',
    data
  })
}

export const getMyPrivateCourses = (userId) => {
  return request({
    url: `/tbCourse/my-courses/${userId}`,
    method: 'get'
  })
}

export const getMyPrivateOrders = (userId) => {
  return request({
    url: `/tbCourse/my-orders/${userId}`,
    method: 'get'
  })
}

export const getAvailableSlots = (coachId) => {
  return request({
    url: '/tbCourseBooking/available-slots',
    method: 'get',
    params: { coachId }
  })
}

export const bookCourse = (data) => {
  return request({
    url: '/tbCourseBooking/book',
    method: 'post',
    params: data
  })
}