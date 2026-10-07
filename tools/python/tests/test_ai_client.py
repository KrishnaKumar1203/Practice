import json
import threading
import unittest
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

from tools.python.ai_client import ask


class _Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path.startswith("/api/ask?"):
            body = b"python test answer"
            self.send_response(200)
        else:
            body = json.dumps({"error": "not found"}).encode()
            self.send_response(404)
        self.send_header("Content-Type", "text/plain")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, _format, *_args):
        return


class AiClientTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.server = ThreadingHTTPServer(("127.0.0.1", 0), _Handler)
        cls.thread = threading.Thread(target=cls.server.serve_forever, daemon=True)
        cls.thread.start()
        cls.base_url = f"http://127.0.0.1:{cls.server.server_port}"

    @classmethod
    def tearDownClass(cls):
        cls.server.shutdown()
        cls.thread.join()
        cls.server.server_close()

    def test_asks_local_toolkit_api(self):
        self.assertEqual(ask("hello", self.base_url), "python test answer")

    def test_rejects_empty_questions(self):
        with self.assertRaisesRegex(ValueError, "non-empty question"):
            ask("  ", self.base_url)


if __name__ == "__main__":
    unittest.main()
