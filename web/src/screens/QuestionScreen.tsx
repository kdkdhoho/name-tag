import { useEffect, useState, type FormEvent } from "react";
import {
  getSido,
  getSigungu,
  type Absence,
  type Experience,
  type Expectation,
  type Home,
  type Housemate,
  type Profile,
  type Region,
  type SizeLimit,
  type Tenure,
} from "../api";

interface Props {
  initialProfile: Profile;
  isLoading: boolean;
  onSubmit: (profile: Profile) => void;
}

type Option<T extends string> = { value: T; label: string };

const homes: Option<Home>[] = [
  { value: "HOUSE", label: "단독주택" },
  { value: "APARTMENT", label: "아파트" },
  { value: "VILLA", label: "빌라·다세대" },
  { value: "STUDIO", label: "원룸·오피스텔" },
];
const limits: Option<SizeLimit>[] = [
  { value: "NONE", label: "제한 없음" },
  { value: "UNDER_10", label: "10kg 미만" },
  { value: "UNDER_25", label: "25kg 미만" },
  { value: "UNKNOWN", label: "아직 모르겠어요" },
];
const tenures: Option<Tenure>[] = [
  { value: "OWN", label: "자가" },
  { value: "RENT_APPROVED", label: "임대인 동의를 받았어요" },
  { value: "RENT_UNCONFIRMED", label: "임대인에게 아직 확인 전이에요" },
  { value: "RENT_DENIED", label: "임대인이 반대해요" },
];
const absences: Option<Absence>[] = [
  { value: "UNDER_2", label: "2시간 미만" },
  { value: "UNDER_4", label: "2~4시간" },
  { value: "FOUR_TO_EIGHT", label: "4~8시간" },
  { value: "EIGHT_TO_TEN", label: "8~10시간" },
  { value: "OVER_10", label: "10시간 초과" },
];
const housemates: Option<Housemate>[] = [
  { value: "ALONE", label: "혼자 살아요" },
  { value: "ADULT", label: "다른 성인과 함께 살아요" },
  { value: "CHILD_UNDER_7", label: "7세 미만 아이가 있어요" },
  { value: "SENIOR_65", label: "65세 이상 어르신이 있어요" },
  { value: "PET", label: "다른 반려동물이 있어요" },
];
const experiences: Option<Experience>[] = [
  { value: "NONE", label: "처음이에요" },
  { value: "FAMILY", label: "가족 반려견을 함께 돌봤어요" },
  { value: "PRIMARY_UNDER_10", label: "주 양육 경험 10년 미만" },
  { value: "PRIMARY_OVER_10", label: "주 양육 경험 10년 이상" },
];
const expectations: Option<Expectation>[] = [
  { value: "DAYS", label: "며칠 안에 적응하길 기대해요" },
  { value: "WEEKS", label: "몇 주는 기다릴 수 있어요" },
  { value: "MONTHS", label: "몇 달의 적응 기간을 생각해요" },
];

function SelectField<T extends string>({
  label,
  options,
  value,
  onChange,
}: {
  label: string;
  options: Option<T>[];
  value: T;
  onChange: (value: T) => void;
}) {
  return (
    <label className="field-label">
      <span>{label}</span>
      <select
        value={value}
        onChange={(event) => onChange(event.target.value as T)}
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </label>
  );
}

export default function QuestionScreen({
  initialProfile,
  isLoading,
  onSubmit,
}: Props) {
  const [profile, setProfile] = useState(initialProfile);
  const [sidos, setSidos] = useState<Region[]>([]);
  const [sigungus, setSigungus] = useState<Region[]>([]);
  const [regionError, setRegionError] = useState("");

  useEffect(() => {
    getSido()
      .then((regions) => {
        setSidos(regions);
        if (!regions.some((region) => region.orgdownNm === profile.sido)) {
          setProfile((current) => ({
            ...current,
            sido: regions[0]?.orgdownNm ?? "",
            sigungu: "",
          }));
        }
      })
      .catch(() => setRegionError("지역 목록을 불러오지 못했습니다."));
  }, []);

  useEffect(() => {
    const selectedSido = sidos.find(
      (region) => region.orgdownNm === profile.sido,
    );
    if (!selectedSido?.uprCd) return;

    getSigungu(selectedSido.uprCd)
      .then((regions) => {
        setSigungus(regions);
        if (!regions.some((region) => region.orgdownNm === profile.sigungu)) {
          setProfile((current) => ({
            ...current,
            sigungu: regions[0]?.orgdownNm ?? "",
          }));
        }
      })
      .catch(() => setRegionError("시·군·구 목록을 불러오지 못했습니다."));
  }, [profile.sido, profile.sigungu, sidos]);

  function update<K extends keyof Profile>(key: K, value: Profile[K]) {
    setProfile((current) => ({ ...current, [key]: value }));
  }

  function toggleHousemate(value: Housemate) {
    setProfile((current) => {
      const housemates = current.housemates.includes(value)
        ? current.housemates.filter((item) => item !== value)
        : [...current.housemates, value];
      return { ...current, housemates };
    });
  }

  function handleSido(value: string) {
    setProfile((current) => ({ ...current, sido: value, sigungu: "" }));
  }

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    onSubmit(profile);
  }

  return (
    <section aria-labelledby="question-title">
      <h1 id="question-title">정착</h1>
      <p className="lead">생활 조건을 바탕으로 보호소 공고를 함께 살펴봐요.</p>
      <form className="question-form" onSubmit={submit}>
        <fieldset>
          <legend>1. 거주 지역</legend>
          <div className="two-columns">
            <label className="field-label">
              <span>시·도</span>
              <select
                value={profile.sido}
                onChange={(event) => handleSido(event.target.value)}
              >
                {sidos.length === 0 && (
                  <option value={profile.sido}>{profile.sido}</option>
                )}
                {sidos.map((region) => (
                  <option key={region.uprCd} value={region.orgdownNm}>
                    {region.orgdownNm}
                  </option>
                ))}
              </select>
            </label>
            <label className="field-label">
              <span>시·군·구</span>
              <select
                value={profile.sigungu}
                onChange={(event) => update("sigungu", event.target.value)}
              >
                {sigungus.length === 0 && (
                  <option value={profile.sigungu}>{profile.sigungu}</option>
                )}
                {sigungus.map((region) => (
                  <option key={region.orgCd} value={region.orgdownNm}>
                    {region.orgdownNm}
                  </option>
                ))}
              </select>
            </label>
          </div>
          {regionError && (
            <p className="field-help" role="alert">
              {regionError}
            </p>
          )}
        </fieldset>
        <fieldset>
          <legend>2. 주거 형태</legend>
          <SelectField
            label="집 형태"
            options={homes}
            value={profile.home}
            onChange={(value) => update("home", value)}
          />
        </fieldset>
        <fieldset>
          <legend>3. 반려견 크기 제한</legend>
          <SelectField
            label="허용 가능한 성견 체중"
            options={limits}
            value={profile.sizeLimit}
            onChange={(value) => update("sizeLimit", value)}
          />
        </fieldset>
        <fieldset>
          <legend>4. 임대인 동의</legend>
          <SelectField
            label="주거 계약 상태"
            options={tenures}
            value={profile.tenure}
            onChange={(value) => update("tenure", value)}
          />
        </fieldset>
        <fieldset>
          <legend>5. 평소 혼자 있는 시간</legend>
          <SelectField
            label="하루 평균 부재 시간"
            options={absences}
            value={profile.absence}
            onChange={(value) => update("absence", value)}
          />
        </fieldset>
        <fieldset>
          <legend>6. 함께 사는 구성원</legend>
          <div className="check-list">
            {housemates.map((option) => (
              <label key={option.value} className="check-option">
                <input
                  checked={profile.housemates.includes(option.value)}
                  onChange={() => toggleHousemate(option.value)}
                  type="checkbox"
                />
                {option.label}
              </label>
            ))}
          </div>
        </fieldset>
        <fieldset>
          <legend>7. 일주일에 활동 가능한 날</legend>
          <label className="range-field">
            <input
              aria-label="활동 가능한 날 수"
              max="7"
              min="0"
              onChange={(event) =>
                update("activeDays", Number(event.target.value))
              }
              type="range"
              value={profile.activeDays}
            />
            <strong>주 {profile.activeDays}일</strong>
          </label>
        </fieldset>
        <fieldset>
          <legend>8. 반려견 양육 경험</legend>
          <SelectField
            label="양육 경험"
            options={experiences}
            value={profile.experience}
            onChange={(value) => update("experience", value)}
          />
        </fieldset>
        <fieldset>
          <legend>9. 적응 기간 기대</legend>
          <SelectField
            label="기다릴 수 있는 기간"
            options={expectations}
            value={profile.expectation}
            onChange={(value) => update("expectation", value)}
          />
        </fieldset>
        <button className="primary-button" disabled={isLoading} type="submit">
          {isLoading ? "추천을 살펴보는 중…" : "추천 보기"}
        </button>
      </form>
    </section>
  );
}
