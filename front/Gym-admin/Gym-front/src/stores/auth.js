import { defineStore } from 'pinia'

const FRONT_TOKEN_KEY = 'FRONT_TOKEN'
const FRONT_USER_KEY = 'FRONT_USER'

export const useAuth = defineStore('auth', {
  state: () => ({
    token: window.localStorage.getItem(FRONT_TOKEN_KEY) || '',
    user: (() => {
      try {
        const raw = window.localStorage.getItem(FRONT_USER_KEY)
        return raw ? JSON.parse(raw) : null
      } catch {
        return null
      }
    })(),
  }),
  actions: {
    setToken(token) {
      this.token = token || ''
      if (this.token) window.localStorage.setItem(FRONT_TOKEN_KEY, this.token)
      else window.localStorage.removeItem(FRONT_TOKEN_KEY)
    },
    setUser(user) {
      this.user = user || null
      if (this.user) window.localStorage.setItem(FRONT_USER_KEY, JSON.stringify(this.user))
      else window.localStorage.removeItem(FRONT_USER_KEY)
    },
    logout() {
      this.setToken('')
      this.setUser(null)
    },
  },
})

export { FRONT_TOKEN_KEY }

