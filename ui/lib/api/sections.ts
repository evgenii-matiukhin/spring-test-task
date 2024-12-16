const API_BASE = '/api/v1';

export async function fetchSections() {
  const response = await fetch(`${API_BASE}/sections`);
  if (!response.ok) throw new Error('Failed to fetch sections');
  return response.json();
}

export async function createSection(data: { name: string; geologicalClasses: string[] }) {
  const response = await fetch(`${API_BASE}/sections`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to create section');
  return response.json();
}

export async function updateSection(id: string, data: { name: string; geologicalClasses: string[] }) {
  const response = await fetch(`${API_BASE}/sections/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error('Failed to update section');
  return response.json();
}

export async function deleteSection(id: string) {
  const response = await fetch(`${API_BASE}/sections/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error('Failed to delete section');
}