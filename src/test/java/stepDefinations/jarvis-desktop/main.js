const { app, BrowserWindow, session } = require('electron');
const path = require('path');

function createWindow() {
  const win = new BrowserWindow({
    width: 900,
    height: 700,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'), // fixed path
      contextIsolation: true,
      nodeIntegration: false,
      // Enable media access
      // Note: 'media' option is not documented but permissions handler is required
    }
  });

  win.loadFile(path.join(__dirname, 'public/index.html'));
}

app.whenReady().then(() => {
  // Handle permission requests, e.g., microphone access
  session.defaultSession.setPermissionRequestHandler((webContents, permission, callback) => {
    if (permission === 'media') {
      // Approve microphone and camera access requests
      callback(true);
    } else {
      // Deny all other permissions
      callback(false);
    }
  });

  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit();
});
