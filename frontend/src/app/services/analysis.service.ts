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
import { EnhancedAnalysisResponse } from '../models/enhanced.model';

@Injectable({
  providedIn: 'root'
})
export class AnalysisService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  private readonly HISTORY_KEY = 'code_analyzer_history';
  private readonly MULTI_HISTORY_KEY = 'code_analyzer_multi_history';
  private readonly ENHANCED_HISTORY_KEY = 'code_analyzer_enhanced_history';
  private readonly MAX_HISTORY = 20;

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private historySubject = new BehaviorSubject<AnalysisHistory[]>(this.loadHistory());
  history$ = this.historySubject.asObservable();

  private multiHistorySubject = new BehaviorSubject<MultiFileHistory[]>(this.loadMultiHistory());
  multiHistory$ = this.multiHistorySubject.asObservable();

  // Basic analysis
  analyze(request: AnalysisRequest): Observable<AnalysisResponse> {
    this.loadingSubject.next(true);
    return this.http.post<AnalysisResponse>(`${this.apiUrl}/analyze`, request).pipe(
      tap({
        next: (response) => {
          this.saveToHistory({ id: response.id, code: request.code, language: request.language, persona: request.persona, response, createdAt: Date.now() });
          this.loadingSubject.next(false);
        },
        error: () => this.loadingSubject.next(false)
      })
    );
  }

  // Enhanced analysis with incidents, costs, achievements
  analyzeEnhanced(request: AnalysisRequest): Observable<EnhancedAnalysisResponse> {
    this.loadingSubject.next(true);
    return this.http.post<EnhancedAnalysisResponse>(`${this.apiUrl}/analyze/enhanced`, request).pipe(
      tap({
        next: (response) => {
          this.saveEnhancedHistory({ id: response.id, code: request.code, language: request.language, persona: request.persona, response, createdAt: Date.now() });
          this.loadingSubject.next(false);
        },
        error: () => this.loadingSubject.next(false)
      })
    );
  }

  // Multi-file analysis
  analyzeMultiple(request: MultiFileAnalysisRequest): Observable<MultiFileAnalysisResponse> {
    this.loadingSubject.next(true);
    return this.http.post<MultiFileAnalysisResponse>(`${this.apiUrl}/analyze/multi`, request).pipe(
      tap({
        next: (response) => {
          this.saveToMultiHistory({ id: response.id, files: request.files, persona: request.persona, projectName: request.projectName, response, createdAt: Date.now() });
          this.loadingSubject.next(false);
        },
        error: () => this.loadingSubject.next(false)
      })
    );
  }

  getPersonas(): Observable<{ personas: Persona[] }> {
    return this.http.get<{ personas: Persona[] }>(`${this.apiUrl}/personas`);
  }

  getLanguages(): Observable<{ languages: Language[] }> {
    return this.http.get<{ languages: Language[] }>(`${this.apiUrl}/languages`);
  }

  // History management
  private loadHistory(): AnalysisHistory[] {
    try { return JSON.parse(localStorage.getItem(this.HISTORY_KEY) || '[]'); }
    catch { return []; }
  }

  private saveToHistory(entry: AnalysisHistory): void {
    const history = this.loadHistory();
    history.unshift(entry);
    localStorage.setItem(this.HISTORY_KEY, JSON.stringify(history.slice(0, this.MAX_HISTORY)));
    this.historySubject.next(history.slice(0, this.MAX_HISTORY));
  }

  private loadMultiHistory(): MultiFileHistory[] {
    try { return JSON.parse(localStorage.getItem(this.MULTI_HISTORY_KEY) || '[]'); }
    catch { return []; }
  }

  private saveToMultiHistory(entry: MultiFileHistory): void {
    const history = this.loadMultiHistory();
    history.unshift(entry);
    localStorage.setItem(this.MULTI_HISTORY_KEY, JSON.stringify(history.slice(0, this.MAX_HISTORY)));
    this.multiHistorySubject.next(history.slice(0, this.MAX_HISTORY));
  }

  private loadEnhancedHistory(): any[] {
    try { return JSON.parse(localStorage.getItem(this.ENHANCED_HISTORY_KEY) || '[]'); }
    catch { return []; }
  }

  private saveEnhancedHistory(entry: any): void {
    const history = this.loadEnhancedHistory();
    history.unshift(entry);
    localStorage.setItem(this.ENHANCED_HISTORY_KEY, JSON.stringify(history.slice(0, this.MAX_HISTORY)));
  }

  getHistoryById(id: string): AnalysisHistory | undefined {
    return this.loadHistory().find(h => h.id === id);
  }

  getMultiHistoryById(id: string): MultiFileHistory | undefined {
    return this.loadMultiHistory().find(h => h.id === id);
  }

  getEnhancedHistoryById(id: string): any | undefined {
    return this.loadEnhancedHistory().find(h => h.id === id);
  }

  clearHistory(): void {
    localStorage.removeItem(this.HISTORY_KEY);
    localStorage.removeItem(this.MULTI_HISTORY_KEY);
    localStorage.removeItem(this.ENHANCED_HISTORY_KEY);
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

  detectLanguage(filename: string): string {
    const ext = filename.split('.').pop()?.toLowerCase();
    const langMap: Record<string, string> = { java: 'java', js: 'javascript', jsx: 'javascript', ts: 'typescript', tsx: 'typescript', py: 'python', pyw: 'python' };
    return langMap[ext || ''] || 'javascript';
  }

  generateShareUrl(analysisId: string): string {
    return `${window.location.origin}?analysis=${analysisId}`;
  }
}
