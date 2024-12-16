export async function login(username: string, password: string) {
  const validCredentials = {
    'admin': 'admin',
    'user': 'user'
  };

  if (validCredentials[username] === password) {
    return { success: true, user: { username } };
  }
  
  throw new Error('Invalid credentials');
}