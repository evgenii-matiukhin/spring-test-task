import { API_CONFIG } from './config'
import { useAuth } from '@/lib/hooks/useAuth'
import type { PageRequest, SectionPage, Section } from '@/lib/types'

export async function fetchSections(pageRequest?: PageRequest): Promise<SectionPage> {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const params = pageRequest 
    ? `?page=${pageRequest.page}&size=${pageRequest.size}` 
    : ''

  const response = await fetch(`${API_CONFIG.BASE_URL}/sections${params}`, {
    headers: API_CONFIG.getAuthHeaders(username, password),
  })
  if (!response.ok) throw new Error('Failed to fetch sections')
  return response.json()
}

export async function createSection(data: Omit<Section, 'id'>): Promise<Section> {
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

export async function updateSection(id: number | undefined, data: Omit<Section, 'id'>): Promise<Section> {
  if (!id) throw new Error('Section ID is required')
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

export async function deleteSection(id: number): Promise<void> {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/sections/${id}`, {
    method: 'DELETE',
    headers: API_CONFIG.getAuthHeaders(username, password),
  })
  if (!response.ok) throw new Error('Failed to delete section')
}