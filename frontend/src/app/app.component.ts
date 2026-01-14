import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CodeInputComponent } from './components/code-input/code-input.component';
import { PersonaSelectorComponent } from './components/persona-selector/persona-selector.component';
import { AnalysisResultsComponent } from './components/analysis-results/analysis-results.component';
import { HistoryPanelComponent } from './components/history-panel/history-panel.component';
import { AnalysisService } from './services/analysis.service';
import { AnalysisResponse, AnalysisHistory, Persona, Language } from './models/analysis.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    CodeInputComponent,
    PersonaSelectorComponent,
    AnalysisResultsComponent,
    HistoryPanelComponent
  ],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-900 via-gray-900 to-gray-800">
      <!-- Header -->
      <header class="border-b border-gray-800">
        <div class="max-w-7xl mx-auto px-4 py-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <span class="text-2xl">🔍</span>
              <div>
                <h1 class="text-xl font-bold text-white">AI Code Logic Analyzer</h1>
                <p class="text-xs text-gray-500">Let a senior dev tear apart your code</p>
              </div>
            </div>
            <div class="text-xs text-gray-600">
              Powered by Claude AI
            </div>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <main class="max-w-7xl mx-auto px-4 py-8">
        <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
          <!-- Sidebar - History -->
          <aside class="lg:col-span-1 order-2 lg:order-1">
            <app-history-panel
              [history]="history"
              (onSelectHistory)="loadFromHistory($event)"
              (onClearHistory)="clearHistory()"
              (onDeleteItem)="deleteHistoryItem($event)"
            />
          </aside>

          <!-- Main Panel -->
          <div class="lg:col-span-3 order-1 lg:order-2 space-y-6">
            @if (!analysisResult) {
              <!-- Input Form -->
              <div class="bg-gray-800/30 rounded-xl border border-gray-700 p-6 space-y-6">
                <app-code-input
                  [languages]="languages"
                  [code]="code"
                  [selectedLanguage]="selectedLanguage"
                  [context]="context"
                  (codeChange)="code = $event"
                  (languageChange)="selectedLanguage = $event"
                  (contextChange)="context = $event"
                />

                <app-persona-selector
                  [personas]="personas"
                  [selectedPersona]="selectedPersona"
                  (personaChange)="selectedPersona = $event"
                />

                <div class="flex items-center justify-between pt-4 border-t border-gray-700">
                  <p class="text-xs text-gray-500">
                    Your code is analyzed in real-time and never stored on our servers.
                  </p>
                  <button
                    (click)="analyze()"
                    [disabled]="isLoading || !code.trim()"
                    class="bg-blue-600 hover:bg-blue-500 disabled:bg-gray-700 disabled:cursor-not-allowed
                           px-6 py-2.5 rounded-lg font-medium transition-colors flex items-center gap-2"
                  >
                    @if (isLoading) {
                      <span class="loading-pulse">Analyzing...</span>
                    } @else {
                      <span>🔬</span>
                      <span>Analyze Code</span>
                    }
                  </button>
                </div>
              </div>

              <!-- Error Message -->
              @if (error) {
                <div class="bg-red-500/10 border border-red-500/30 rounded-xl p-4 text-red-400">
                  <div class="flex items-center gap-2">
                    <span>⚠️</span>
                    <span>{{ error }}</span>
                  </div>
                </div>
              }
            } @else {
              <!-- Results -->
              <app-analysis-results
                [result]="analysisResult"
                (onNewAnalysis)="resetAnalysis()"
              />
            }
          </div>
        </div>
      </main>

      <!-- Footer -->
      <footer class="border-t border-gray-800 mt-12">
        <div class="max-w-7xl mx-auto px-4 py-6">
          <div class="flex items-center justify-between text-xs text-gray-600">
            <p>AI Code Logic Analyzer - Find bugs before they find you</p>
            <p>Built with Spring Boot + Angular + Claude AI</p>
          </div>
        </div>
      </footer>
    </div>
  `
})
export class AppComponent implements OnInit {
  private analysisService = inject(AnalysisService);

  code = '';
  selectedLanguage = 'javascript';
  selectedPersona = 'brutal';
  context = '';

  personas: Persona[] = [];
  languages: Language[] = [];
  history: AnalysisHistory[] = [];

  analysisResult: AnalysisResponse | null = null;
  isLoading = false;
  error = '';

  ngOnInit(): void {
    this.loadPersonas();
    this.loadLanguages();
    this.loadHistory();
    this.checkUrlForSharedAnalysis();
  }

  private loadPersonas(): void {
    this.analysisService.getPersonas().subscribe({
      next: (response) => {
        this.personas = response.personas;
      },
      error: () => {
        // Fallback personas
        this.personas = [
          { id: 'brutal', name: 'Brutal Senior', description: 'Harshly critical, finds every possible flaw.' },
          { id: 'mentor', name: 'Constructive Mentor', description: 'Critical but educational.' },
          { id: 'edge-hunter', name: 'Edge Case Hunter', description: 'Focuses on boundary conditions.' }
        ];
      }
    });
  }

  private loadLanguages(): void {
    this.analysisService.getLanguages().subscribe({
      next: (response) => {
        this.languages = response.languages;
      },
      error: () => {
        // Fallback languages
        this.languages = [
          { id: 'java', name: 'Java' },
          { id: 'javascript', name: 'JavaScript' },
          { id: 'typescript', name: 'TypeScript' },
          { id: 'python', name: 'Python' }
        ];
      }
    });
  }

  private loadHistory(): void {
    this.analysisService.history$.subscribe(history => {
      this.history = history;
    });
  }

  private checkUrlForSharedAnalysis(): void {
    const params = new URLSearchParams(window.location.search);
    const analysisId = params.get('analysis');
    if (analysisId) {
      const historyItem = this.analysisService.getHistoryById(analysisId);
      if (historyItem) {
        this.loadFromHistory(historyItem);
      }
    }
  }

  analyze(): void {
    if (!this.code.trim()) return;

    this.isLoading = true;
    this.error = '';

    this.analysisService.analyze({
      code: this.code,
      language: this.selectedLanguage,
      context: this.context,
      persona: this.selectedPersona
    }).subscribe({
      next: (response) => {
        this.analysisResult = response;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Analysis failed. Please try again.';
        this.isLoading = false;
      }
    });
  }

  loadFromHistory(item: AnalysisHistory): void {
    this.code = item.code;
    this.selectedLanguage = item.language;
    this.selectedPersona = item.persona;
    this.analysisResult = item.response;
  }

  resetAnalysis(): void {
    this.analysisResult = null;
  }

  clearHistory(): void {
    if (confirm('Are you sure you want to clear all history?')) {
      this.analysisService.clearHistory();
    }
  }

  deleteHistoryItem(id: string): void {
    this.analysisService.deleteHistoryItem(id);
  }
}
