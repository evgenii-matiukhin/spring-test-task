import { API_CONFIG } from './config'
import { useAuth } from '@/lib/hooks/useAuth'

export async function fetchSections() {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/sections`, {
    headers: API_CONFIG.getAuthHeaders(username, password),
  })
  if (!response.ok) throw new Error('Failed to fetch sections')
  return response.json()
}

export async function createSection(data: { name: string; geologicalClasses: string[] }) {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/sections`, {
    method: 'POST',
    headers: API_CONFIG.getAuthHeaders(username, password),
    body: JSON.stringify(data),
  })
  if (!response.ok) throw new Error('Failed to create section')
  return response.json()
}

export async function updateSection(id: string, data: { name: string; geologicalClasses: string[] }) {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/sections/${id}`, {
    method: 'PUT',
    headers: API_CONFIG.getAuthHeaders(username, password),
    body: JSON.stringify(data),
  })
  if (!response.ok) throw new Error('Failed to update section')
  return response.json()
}

export async function deleteSection(id: string) {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/sections/${id}`, {
    method: 'DELETE',
    headers: API_CONFIG.getAuthHeaders(username, password),
  })
  if (!response.ok) throw new Error('Failed to delete section')
}