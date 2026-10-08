"""Small standard-library client for the Practice toolkit HTTP API."""

from __future__ import annotations

import argparse
import os
from urllib.error import HTTPError
from urllib.parse import urlencode
from urllib.request import Request, urlopen

DEFAULT_BASE_URL = "http://127.0.0.1:8080"


def ask(question: str, base_url: str | None = None, timeout: float = 180) -> str:
    if not isinstance(question, str) or not question.strip():
        raise ValueError("Provide a non-empty question.")

    selected_url = base_url or os.environ.get("PRACTICE_TOOLKIT_URL", DEFAULT_BASE_URL)
    url = f"{selected_url.rstrip('/')}/api/ask?{urlencode({'question': question.strip()})}"
    request = Request(url, headers={"Accept": "text/plain"})
    try:
        with urlopen(request, timeout=timeout) as response:
            return response.read().decode("utf-8")
    except HTTPError as error:
        detail = error.read().decode("utf-8", errors="replace")
        raise RuntimeError(
            f"Toolkit API request failed (HTTP {error.code}): {detail}"
        ) from error


def main() -> int:
    parser = argparse.ArgumentParser(description="Ask the local Practice toolkit AI API.")
    parser.add_argument("question", nargs="+", help="Question or prompt to send")
    parser.add_argument(
        "--url",
        default=os.environ.get("PRACTICE_TOOLKIT_URL", DEFAULT_BASE_URL),
        help="Toolkit server base URL",
    )
    args = parser.parse_args()
    print(ask(" ".join(args.question), args.url))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
