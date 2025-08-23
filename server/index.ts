// Temporary file to run Vite since we can't modify package.json
import { spawn } from 'child_process';

console.log('Starting Vite frontend server on http://localhost:5173...');
console.log('Backend should be running on http://localhost:8080');

const vite = spawn('npx', ['vite', '--host', '0.0.0.0', '--port', '5173'], {
  stdio: 'inherit'
});

vite.on('error', (error) => {
  console.error('Failed to start Vite:', error);
});

vite.on('exit', (code) => {
  console.log(`Vite exited with code ${code}`);
});