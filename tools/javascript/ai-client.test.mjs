import assert from "node:assert/strict";
import { after, before, test } from "node:test";
import { createServer } from "node:http";
import { ask } from "./ai-client.mjs";

let server;
let baseUrl;

before(async () => {
  server = createServer((request, response) => {
    if (request.url.startsWith("/api/ask?")) {
      response.writeHead(200, { "Content-Type": "text/plain" });
      response.end("test answer");
      return;
    }
    response.writeHead(404, { "Content-Type": "text/plain" });
    response.end("not found");
  });
  await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
  baseUrl = `http://127.0.0.1:${server.address().port}`;
});

after(async () => {
  await new Promise((resolve, reject) => {
    server.close((error) => (error ? reject(error) : resolve()));
  });
});

test("asks the local toolkit API and returns its response", async () => {
  assert.equal(await ask("hello", baseUrl), "test answer");
});

test("rejects empty questions before making a request", async () => {
  await assert.rejects(ask("  ", baseUrl), {
    name: "TypeError",
    message: "Provide a non-empty question.",
  });
});
