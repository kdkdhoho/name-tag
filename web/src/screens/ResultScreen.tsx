import type { MatchOutput } from "../api";
import AnimalCard from "../components/AnimalCard";

interface Props {
  result: MatchOutput;
  isLoadingPlan: boolean;
  onRestart: () => void;
  onSettlePlan: () => void;
}

const sections = [
  { grade: "GOOD", title: "잘 맞음" },
  { grade: "CONDITIONAL", title: "조건부 추천" },
  { grade: "INFO_LACKING", title: "정보 부족" },
] as const;

export default function ResultScreen({
  result,
  isLoadingPlan,
  onRestart,
  onSettlePlan,
}: Props) {
  return (
    <section aria-labelledby="result-title">
      <div className="screen-heading">
        <div>
          <h1 id="result-title">추천 결과</h1>
          <p className="lead">공고 정보와 생활 조건을 같이 확인해 보세요.</p>
        </div>
        <button className="text-button" onClick={onRestart} type="button">
          조건 다시 입력
        </button>
      </div>
      {result.cards.length === 0 && (
        <p className="empty-state">현재 조건에 맞는 공고를 찾지 못했습니다.</p>
      )}
      {sections.map((section) => {
        const cards = result.cards.filter(
          (card) => card.grade === section.grade,
        );
        if (cards.length === 0) return null;
        return (
          <section className="result-section" key={section.grade}>
            <h2>{section.title}</h2>
            {cards.map((card) => (
              <AnimalCard
                card={card}
                isLoadingPlan={isLoadingPlan}
                key={`${card.animal.raw.kindNm}-${card.animal.raw.noticeEdt}`}
                onSettlePlan={onSettlePlan}
              />
            ))}
          </section>
        );
      })}
      <p className="disclaimer">품종으로 성격을 단정하지 않습니다.</p>
    </section>
  );
}
