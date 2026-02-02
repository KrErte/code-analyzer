import { Finding } from './analysis.model';

export interface ProductionIncident {
  id: string;
  title: string;
  severity: 'P0' | 'P1' | 'P2' | 'P3';
  scenario: string;
  whatHappens: string;
  rootCause: string;
  affectedLine?: number;
  affectedFile?: string;
  timeToOccur: string;
  probabilityPercent: number;
  businessImpact: string;
  costEstimate?: CostEstimate;
  preventionCode: string;
}

export interface CostEstimate {
  minDollars: number;
  maxDollars: number;
  breakdown: string;
}

export interface ShipItScore {
  verdict: string;
  confidence: number;
  reasoning: string;
  mustFixBefore: string[];
  niceToHave: string[];
  tldr: string;
  riskBreakdown: RiskBreakdown;
}

export interface RiskBreakdown {
  securityRisk: number;
  stabilityRisk: number;
  performanceRisk: number;
  maintainabilityRisk: number;
  dataLossRisk: number;
}

export interface CostAnalysis {
  totalEstimatedCost: number;
  engineeringHoursToFix: number;
  potentialRevenueLoss: number;
  technicalDebtCost: number;
  recommendation: string;
}

export interface Achievement {
  id: string;
  name: string;
  icon: string;
  description: string;
  unlocked: boolean;
  unlockedReason?: string;
}

export interface CodeRoast {
  headline: string;
  roasts: string[];
  memeUrl: string;
  savageryLevel: number;
  constructiveTakeaway: string;
}

// NEW: Famous Bug Pattern Matching
export interface FamousBugMatch {
  famousBugId: string;
  bugName: string;
  company: string;
  year: string;
  icon: string;
  similarityPercent: number;
  matchReason: string;
  financialImpact: string;
  yourCodePattern: string;
  historyPattern: string;
  lesson: string;
}

// NEW: Pre-Mortem (postmortem before incident)
export interface PreMortem {
  incidentTitle: string;
  severity: string;
  date: string;
  timeOfIncident: string;
  duration: string;
  executiveSummary: string;
  timeline: string;
  rootCauses: string[];
  contributingFactors: string[];
  impactAssessment: string;
  customerCommunication: string;
  actionItems: string[];
  lessonsLearned: string;
  whoGetsBlamed: string;
  slackChannelName: string;
  numberOfPagesGenerated: number;
  postmortemMeetingDuration: string;
}

// NEW: On-Call Forecast
export interface OnCallForecast {
  painIndex: number;
  overallVerdict: string;
  predictedPages: number;
  sleepInterruptions: number;
  weekendRuined: number;
  timeline: OnCallEvent[];
  survivalTips: string[];
  worstCaseScenario: string;
  bestCaseScenario: string;
  coffeeCupsNeeded: number;
  grayHairsGained: number;
  relationshipStrainIndex: number;
  recommendedCopingMechanism: string;
}

export interface OnCallEvent {
  day: string;
  time: string;
  event: string;
  severity: string;
  mood: string;
  whatYoullBeDoing: string;
}

// NEW: Code Karma
export interface CodeKarma {
  karmaScore: number;
  karmaVerdict: string;
  debtCreated: TechDebtCreated;
  debtInherited: TechDebtInherited;
  karmaLedger: KarmaEvent[];
  nextLifePrediction: string;
  reincarnationAs: string;
  sixMonthsFromNow: string;
  oneYearFromNow: string;
  futureYouMessage: string;
}

export interface TechDebtCreated {
  totalHours: number;
  maintainerCurses: number;
  debtItems: string[];
  worstOffense: string;
}

export interface TechDebtInherited {
  totalHours: number;
  originalSinner: string;
  yearOfSin: string;
  inheritedProblems: string[];
}

export interface KarmaEvent {
  action: string;
  karmaPoints: number;
  consequence: string;
}

// NEW: Mock Scenario
export interface MockScenario {
  id: string;
  name: string;
  icon: string;
  category: string;
  difficulty: string;
  description: string;
  language: string;
  code: string;
  hints: string[];
  realWorldExample: string;
  challenge: string;
}

// NEW: Famous Bug
export interface FamousBug {
  id: string;
  name: string;
  company: string;
  year: string;
  icon: string;
  description: string;
  whatHappened: string;
  rootCause: string;
  financialImpact: string;
  codePattern: string;
  lesson: string;
  tags: string[];
  sampleCode: string;
}

export interface EnhancedAnalysisResponse {
  id: string;
  score: number;
  findings: Finding[];
  improvedCode: string;
  summary: string;
  analyzedAt: number;
  predictedIncidents: ProductionIncident[];
  incidentTimeline: string;
  shipItScore: ShipItScore;
  costAnalysis: CostAnalysis;
  achievements: Achievement[];
  roast?: CodeRoast;
  // NEW fields
  famousBugMatches?: FamousBugMatch[];
  preMortem?: PreMortem;
  onCallForecast?: OnCallForecast;
  codeKarma?: CodeKarma;
}
