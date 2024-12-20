"use client"

import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AuthState {
  username: string | null
  password: string | null
  setCredentials: (username: string, password: string) => void
  clearCredentials: () => void
}

export const useAuth = create<AuthState>()(
  persist(
    (set) => ({
      username: null,
      password: null,
      setCredentials: (username: string, password: string) => 
        set({ username, password }),
      clearCredentials: () => set({ username: null, password: null }),
    }),
    {
      name: 'auth-storage',
    }
  )
)