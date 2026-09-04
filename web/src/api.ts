export type Home = "HOUSE" | "APARTMENT" | "VILLA" | "STUDIO";
export type SizeLimit = "NONE" | "UNDER_10" | "UNDER_25" | "UNKNOWN";
export type Tenure =
  "OWN" | "RENT_APPROVED" | "RENT_UNCONFIRMED" | "RENT_DENIED";
export type Absence =
  "UNDER_2" | "UNDER_4" | "FOUR_TO_EIGHT" | "EIGHT_TO_TEN" | "OVER_10";
export type Housemate =
  "ALONE" | "ADULT" | "CHILD_UNDER_7" | "SENIOR_65" | "PET";
export type Experience =
  "NONE" | "FAMILY" | "PRIMARY_UNDER_10" | "PRIMARY_OVER_10";
export type Expectation = "DAYS" | "WEEKS" | "MONTHS";

export interface Profile {
  home: Home;
  sizeLimit: SizeLimit;
  tenure: Tenure;
  absence: Absence;
  housemates: Housemate[];
  activeDays: number;
  experience: Experience;
  expectation: Expectation;
  sido: string;
  sigungu: string;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Region {
  uprCd?: string;
  orgCd?: string;
  orgdownNm: string;
}

export interface RawAnimal {
  kindNm: string;
  noticeEdt?: string;
  careNm?: string;
  careTel?: string;
}

export interface Animal {
  raw: RawAnimal;
  kg: number | null;
  neonate: boolean;
  ageApprox: number | null;
  adultSizeUnknown: boolean;
  infoLevel: "ENOUGH" | "LACKING";
  photoUrl?: string | null;
}

export interface MatchCard {
  animal: Animal;
  grade: "GOOD" | "CONDITIONAL" | "INFO_LACKING";
  flags: string[];
  reasons: string[];
  questions: string[];
}

export interface MatchOutput {
  landlordDenied: boolean;
  cards: MatchCard[];
}

export interface SettlePlan {
  days: string[];
  warnings: string[];
  day14Questions: string[];
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, init);
  const body = (await response.json()) as ApiResponse<T>;

  if (!response.ok || !body.success) {
    throw new Error(body.message || "요청을 처리하지 못했습니다.");
  }

  return body.data;
}

export function getSido(): Promise<Region[]> {
  return request("/api/v1/regions/sido");
}

export function getSigungu(uprCd: string): Promise<Region[]> {
  return request(`/api/v1/regions/sigungu?uprCd=${encodeURIComponent(uprCd)}`);
}

export function getMatches(profile: Profile): Promise<MatchOutput> {
  return request("/api/v1/matches", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify(profile),
  });
}

export function getSettlePlan(profile: Profile): Promise<SettlePlan> {
  return request("/api/v1/settle-plans", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify(profile),
  });
}
