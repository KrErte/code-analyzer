import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AnalysisRequest,
  AnalysisResponse,
  AnalysisHistory,
  Persona,
  Language,
  MultiFileAnalysisRequest,
  MultiFileAnalysisResponse,
  MultiFileHistory,
  FileContent
} from '../models/analysis.model';

@Injectable({
  providedIn: 'root'
})
export class AnalysisService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  private readonly HISTORY_KEY = 'code_analyzer_history';
  private readonly MULTI_HISTORY_KEY = 'code_analyzer_multi_history';
  private readonly MAX_HISTORY = 20;

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private historySubject = new BehaviorSubject<AnalysisHistory[]>(this.loadHistory());
  history$ = this.historySubject.asObservable();

  private multiHistorySubject = new BehaviorSubject<MultiFileHistory[]>(this.loadMultiHistory());
  multiHistory$ = this.multiHistorySubject.asObservable();

  // Single file analysis
  analyze(request: AnalysisRequest): Observable<AnalysisResponse> {
    this.loadingSubject.next(true);

    return this.http.post<AnalysisResponse>(`${this.apiUrl}/analyze`, request).pipe(
      tap({
        next: (response) => {
          this.saveToHistory({
            id: response.id,
            code: request.code,
            language: request.language,
            persona: request.persona,
            response,
            createdAt: Date.now()
          });
          this.loadingSubject.next(false);
        },
        error: () => {
          this.loadingSubject.next(false);
        }
      })
    );
  }

  // Multi-file analysis
  analyzeMultiple(request: MultiFileAnalysisRequest): Observable<MultiFileAnalysisResponse> {
    this.loadingSubject.next(true);

    return this.http.post<MultiFileAnalysisResponse>(`${this.apiUrl}/analyze/multi`, request).pipe(
      tap({
        next: (response) => {
          this.saveToMultiHistory({
            id: response.id,
            files: request.files,
            persona: request.persona,
            projectName: request.projectName,
            response,
            createdAt: Date.now()
          });
          this.loadingSubject.next(false);
        },
        error: () => {
          this.loadingSubject.next(false);
        }
      })
    );
  }

  getPersonas(): Observable<{ personas: Persona[] }> {
    return this.http.get<{ personas: Persona[] }>(`${this.apiUrl}/personas`);
  }

  getLanguages(): Observable<{ languages: Language[] }> {
    return this.http.get<{ languages: Language[] }>(`${this.apiUrl}/languages`);
  }

  // Single file history
  private loadHistory(): AnalysisHistory[] {
    try {
      const stored = localStorage.getItem(this.HISTORY_KEY);
      return stored ? JSON.parse(stored) : [];
    } catch {
      return [];
    }
  }

  private saveToHistory(entry: AnalysisHistory): void {
    const history = this.loadHistory();
    history.unshift(entry);
    const trimmed = history.slice(0, this.MAX_HISTORY);
    localStorage.setItem(this.HISTORY_KEY, JSON.stringify(trimmed));
    this.historySubject.next(trimmed);
  }

  // Multi-file history
  private loadMultiHistory(): MultiFileHistory[] {
    try {
      const stored = localStorage.getItem(this.MULTI_HISTORY_KEY);
      return stored ? JSON.parse(stored) : [];
    } catch {
      return [];
    }
  }

  private saveToMultiHistory(entry: MultiFileHistory): void {
    const history = this.loadMultiHistory();
    history.unshift(entry);
    const trimmed = history.slice(0, this.MAX_HISTORY);
    localStorage.setItem(this.MULTI_HISTORY_KEY, JSON.stringify(trimmed));
    this.multiHistorySubject.next(trimmed);
  }

  getHistoryById(id: string): AnalysisHistory | undefined {
    return this.loadHistory().find(h => h.id === id);
  }

  getMultiHistoryById(id: string): MultiFileHistory | undefined {
    return this.loadMultiHistory().find(h => h.id === id);
  }

  clearHistory(): void {
    localStorage.removeItem(this.HISTORY_KEY);
    localStorage.removeItem(this.MULTI_HISTORY_KEY);
    this.historySubject.next([]);
    this.multiHistorySubject.next([]);
  }

  deleteHistoryItem(id: string): void {
    const history = this.loadHistory().filter(h => h.id !== id);
    localStorage.setItem(this.HISTORY_KEY, JSON.stringify(history));
    this.historySubject.next(history);
  }

  deleteMultiHistoryItem(id: string): void {
    const history = this.loadMultiHistory().filter(h => h.id !== id);
    localStorage.setItem(this.MULTI_HISTORY_KEY, JSON.stringify(history));
    this.multiHistorySubject.next(history);
  }

  // Utility: detect language from filename
  detectLanguage(filename: string): string {
    const ext = filename.split('.').pop()?.toLowerCase();
    const langMap: Record<string, string> = {
      'java': 'java',
      'js': 'javascript',
      'jsx': 'javascript',
      'ts': 'typescript',
      'tsx': 'typescript',
      'py': 'python',
      'pyw': 'python'
    };
    return langMap[ext || ''] || 'javascript';
  }

  generateShareUrl(analysisId: string): string {
    return `${window.location.origin}?analysis=${analysisId}`;
  }
}
