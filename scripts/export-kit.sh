#!/usr/bin/env bash
set -euo pipefail
target=${1:?사용법: bash scripts/export-kit.sh <새 폴더>}
mkdir -p "$target"
for item in AGENTS.md build.gradle settings.gradle gradlew gradlew.bat gradle .env.example .gitignore .agents .codex plugins fixtures docs prompts scripts web/package.json web/vite.config.ts web/tsconfig.json web/index.html web/AGENTS.md src/main/resources; do
  if [ -e "$item" ]; then
    mkdir -p "$target/$(dirname "$item")"
    cp -R "$item" "$target/$(dirname "$item")"
  fi
done
echo "키트 반출 완료: $target (참조 구현의 Java·테스트·web/src는 포함하지 않음)"
