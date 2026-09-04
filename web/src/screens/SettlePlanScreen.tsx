import type { SettlePlan } from "../api";

interface Props {
  plan: SettlePlan;
  onBack: () => void;
}

export default function SettlePlanScreen({ plan, onBack }: Props) {
  return (
    <section aria-labelledby="plan-title">
      <button className="text-button" onClick={onBack} type="button">
        ← 결과로 돌아가기
      </button>
      <h1 id="plan-title">첫 2주 정착 플랜</h1>
      <p className="lead">
        새 환경에 익숙해지는 속도는 개체마다 다를 수 있어요.
      </p>
      <section className="plan-block">
        <h2>날짜별 할 일</h2>
        <ol className="plan-list">
          {plan.days.map((day) => (
            <li key={day}>{day}</li>
          ))}
        </ol>
      </section>
      <section className="plan-block">
        <h2>주의할 신호</h2>
        <ul>
          {plan.warnings.map((warning) => (
            <li key={warning}>{warning}</li>
          ))}
        </ul>
      </section>
      <section className="plan-block">
        <h2>14일차에 확인할 것</h2>
        <ul>
          {plan.day14Questions.map((question) => (
            <li key={question}>{question}</li>
          ))}
        </ul>
      </section>
    </section>
  );
}
