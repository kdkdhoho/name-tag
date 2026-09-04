import { execFileSync, spawn, spawnSync } from 'node:child_process';
import { readFileSync } from 'node:fs';

const run = (cmd, args) => execFileSync(cmd, args, { stdio: 'inherit' });
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function waitForHealth(url) {
  for (let attempt = 0; attempt < 30; attempt += 1) {
    try { if ((await fetch(`${url}/api/v1/health`)).ok) return; } catch { /* 기동 대기 */ }
    await sleep(250);
  }
  throw new Error('REST 서버가 7.5초 안에 기동하지 않았습니다.');
}

async function verifyRest() {
  const url = 'http://127.0.0.1:18080';
  const server = spawn('java', ['-jar', 'build/libs/jeongchak-0.0.1-SNAPSHOT.jar', '--server.port=18080'], {
    env: { ...process.env, NAMETAG_USE_FIXTURE: '1' }, stdio: 'ignore'
  });
  try {
    await waitForHealth(url);
    const response = await fetch(`${url}/api/v1/matches`, {
      method: 'POST', headers: { 'content-type': 'application/json' }, body: readFileSync('fixtures/match.request.json', 'utf8')
    });
    const body = await response.json();
    if (!body.success || body.data.landlordDenied || body.data.cards.length === 0) throw new Error('fixture 매칭 REST 응답이 성공 계약을 충족하지 않습니다.');
    console.log(`REST match: ${body.data.cards.length} cards`);
  } finally { server.kill('SIGTERM'); }
}

try {
  run('./gradlew', ['-q', 'test', 'smoke']);
  const evaluation = JSON.parse(readFileSync('build/evaluation.json'));
  const safe = evaluation.personas === 20 && !evaluation.unsupportedReasons && !evaluation.endedAnimals && !evaluation.hardFilterViolations && !evaluation.goodFlagViolations;
  if (!safe) throw new Error(JSON.stringify(evaluation));
  if (process.argv.includes('--quick')) console.log('CHECK 1 PASSED');
  else {
    run('./gradlew', ['-q', 'bootJar']);
    await verifyRest();
    const mcp = spawnSync('java', ['-jar', 'build/libs/jeongchak-0.0.1-SNAPSHOT.jar', '--spring.profiles.active=mcp'], { input: '{"jsonrpc":"2.0","id":1,"method":"tools/list"}\n', encoding: 'utf8', timeout: 20_000 });
    if (mcp.status !== 0 || !mcp.stdout.includes('evaluate_match')) throw new Error(mcp.stderr);
    console.log('MCP tools: 5/5');
    console.log('ALL CHECKS PASSED');
  }
} catch (error) { console.error('CHECKS FAILED', error.message); process.exit(1); }
