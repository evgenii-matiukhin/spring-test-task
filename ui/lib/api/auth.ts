function delay(ms: number) {
    return new Promise( resolve => setTimeout(resolve, ms) );
}
export async function login(username: string, password: string) {
  const validCredentials = {
    'admin': 'admin',
    'user': 'user'
  };
  await delay(5000);

  if (validCredentials[username] === password) {
    return { success: true, user: { username } };
  }
  
  throw new Error('Invalid credentials');
}