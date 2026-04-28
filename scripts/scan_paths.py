#!/usr/bin/env python3
"""
Scan localhost endpoints and classify paths as:
- occupied   : any response except 404 (including 401/403/405/500)
- free       : 404
- unavailable: connection refused / timeout / DNS / transport errors

By default, this script also discovers Spring mappings from Java controllers.
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable
from urllib import error, request


HTTP_METHODS = ("GET", "POST", "PUT", "DELETE", "PATCH")
METHOD_ANNOTATIONS = {
    "GetMapping": "GET",
    "PostMapping": "POST",
    "PutMapping": "PUT",
    "DeleteMapping": "DELETE",
    "PatchMapping": "PATCH",
}

CLASS_REQ_RE = re.compile(r"@RequestMapping\s*\((.*?)\)", re.DOTALL)
MAPPING_RE = re.compile(
    r"@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping|RequestMapping)\s*(?:\((.*?)\))?",
    re.DOTALL,
)
CLASS_DEF_RE = re.compile(r"\bclass\b")
STRING_LITERAL_RE = re.compile(r'"([^"\\]*(?:\\.[^"\\]*)*)"')
COMMENT_RE = re.compile(r"//.*?$|/\*.*?\*/", re.DOTALL | re.MULTILINE)


@dataclass(frozen=True)
class Route:
    method: str
    path: str


def normalize_path(path: str) -> str:
    if not path:
        return "/"
    cleaned = path.strip()
    if not cleaned.startswith("/"):
        cleaned = "/" + cleaned
    normalized = re.sub(r"/{2,}", "/", cleaned)
    if len(normalized) > 1 and normalized.endswith("/"):
        normalized = normalized[:-1]
    return normalized


def join_paths(prefix: str, suffix: str) -> str:
    prefix = normalize_path(prefix)
    suffix = normalize_path(suffix)
    if prefix == "/":
        return suffix
    return normalize_path(prefix.rstrip("/") + "/" + suffix.lstrip("/"))


def parse_mapping_paths(arg_text: str | None) -> list[str]:
    if not arg_text:
        return ["/"]

    # Supports:
    # @GetMapping("/x"), @RequestMapping(path = "/x"), @RequestMapping(value = {"/a", "/b"})
    named_path_match = re.search(r"\b(?:path|value)\s*=\s*(\{.*?\}|\".*?\")", arg_text, re.DOTALL)
    if named_path_match:
        target = named_path_match.group(1)
    else:
        target = arg_text

    literals = [bytes(s, "utf-8").decode("unicode_escape") for s in STRING_LITERAL_RE.findall(target)]
    if literals:
        return [normalize_path(s) for s in literals]
    return ["/"]


def discover_routes(java_root: Path) -> set[Route]:
    routes: set[Route] = set()
    for java_file in java_root.rglob("*.java"):
        try:
            raw = java_file.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            continue
        text = COMMENT_RE.sub("", raw)

        class_prefix = "/"
        class_def_match = CLASS_DEF_RE.search(text)
        if class_def_match:
            before_class = text[: class_def_match.start()]
            class_mappings = CLASS_REQ_RE.findall(before_class)
            if class_mappings:
                # Use the closest @RequestMapping before class definition.
                class_prefix = parse_mapping_paths(class_mappings[-1])[0]

        for match in MAPPING_RE.finditer(text):
            annotation = match.group(1)
            args = match.group(2)
            # Skip class-level @RequestMapping (already handled in class_prefix).
            if annotation == "RequestMapping":
                after = text[match.end() : match.end() + 120]
                next_tokens = after.lstrip()
                if next_tokens.startswith("public class") or next_tokens.startswith("class "):
                    continue

            if annotation == "RequestMapping":
                method = "GET"
                method_match = re.search(
                    r"\bmethod\s*=\s*RequestMethod\.(GET|POST|PUT|DELETE|PATCH)",
                    args or "",
                )
                if method_match:
                    method = method_match.group(1)
                paths = parse_mapping_paths(args)
            else:
                method = METHOD_ANNOTATIONS[annotation]
                paths = parse_mapping_paths(args)

            for p in paths:
                full_path = join_paths(class_prefix, p)
                routes.add(Route(method=method, path=full_path))

    return routes


def classify_status(status: int) -> str:
    if status == 404:
        return "free"
    return "occupied"


def check_url(url: str, method: str, timeout: float) -> tuple[str, str]:
    req = request.Request(url=url, method=method)
    try:
        with request.urlopen(req, timeout=timeout) as resp:
            status = resp.status
            return classify_status(status), f"HTTP {status}"
    except error.HTTPError as exc:
        return classify_status(exc.code), f"HTTP {exc.code}"
    except Exception as exc:  # noqa: BLE001 - need to classify transport failures.
        return "unavailable", f"{type(exc).__name__}: {exc}"


def read_paths_file(paths_file: Path) -> set[Route]:
    routes: set[Route] = set()
    for line in paths_file.read_text(encoding="utf-8").splitlines():
        cleaned = line.strip()
        if not cleaned or cleaned.startswith("#"):
            continue
        if " " in cleaned:
            maybe_method, maybe_path = cleaned.split(maxsplit=1)
            upper = maybe_method.upper()
            if upper in HTTP_METHODS:
                routes.add(Route(method=upper, path=normalize_path(maybe_path)))
                continue
        routes.add(Route(method="GET", path=normalize_path(cleaned)))
    return routes


def format_group(name: str, items: list[tuple[Route, str]]) -> str:
    lines = [f"{name} ({len(items)}):"]
    for route, details in sorted(items, key=lambda x: (x[0].path, x[0].method)):
        lines.append(f"  - [{route.method}] {route.path:<35} -> {details}")
    return "\n".join(lines)


def parse_args(argv: Iterable[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Scan localhost paths and classify them as occupied/free/unavailable.",
    )
    parser.add_argument(
        "--base-url",
        default="http://localhost:8080",
        help="Base URL to check (default: http://localhost:8080).",
    )
    parser.add_argument(
        "--timeout",
        type=float,
        default=4.0,
        help="HTTP timeout in seconds (default: 4.0).",
    )
    parser.add_argument(
        "--paths-file",
        type=Path,
        help="Optional text file with paths or 'METHOD /path' lines.",
    )
    parser.add_argument(
        "--skip-discovery",
        action="store_true",
        help="Do not auto-discover mappings from src/main/java.",
    )
    parser.add_argument(
        "--list-only",
        action="store_true",
        help="Only print discovered routes without making HTTP requests.",
    )
    return parser.parse_args(list(argv))


def main(argv: Iterable[str]) -> int:
    args = parse_args(argv)
    root = Path(__file__).resolve().parents[1]

    routes: set[Route] = set()
    if not args.skip_discovery:
        routes |= discover_routes(root / "src/main/java")

    if args.paths_file:
        if not args.paths_file.exists():
            print(f"Paths file not found: {args.paths_file}", file=sys.stderr)
            return 2
        routes |= read_paths_file(args.paths_file)

    if not routes:
        print("No routes found. Provide --paths-file or disable --skip-discovery.", file=sys.stderr)
        return 2

    print(f"Discovered routes: {len(routes)}")
    if args.list_only:
        for route in sorted(routes, key=lambda r: (r.path, r.method)):
            print(f"[{route.method}] {route.path}")
        return 0

    occupied: list[tuple[Route, str]] = []
    free: list[tuple[Route, str]] = []
    unavailable: list[tuple[Route, str]] = []
    base = args.base_url.rstrip("/")

    for route in sorted(routes, key=lambda r: (r.path, r.method)):
        url = f"{base}{route.path}"
        group, details = check_url(url, route.method, args.timeout)
        if group == "occupied":
            occupied.append((route, details))
        elif group == "free":
            free.append((route, details))
        else:
            unavailable.append((route, details))

    print()
    print(format_group("Occupied", occupied))
    print()
    print(format_group("Free", free))
    print()
    print(format_group("Unavailable", unavailable))
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
