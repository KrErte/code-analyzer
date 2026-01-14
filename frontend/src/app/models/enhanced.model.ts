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
}
