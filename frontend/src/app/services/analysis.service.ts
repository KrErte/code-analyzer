import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AnalysisRequest, AnalysisResponse, AnalysisHistory, Persona, Language } from '../models/analysis.model';

@Injectable({
  providedIn: 'root'
})
export class AnalysisService {
  private http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  private readonly HISTORY_KEY = 'code_analyzer_history';
  private readonly MAX_HISTORY = 20;

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private historySubject = new BehaviorSubject<AnalysisHistory[]>(this.loadHistory());
  history$ = this.historySubject.asObservable();

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

  getPersonas(): Observable<{ personas: Persona[] }> {
    return this.http.get<{ personas: Persona[] }>(`${this.apiUrl}/personas`);
  }

  getLanguages(): Observable<{ languages: Language[] }> {
    return this.http.get<{ languages: Language[] }>(`${this.apiUrl}/languages`);
  }

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

    // Keep only the last MAX_HISTORY entries
    const trimmed = history.slice(0, this.MAX_HISTORY);

    localStorage.setItem(this.HISTORY_KEY, JSON.stringify(trimmed));
    this.historySubject.next(trimmed);
  }

  getHistoryById(id: string): AnalysisHistory | undefined {
    return this.loadHistory().find(h => h.id === id);
  }

  clearHistory(): void {
    localStorage.removeItem(this.HISTORY_KEY);
    this.historySubject.next([]);
  }

  deleteHistoryItem(id: string): void {
    const history = this.loadHistory().filter(h => h.id !== id);
    localStorage.setItem(this.HISTORY_KEY, JSON.stringify(history));
    this.historySubject.next(history);
  }

  generateShareUrl(analysisId: string): string {
    // For MVP, we use a simple URL scheme
    // In production, this would create a shareable link via backend
    return `${window.location.origin}?analysis=${analysisId}`;
  }
}
