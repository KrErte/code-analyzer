import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CodeInputComponent } from './components/code-input/code-input.component';
import { PersonaSelectorComponent } from './components/persona-selector/persona-selector.component';
import { AnalysisResultsComponent } from './components/analysis-results/analysis-results.component';
import { HistoryPanelComponent } from './components/history-panel/history-panel.component';
import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { MultiFileResultsComponent } from './components/multi-file-results/multi-file-results.component';
import { AnalysisService } from './services/analysis.service';
import {
  AnalysisResponse,
  AnalysisHistory,
  Persona,
  Language,
  FileContent,
  MultiFileAnalysisResponse
} from './models/analysis.model';

type AnalysisMode = 'single' | 'multi';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CodeInputComponent,
    PersonaSelectorComponent,
    AnalysisResultsComponent,
    HistoryPanelComponent,
    FileUploadComponent,
    MultiFileResultsComponent
  ],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-gray-900 via-gray-900 to-gray-800">
      <!-- Header -->
      <header class="border-b border-gray-800 sticky top-0 bg-gray-900/95 backdrop-blur z-50">
        <div class="max-w-7xl mx-auto px-4 py-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <span class="text-2xl">🔍</span>
              <div>
                <h1 class="text-xl font-bold text-white">AI Code Logic Analyzer</h1>
                <p class="text-xs text-gray-500">Let a senior dev tear apart your code</p>
              </div>
            </div>

            <!-- Mode Switcher -->
            <div class="flex items-center gap-1 bg-gray-800 rounded-lg p-1">
              <button
                (click)="switchMode('single')"
                [class]="mode === 'single' ? 'bg-blue-600 text-white' : 'text-gray-400 hover:text-white'"
                class="px-4 py-2 rounded-md text-sm font-medium transition-all"
              >
                📝 Single File
              </button>
              <button
                (click)="switchMode('multi')"
                [class]="mode === 'multi' ? 'bg-gradient-to-r from-blue-600 to-purple-600 text-white' : 'text-gray-400 hover:text-white'"
                class="px-4 py-2 rounded-md text-sm font-medium transition-all"
              >
                📁 Multi-File
              </button>
            </div>

            <div class="text-xs text-gray-600">
              Powered by Claude AI
            </div>
          </div>
        </div>
      </header>

      <!-- Main Content -->
      <main class="max-w-7xl mx-auto px-4 py-8">
        <!-- Single File Mode -->
        @if (mode === 'single') {
          <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
            <aside class="lg:col-span-1 order-2 lg:order-1">
              <app-history-panel
                [history]="history"
                (onSelectHistory)="loadFromHistory($event)"
                (onClearHistory)="clearHistory()"
                (onDeleteItem)="deleteHistoryItem($event)"
              />
            </aside>

            <div class="lg:col-span-3 order-1 lg:order-2 space-y-6">
              @if (!analysisResult) {
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

                @if (error) {
                  <div class="bg-red-500/10 border border-red-500/30 rounded-xl p-4 text-red-400">
                    <div class="flex items-center gap-2">
                      <span>⚠️</span>
                      <span>{{ error }}</span>
                    </div>
                  </div>
                }
              } @else {
                <app-analysis-results
                  [result]="analysisResult"
                  (onNewAnalysis)="resetAnalysis()"
                />
              }
            </div>
          </div>
        }

        <!-- Multi-File Mode -->
        @if (mode === 'multi') {
          <div class="space-y-6">
            @if (!multiFileResult) {
              <div class="bg-gray-800/30 rounded-xl border border-gray-700 p-6 space-y-6">
                <app-file-upload
                  (filesChange)="files = $event"
                  (projectNameChange)="projectName = $event"
                />

                @if (files.length > 0) {
                  <div>
                    <label class="text-sm font-medium text-gray-300 block mb-2">
                      Context (optional)
                    </label>
                    <input
                      type="text"
                      [(ngModel)]="multiContext"
                      placeholder="What should this project do? Any specific concerns?"
                      class="w-full bg-gray-800 border border-gray-700 rounded-lg px-4 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  </div>

                  <app-persona-selector
                    [personas]="personas"
                    [selectedPersona]="selectedPersona"
                    (personaChange)="selectedPersona = $event"
                  />

                  <div class="flex items-center justify-between pt-4 border-t border-gray-700">
                    <p class="text-xs text-gray-500">
                      {{ files.length }} files ready for analysis
                    </p>
                    <button
                      (click)="analyzeMultiple()"
                      [disabled]="isLoading || files.length === 0"
                      class="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500
                             disabled:from-gray-700 disabled:to-gray-700 disabled:cursor-not-allowed
                             px-8 py-3 rounded-lg font-medium transition-all flex items-center gap-2 text-lg"
                    >
                      @if (isLoading) {
                        <span class="animate-spin">⚙️</span>
                        <span>Analyzing {{ files.length }} files...</span>
                      } @else {
                        <span>🚀</span>
                        <span>Analyze Project</span>
                      }
                    </button>
                  </div>
                }
              </div>

              @if (error) {
                <div class="bg-red-500/10 border border-red-500/30 rounded-xl p-4 text-red-400">
                  <div class="flex items-center gap-2">
                    <span>⚠️</span>
                    <span>{{ error }}</span>
                  </div>
                </div>
              }
            } @else {
              <app-multi-file-results
                [result]="multiFileResult"
                (onNewAnalysis)="resetMultiAnalysis()"
              />
            }
          </div>
        }
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

  mode: AnalysisMode = 'single';

  // Single file
  code = '';
  selectedLanguage = 'javascript';
  selectedPersona = 'brutal';
  context = '';
  analysisResult: AnalysisResponse | null = null;

  // Multi-file
  files: FileContent[] = [];
  projectName = '';
  multiContext = '';
  multiFileResult: MultiFileAnalysisResponse | null = null;

  // Shared
  personas: Persona[] = [];
  languages: Language[] = [];
  history: AnalysisHistory[] = [];
  isLoading = false;
  error = '';

  ngOnInit(): void {
    this.loadPersonas();
    this.loadLanguages();
    this.loadHistory();
    this.checkUrlParams();
  }

  switchMode(newMode: AnalysisMode): void {
    this.mode = newMode;
    this.error = '';
  }

  private loadPersonas(): void {
    this.analysisService.getPersonas().subscribe({
      next: (res) => this.personas = res.personas,
      error: () => {
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
      next: (res) => this.languages = res.languages,
      error: () => {
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
    this.analysisService.history$.subscribe(h => this.history = h);
  }

  private checkUrlParams(): void {
    const params = new URLSearchParams(window.location.search);
    const analysisId = params.get('analysis');
    const multiId = params.get('multi');

    if (analysisId) {
      const item = this.analysisService.getHistoryById(analysisId);
      if (item) this.loadFromHistory(item);
    } else if (multiId) {
      const item = this.analysisService.getMultiHistoryById(multiId);
      if (item) {
        this.mode = 'multi';
        this.multiFileResult = item.response;
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
      next: (res) => { this.analysisResult = res; this.isLoading = false; },
      error: (err) => { this.error = err.error?.message || 'Analysis failed.'; this.isLoading = false; }
    });
  }

  analyzeMultiple(): void {
    if (this.files.length === 0) return;
    this.isLoading = true;
    this.error = '';

    this.analysisService.analyzeMultiple({
      files: this.files,
      context: this.multiContext,
      persona: this.selectedPersona,
      projectName: this.projectName
    }).subscribe({
      next: (res) => { this.multiFileResult = res; this.isLoading = false; },
      error: (err) => { this.error = err.error?.message || 'Analysis failed.'; this.isLoading = false; }
    });
  }

  loadFromHistory(item: AnalysisHistory): void {
    this.code = item.code;
    this.selectedLanguage = item.language;
    this.selectedPersona = item.persona;
    this.analysisResult = item.response;
    this.mode = 'single';
  }

  resetAnalysis(): void { this.analysisResult = null; }
  resetMultiAnalysis(): void {
    this.multiFileResult = null;
    this.files = [];
    this.projectName = '';
    this.multiContext = '';
  }

  clearHistory(): void {
    if (confirm('Clear all history?')) this.analysisService.clearHistory();
  }

  deleteHistoryItem(id: string): void {
    this.analysisService.deleteHistoryItem(id);
  }
}
