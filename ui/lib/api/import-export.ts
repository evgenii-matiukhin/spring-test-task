const API_BASE = '/api/v1';

export async function startImport(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch(`${API_BASE}/import`, {
    method: 'POST',
    body: formData,
  });
  if (!response.ok) throw new Error('Failed to start import');
  return response.json();
}

export async function checkImportStatus(jobId: string) {
  const response = await fetch(`${API_BASE}/import/${jobId}`);
  if (!response.ok) throw new Error('Failed to check import status');
  return response.json();
}

export async function startExport() {
  const response = await fetch(`${API_BASE}/export`);
  if (!response.ok) throw new Error('Failed to start export');
  return response.json();
}

export async function checkExportStatus(jobId: string) {
  const response = await fetch(`${API_BASE}/export/${jobId}`);
  if (!response.ok) throw new Error('Failed to check export status');
  return response.json();
}

export async function downloadExportFile(jobId: string) {
  const response = await fetch(`${API_BASE}/export/${jobId}/file`);
  if (!response.ok) throw new Error('Failed to download export file');
  return response.blob();
}