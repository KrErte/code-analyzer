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

// Multi-file types
export interface FileContent {
  filename: string;
  path?: string;
  content: string;
  language: string;
}

export interface MultiFileAnalysisRequest {
  files: FileContent[];
  context?: string;
  persona: string;
  projectName?: string;
}

export interface FileFinding {
  filename: string;
  findings: Finding[];
  fileScore: number;
}

export interface CrossFileIssue {
  issue: string;
  explanation: string;
  affectedFiles: string[];
  suggestion: string;
}

export interface ArchitectureReview {
  overview: string;
  strengths: string[];
  concerns: string[];
  recommendations: string[];
}

export interface MultiFileAnalysisResponse {
  id: string;
  overallScore: number;
  summary: string;
  fileFindings: FileFinding[];
  crossFileIssues: CrossFileIssue[];
  architectureReview: ArchitectureReview;
  analyzedAt: number;
  totalFiles: number;
  totalFindings: number;
}

export interface MultiFileHistory {
  id: string;
  files: FileContent[];
  persona: string;
  projectName?: string;
  response: MultiFileAnalysisResponse;
  createdAt: number;
}
