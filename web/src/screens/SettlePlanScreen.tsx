import type { SettlePlan } from "../api";
import Reveal from "../components/Reveal";

interface Props {
  plan: SettlePlan;
  onBack: () => void;
}

export default function SettlePlanScreen({ plan, onBack }: Props) {
  return (
    <section aria-labelledby="plan-title">
      <Reveal className="plan-intro">
        <button className="text-button" onClick={onBack} type="button">
          <span className="back-icon" aria-hidden="true">←</span> 결과로 돌아가기
        </button>
        <h1 id="plan-title">첫 2주 정착 플랜</h1>
        <p className="lead">
        새 환경에 익숙해지는 속도는 개체마다 다를 수 있어요.
        </p>
      </Reveal>
      <Reveal delay={70}>
      <section className="plan-block surface-shell">
        <div className="surface-core plan-core">
        <h2>날짜별 할 일</h2>
        <ol className="plan-list">
          {plan.days.map((day) => (
            <li key={day}>{day}</li>
          ))}
        </ol>
        </div>
      </section>
      </Reveal>
      <Reveal delay={140}>
      <section className="plan-block surface-shell">
        <div className="surface-core plan-core">
        <h2>주의할 신호</h2>
        <ul>
          {plan.warnings.map((warning) => (
            <li key={warning}>{warning}</li>
          ))}
        </ul>
        </div>
      </section>
      </Reveal>
      <Reveal delay={210}>
      <section className="plan-block surface-shell">
        <div className="surface-core plan-core">
        <h2>14일차에 확인할 것</h2>
        <ul>
          {plan.day14Questions.map((question) => (
            <li key={question}>{question}</li>
          ))}
        </ul>
        </div>
      </section>
      </Reveal>
    </section>
  );
}
