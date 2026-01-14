export interface AnalysisRequest {
  code: string;
  language: string;
  context?: string;
  persona: string;
}

export interface Finding {
  severity: 'critical' | 'warning' | 'suggestion';
  line: number | null;
  issue: string;
  explanation: string;
  suggestion: string;
}

export interface AnalysisResponse {
  id: string;
  score: number;
  findings: Finding[];
  improvedCode: string;
  summary: string;
  analyzedAt: number;
}

export interface Persona {
  id: string;
  name: string;
  description: string;
  icon?: string;
}

export interface Language {
  id: string;
  name: string;
}

export interface AnalysisHistory {
  id: string;
  code: string;
  language: string;
  persona: string;
  response: AnalysisResponse;
  createdAt: number;
}
