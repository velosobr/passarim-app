#!/usr/bin/env bash
# Resume os resultados dos testes de lógica por módulo, plataforma e classe (lê os XMLs do JUnit).
# Uso: scripts/test-summary.sh [módulo...]   Ex.: scripts/test-summary.sh core/data app
set -euo pipefail

cd "$(dirname "$0")/.."
python3 - "$@" <<'PY'
import glob, re, sys

modules = sys.argv[1:] or sorted({p.split("/build/")[0] for p in glob.glob("**/build/test-results/*/*.xml", recursive=True)})
total = [0, 0]
for module in modules:
    for path in sorted(glob.glob(f"{module}/build/test-results/*/*.xml")):
        text = open(path, encoding="utf-8").read()
        m = re.search(r'tests="(\d+)" skipped="\d+" failures="(\d+)" errors="(\d+)"', text)
        if not m:
            continue
        tests, failures, errors = (int(x) for x in m.groups())
        target = path.split("/build/test-results/")[1].split("/")[0]
        cls = re.search(r'<testsuite name="([^"]+)"', text).group(1).split(".")[-1]
        print(f"{module:20} {target:24} {cls:34} {tests:3} testes, {failures + errors} falhas")
        total[0] += tests
        total[1] += failures + errors
print(f"TOTAL: {total[0]} testes, {total[1]} falhas")
sys.exit(1 if total[1] else 0)
PY
