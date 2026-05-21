#!/usr/bin/env bash
set -euo pipefail

readonly README_FILE="README.md"
readonly START_MARKER="<!-- VERSION-MATRIX:START -->"
readonly END_MARKER="<!-- VERSION-MATRIX:END -->"

usage() {
  echo "Usage: $0 <project-version> <quarkus-version>" >&2
  exit 1
}

if [[ $# -ne 2 ]]; then
  usage
fi

project_version="$1"
quarkus_version="$2"

if [[ ! "${project_version}" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-lts)?$ ]]; then
  echo "Invalid project version: ${project_version}" >&2
  exit 1
fi

if [[ ! "${quarkus_version}" =~ ^[0-9]+\.[0-9]+\.[0-9]+([.-][0-9]+)?$ ]]; then
  echo "Invalid Quarkus version: ${quarkus_version}" >&2
  exit 1
fi

release_type="Normal"
if [[ "${project_version}" == *-lts ]]; then
  release_type="LTS"
fi

cd "$(git rev-parse --show-toplevel)"

if [[ ! -f "${README_FILE}" ]]; then
  echo "README.md not found" >&2
  exit 1
fi

python3 - "${README_FILE}" "${START_MARKER}" "${END_MARKER}" "${project_version}" "${quarkus_version}" "${release_type}" <<'PY'
from pathlib import Path
import re
import sys

readme_path = Path(sys.argv[1])
start_marker = sys.argv[2]
end_marker = sys.argv[3]
project_version = sys.argv[4]
quarkus_version = sys.argv[5]
release_type = sys.argv[6]

content = readme_path.read_text()

start = content.find(start_marker)
end = content.find(end_marker)
if start == -1 or end == -1 or end < start:
    raise SystemExit("Invalid version matrix markers in README.md")

end += len(end_marker)
section = content[start:end]

row_pattern = re.compile(r"^\| ([^|]+) \| ([^|]+) \| ([^|]+) \|$")
rows = []

for line in section.splitlines():
    match = row_pattern.match(line.strip())
    if not match:
        continue
    version = match.group(1).strip()
    if version == "Project version":
        continue
    rows.append(
        {
            "version": version,
            "type": match.group(2).strip(),
            "quarkus": match.group(3).strip(),
        }
    )

updated = False
for row in rows:
    if row["version"] == project_version:
        row["type"] = release_type
        row["quarkus"] = quarkus_version
        updated = True
        break

if not updated:
    rows.append(
        {
            "version": project_version,
            "type": release_type,
            "quarkus": quarkus_version,
        }
    )

def version_key(value: str):
    raw = value[:-4] if value.endswith("-lts") else value
    parts = tuple(int(part) for part in raw.split("."))
    is_lts = 1 if value.endswith("-lts") else 0
    return (*parts, is_lts)

rows.sort(key=lambda row: version_key(row["version"]), reverse=True)

lines = [
    start_marker,
    "The project keeps normal releases and `-lts` releases aligned with specific Quarkus streams.",
    "",
    "| Project version | Type | Quarkus version |",
    "|---|---|---|",
]

for row in rows:
    lines.append(f"| {row['version']} | {row['type']} | {row['quarkus']} |")

lines.extend(
    [
        "",
        "The matrix above is maintained via workflow dispatch using the informed project version and Quarkus version.",
        end_marker,
    ]
)

replacement = "\n".join(lines)
updated_content = content[:start] + replacement + content[end:]

if updated_content != content:
    readme_path.write_text(updated_content)
PY

echo "Updated ${README_FILE} version matrix for ${project_version} -> ${quarkus_version}"