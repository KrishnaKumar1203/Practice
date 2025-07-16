const jsonServer = require('json-server');
const server = jsonServer.create();
const router = jsonServer.router('firstAPI.json');
const middlewares = jsonServer.defaults();

// Basic Auth
server.use((req, res, next) => {
  const auth = { login: 'admin', password: '1234' };
  const b64auth = (req.headers.authorization || '').split(' ')[1] || '';
  const [login, password] = Buffer.from(b64auth, 'base64').toString().split(':');

  if (login === auth.login && password === auth.password) {
    return next();
  }

  res.set('WWW-Authenticate', 'Basic realm="401"');
  res.status(401).send('Authentication required.');
});

server.use(middlewares);
server.use(router);

const listener = server.listen(3000, () => {
  console.log('🚀 JSON Server running at http://localhost:3000');
});

// Graceful shutdown
function shutdown() {
  console.log('🛑 Shutting down JSON Server...');
  listener.close(() => {
    console.log('✅ Server closed.');
    process.exit(0);
  });
}

process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);
