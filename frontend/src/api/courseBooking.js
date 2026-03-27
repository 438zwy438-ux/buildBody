import request from '@/utils/request'

export const getMyBookings = () => {
  return request({
    url: '/tbCourseBooking/my-bookings',
    method: 'get'
  })
}

export const cancelBooking = (bookingId) => {
  return request({
    url: '/tbCourseBooking/cancel',
    method: 'post',
    params: { bookingId }
  })
}
export const checkCourse = (bookingId) => {
  return request({
    url: '/tbCourseBooking/check',
    method: 'post',
    params: { bookingId }
  })
}

export const getCoachBookings = (params) => {
  return request({
    url: '/tbCourseBooking/coach-bookings',
    method: 'get',
    params
  })
}