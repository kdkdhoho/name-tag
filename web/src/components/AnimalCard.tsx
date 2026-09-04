import { useState } from "react";
import type { MatchCard } from "../api";

interface Props {
  card: MatchCard;
  isLoadingPlan: boolean;
  onSettlePlan: () => void;
}

function gradeLabel(card: MatchCard) {
  if (card.grade === "GOOD") return "잘 맞음";
  if (card.grade === "CONDITIONAL")
    return `조건부 · 확인 ${card.flags.length}개`;
  return "정보 부족";
}

function deadline(date?: string) {
  if (!date || !/^\d{8}$/.test(date)) return "마감일 확인 필요";
  return `${date.slice(0, 4)}.${date.slice(4, 6)}.${date.slice(6, 8)} 마감`;
}

function reasonSentence(reason: string) {
  const trimmed = reason.trim();
  return /[.?!。]$/.test(trimmed) ? trimmed : `${trimmed}.`;
}

function publicNoticeUrl(desertionNo?: string) {
  if (!desertionNo) return null;

  return `https://www.animal.go.kr/front/awtis/public/publicDtl.do?desertionNo=${encodeURIComponent(desertionNo)}&menuNo=1000000055`;
}

export default function AnimalCard({
  card,
  isLoadingPlan,
  onSettlePlan,
}: Props) {
  const [imageFailed, setImageFailed] = useState(false);
  const { animal } = card;
  const age = animal.neonate
    ? "60일 미만"
    : animal.ageApprox === null
      ? "나이 확인 필요"
      : `${animal.ageApprox}세`;
  const weight = animal.kg === null ? null : `${animal.kg}kg`;
  const noticeUrl = publicNoticeUrl(animal.raw.desertionNo);

  return (
    <article className="animal-card surface-shell">
      <div className="surface-core animal-card-core">
      <div className="animal-photo" aria-label="보호 동물 사진">
        {animal.photoUrl && !imageFailed ? (
          <img
            alt="보호 동물"
            onError={() => setImageFailed(true)}
            src={animal.photoUrl}
          />
        ) : (
          <span className="photo-fallback">
            <svg viewBox="0 0 64 64" aria-hidden="true"><path d="M18 47c3-10 9-16 14-16s11 6 14 16M20 24c0-5 3-9 7-9 3 0 5 2 5 5 0-3 2-5 5-5 4 0 7 4 7 9 0 8-5 13-12 13s-12-5-12-13Z" /></svg>
            사진 없음
          </span>
        )}
      </div>
      <div className="animal-content">
        <div className="card-title-row">
          <span className={`badge grade-${card.grade.toLowerCase()}`}>
            {gradeLabel(card)}
          </span>
          <span className="deadline">{deadline(animal.raw.noticeEdt)}</span>
        </div>
        <h3>
          {animal.raw.kindNm} · {age}
          {weight && ` · ${weight}`}
        </h3>
        <div className="badge-list">
          {animal.adultSizeUnknown && (
            <span className="badge badge-warning">성견 체중 확인 필요</span>
          )}
          {animal.infoLevel === "LACKING" && (
            <span className="badge badge-lacking">정보 부족</span>
          )}
          {card.flags.map((flag) => (
            <span className="badge badge-warning" key={flag}>
              {flag}
            </span>
          ))}
        </div>
        <div className="reason-list">
          {card.reasons.map((reason) => (
            <p key={reason}>{reasonSentence(reason)}</p>
          ))}
        </div>
        <div className="question-list">
          <h4>보호소에 물어볼 것</h4>
          <ul>
            {card.questions.map((question) => (
              <li key={question}>{question}</li>
            ))}
          </ul>
        </div>
        <p className="shelter-contact">
          {animal.raw.careNm && <span>{animal.raw.careNm} </span>}
          {animal.raw.careTel ? (
            <a href={`tel:${animal.raw.careTel}`}>{animal.raw.careTel}</a>
          ) : (
            "전화번호 확인 필요"
          )}
        </p>
        {noticeUrl && (
          <a
            className="notice-link"
            href={noticeUrl}
            rel="noreferrer"
            target="_blank"
          >
            국가동물보호정보시스템 공고 보기
          </a>
        )}
        <button
          className="secondary-button"
          disabled={isLoadingPlan}
          onClick={onSettlePlan}
          type="button"
        >
          <span>{isLoadingPlan ? "플랜을 불러오는 중" : "정착 플랜"}</span>
          <span className="button-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none"><path d="M5 12h13M13 6l6 6-6 6" /></svg>
          </span>
        </button>
      </div>
      </div>
    </article>
  );
}
