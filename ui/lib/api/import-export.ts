import { API_CONFIG } from './config'
import { useAuth } from '@/lib/hooks/useAuth'

export async function startImport(file: File): Promise<any> {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const formData = new FormData()
  formData.append('file', file)
  
  const response = await fetch(`${API_CONFIG.BASE_URL}/import`, {
    method: 'POST',
    headers: {
      'Authorization': API_CONFIG.getAuthHeaders(username, password).Authorization,
    },
    body: formData,
  })
  if (!response.ok) throw new Error('Failed to start import')
  return response.json()
}

export async function checkImportStatus(jobId: string): Promise<any> {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/import/${jobId}`, {
    headers: API_CONFIG.getAuthHeaders(username, password),
  })
  if (!response.ok) throw new Error('Failed to check import status')
  return response.json()
}

export async function startExport(): Promise<any> {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/export`, {
    headers: API_CONFIG.getAuthHeaders(username, password),
    method: 'POST',
  })
  if (!response.ok) throw new Error('Failed to start export')
  return response.json()
}

export async function checkExportStatus(jobId: string): Promise<any> {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/export/${jobId}`, {
    headers: API_CONFIG.getAuthHeaders(username, password),
  })
  if (!response.ok) throw new Error('Failed to check export status')
  return response.json()
}

export async function downloadExportFile(jobId: string): Promise<Blob> {
  const { username, password } = useAuth.getState()
  if (!username || !password) throw new Error('Not authenticated')

  const response = await fetch(`${API_CONFIG.BASE_URL}/export/${jobId}/file`, {
    headers: API_CONFIG.getAuthHeaders(username, password),
  })
  if (!response.ok) throw new Error('Failed to download export file')
  return response.blob()
}