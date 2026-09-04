import { cpSync, existsSync, mkdirSync, rmSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';

const source = '.agents/skills';
const target = 'plugins/jeongchak/skills';
if (!existsSync(source)) throw new Error(`${source}가 없습니다.`);
rmSync(target, { recursive: true, force: true });
mkdirSync(target, { recursive: true });
cpSync(source, target, { recursive: true });
writeFileSync(join('plugins/jeongchak', '.mcp.json'), JSON.stringify({
  mcpServers: { jeongchak: { command: 'java', args: ['-jar', 'build/libs/jeongchak-0.0.1-SNAPSHOT.jar', '--spring.profiles.active=mcp'], env: { NAMETAG_USE_FIXTURE: '1' } } }
}, null, 2) + '\n');
console.log('플러그인 준비 완료: 9개 스킬과 MCP 설정을 동기화했습니다.');
