import { useState } from "react";
import {
  getMatches,
  getSettlePlan,
  type MatchOutput,
  type Profile,
  type SettlePlan,
} from "./api";
import LandlordDeniedScreen from "./screens/LandlordDeniedScreen";
import QuestionScreen from "./screens/QuestionScreen";
import ResultScreen from "./screens/ResultScreen";
import SettlePlanScreen from "./screens/SettlePlanScreen";

const initialProfile: Profile = {
  home: "STUDIO",
  sizeLimit: "UNDER_10",
  tenure: "OWN",
  absence: "UNDER_4",
  housemates: ["ALONE"],
  activeDays: 3,
  experience: "NONE",
  expectation: "WEEKS",
  sido: "서울",
  sigungu: "강남구",
};

export default function App() {
  const [profile, setProfile] = useState<Profile>(initialProfile);
  const [result, setResult] = useState<MatchOutput | null>(null);
  const [plan, setPlan] = useState<SettlePlan | null>(null);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  async function showMatches(nextProfile: Profile) {
    setProfile(nextProfile);
    setError("");
    setIsLoading(true);

    try {
      setResult(await getMatches(nextProfile));
    } catch (caught) {
      setError(
        caught instanceof Error
          ? caught.message
          : "추천을 불러오지 못했습니다.",
      );
    } finally {
      setIsLoading(false);
    }
  }

  async function showSettlePlan() {
    setError("");
    setIsLoading(true);

    try {
      setPlan(await getSettlePlan(profile));
    } catch (caught) {
      setError(
        caught instanceof Error
          ? caught.message
          : "정착 플랜을 불러오지 못했습니다.",
      );
    } finally {
      setIsLoading(false);
    }
  }

  function startAgain() {
    setResult(null);
    setPlan(null);
    setError("");
  }

  let content = (
    <QuestionScreen
      initialProfile={profile}
      isLoading={isLoading}
      onSubmit={showMatches}
    />
  );

  if (plan) {
    content = <SettlePlanScreen plan={plan} onBack={() => setPlan(null)} />;
  } else if (result?.landlordDenied) {
    content = <LandlordDeniedScreen onRetry={startAgain} />;
  } else if (result) {
    content = (
      <ResultScreen
        isLoadingPlan={isLoading}
        onRestart={startAgain}
        onSettlePlan={showSettlePlan}
        result={result}
      />
    );
  }

  return (
    <main className="app-shell">
      <header className="app-header">
        <p className="brand-mark" aria-label="정착">정착</p>
      </header>
      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}
      {content}
    </main>
  );
}
