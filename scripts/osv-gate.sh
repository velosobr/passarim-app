#!/usr/bin/env bash
# Falha se alguma dependência distribuída no app tiver vulnerabilidade com CVSS >= 7.0 (alta ou crítica),
# como pede o spec geral §9. Lê os gradle.lockfile (veja resolveAndLockShipped no build.gradle.kts).
# Uso: scripts/osv-gate.sh [diretório]   (padrão: raiz do repositório)
set -euo pipefail

ROOT="${1:-$(cd "$(dirname "$0")/.." && pwd)}"
THRESHOLD=7.0
REPORT="$(mktemp)"

# O osv-scanner sai com código 1 quando acha qualquer vulnerabilidade; o corte por severidade é nosso.
osv-scanner scan source -r "$ROOT" --format json > "$REPORT" 2>/dev/null || true

HIGH="$(jq -r --argjson limit "$THRESHOLD" '
  [.results[]?.packages[]?
   | .package as $p
   | .groups[]?
   | select((.max_severity // "0") | tonumber >= $limit)
   | "\($p.name)@\($p.version): \(.ids | join(", ")) (CVSS \(.max_severity))"]
  | .[]' "$REPORT")"

rm -f "$REPORT"
if [ -n "$HIGH" ]; then
  echo "Vulnerabilidades altas/críticas (CVSS >= $THRESHOLD) nas dependências do app:" >&2
  echo "$HIGH" >&2
  exit 1
fi
echo "osv-scanner: nenhuma vulnerabilidade com CVSS >= $THRESHOLD nas dependências do app."
