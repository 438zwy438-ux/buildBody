import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const setRoles = (newRoles) => {
    roles.value = newRoles
    localStorage.setItem('roles', JSON.stringify(newRoles))
  }

  const hasRole = (role) => {
    return roles.value.includes(role)
  }

  const hasAnyRole = (roleList) => {
    return roleList.some(role => roles.value.includes(role))
  }

  const loginAction = async (loginData) => {
    try {
      const res = await login(loginData)
      setToken(res.data.token)
      setUserInfo(res.data)
      setRoles(res.data.roles || [])
      return res
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    roles.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('roles')
  }

  return {
    token,
    userInfo,
    roles,
    setToken,
    setUserInfo,
    setRoles,
    hasRole,
    hasAnyRole,
    loginAction,
    logout
  }
})