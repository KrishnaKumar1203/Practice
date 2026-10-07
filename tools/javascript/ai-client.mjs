import { pathToFileURL } from "node:url";

const defaultBaseUrl = "http://127.0.0.1:8080";

export async function ask(question, baseUrl = process.env.PRACTICE_TOOLKIT_URL ?? defaultBaseUrl) {
  if (typeof question !== "string" || question.trim() === "") {
    throw new TypeError("Provide a non-empty question.");
  }

  const url = new URL("/api/ask", baseUrl);
  url.searchParams.set("question", question.trim());
  const response = await fetch(url, { headers: { Accept: "text/plain" } });
  const body = await response.text();
  if (!response.ok) {
    throw new Error(`Toolkit API request failed (HTTP ${response.status}): ${body}`);
  }
  return body;
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  ask(process.argv.slice(2).join(" "))
    .then((answer) => console.log(answer))
    .catch((error) => {
      console.error(error instanceof Error ? error.message : String(error));
      process.exitCode = 1;
    });
}
