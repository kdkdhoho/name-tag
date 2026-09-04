interface Props {
  onRetry: () => void;
}

export default function LandlordDeniedScreen({ onRetry }: Props) {
  return (
    <section className="notice-screen" aria-labelledby="denied-title">
      <p className="eyebrow">입양 전 확인</p>
      <h1 id="denied-title">임대인 반대 안내</h1>
      <p className="lead">
        반려동물 입양은 주거 계약과 공동생활 규정을 먼저 확인한 뒤 진행하는 편이
        안전해요.
      </p>
      <ol className="numbered-list">
        <li>
          <strong>특약 효력 확인:</strong> 임대차계약의 반려동물 관련 특약과
          건물 관리규약을 읽어 보세요.
        </li>
        <li>
          <strong>동의 받는 순서:</strong> 계약서와 규약을 확인한 뒤 임대인에게
          입양 예정 사실과 관리 계획을 설명하세요.
        </li>
        <li>
          <strong>기록 남기기:</strong> 동의 내용은 문자나 계약 특약처럼 확인할
          수 있는 방식으로 남겨 두세요.
        </li>
      </ol>
      <button className="primary-button" onClick={onRetry} type="button">
        다시 시도
      </button>
    </section>
  );
}
