#!/usr/bin/env bash
# Gera os PNGs do iOS (AppIcon 1024 e ícone da Launch Screen) a partir dos SVGs de design/icon.
# Requer o Google Chrome instalado (usa o modo headless para rasterizar).
set -euo pipefail

cd "$(dirname "$0")/.."
CHROME="${CHROME_PATH:-/Applications/Google Chrome.app/Contents/MacOS/Google Chrome}"
OUT=iosApp/iosApp/Assets.xcassets
TMP="$(mktemp -d)"

# Pedaços dos SVGs: o fundo (retângulo) e o grupo com os traços do pássaro.
BACKGROUND="$(grep '<rect' design/icon/background.svg)"
BIRD="$(sed -n '/<g /,/<\/g>/p' design/icon/foreground.svg)"
SPLASH="$(sed -n '/<circle/,/<\/g>/p' design/icon/splash.svg)"

# AppIcon: o iOS mostra o quadrado inteiro, então recortamos a região central (72 de 108) para o pássaro ocupar bem o ícone.
cat > "$TMP/appicon.html" <<HTML
<body style="margin:0"><svg xmlns="http://www.w3.org/2000/svg" viewBox="18 18 72 72" width="1024" height="1024">
$BACKGROUND
$BIRD
</svg></body>
HTML
"$CHROME" --headless=new --disable-gpu --hide-scrollbars --window-size=1024,1024 \
  --screenshot="$OUT/AppIcon.appiconset/icon-1024.png" "file://$TMP/appicon.html" >/dev/null 2>&1

# Ícone da Launch Screen: disco com o pássaro, fundo transparente, em 1x/2x/3x.
for scale in 1 2 3; do
  size=$((120 * scale))
  cat > "$TMP/launch$scale.html" <<HTML
<body style="margin:0;background:transparent"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 108 108" width="$size" height="$size">
$SPLASH
</svg></body>
HTML
  "$CHROME" --headless=new --disable-gpu --hide-scrollbars --default-background-color=00000000 \
    --window-size="$size,$size" --screenshot="$OUT/LaunchIcon.imageset/launch-icon@${scale}x.png" \
    "file://$TMP/launch$scale.html" >/dev/null 2>&1
done

rm -rf "$TMP"
echo "PNGs gerados em $OUT"
