import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CodeInputComponent } from './components/code-input/code-input.component';
import { PersonaSelectorComponent } from './components/persona-selector/persona-selector.component';
import { AnalysisResultsComponent } from './components/analysis-results/analysis-results.component';
import { HistoryPanelComponent } from './components/history-panel/history-panel.component';
import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { MultiFileResultsComponent } from './components/multi-file-results/multi-file-results.component';
import { EnhancedResultsComponent } from './components/enhanced-results/enhanced-results.component';
import { DemoPlaygroundComponent } from './components/demo-playground/demo-playground.component';
import { AnalysisService } from './services/analysis.service';
import { AnalysisResponse, AnalysisHistory, Persona, Language, FileContent, MultiFileAnalysisResponse } from './models/analysis.model';
import { EnhancedAnalysisResponse } from './models/enhanced.model';

type AnalysisMode = 'single' | 'multi' | 'demo';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, CodeInputComponent, PersonaSelectorComponent, AnalysisResultsComponent, HistoryPanelComponent, FileUploadComponent, MultiFileResultsComponent, EnhancedResultsComponent, DemoPlaygroundComponent],
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
                <p class="text-xs text-gray-500">Production Incident Predictor • Ship-It Score • Achievements</p>
              </div>
            </div>

            <div class="flex items-center gap-4">
              <!-- Mode Switcher -->
              <div class="flex items-center gap-1 bg-gray-800 rounded-lg p-1">
                <button (click)="switchMode('single')" [class]="mode === 'single' ? 'bg-gradient-to-r from-blue-600 to-purple-600 text-white' : 'text-gray-400 hover:text-white'" class="px-4 py-2 rounded-md text-sm font-medium transition-all">
                  🚀 Enhanced
                </button>
                <button (click)="switchMode('multi')" [class]="mode === 'multi' ? 'bg-gradient-to-r from-purple-600 to-pink-600 text-white' : 'text-gray-400 hover:text-white'" class="px-4 py-2 rounded-md text-sm font-medium transition-all">
                  📁 Multi-File
                </button>
                <button (click)="switchMode('demo')" [class]="mode === 'demo' ? 'bg-gradient-to-r from-orange-600 to-red-600 text-white' : 'text-gray-400 hover:text-white'" class="px-4 py-2 rounded-md text-sm font-medium transition-all">
                  🎮 Playground
                </button>
              </div>
              <div class="text-xs text-gray-600">Powered by Claude AI</div>
            </div>
          </div>
        </div>
      </header>

      <main class="max-w-7xl mx-auto px-4 py-8">
        <!-- Enhanced Single File Mode -->
        @if (mode === 'single') {
          <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
            <aside class="lg:col-span-1 order-2 lg:order-1">
              <app-history-panel [history]="history" (onSelectHistory)="loadFromHistory($event)" (onClearHistory)="clearHistory()" (onDeleteItem)="deleteHistoryItem($event)"/>
            </aside>

            <div class="lg:col-span-3 order-1 lg:order-2 space-y-6">
              @if (!enhancedResult) {
                <div class="bg-gray-800/30 rounded-xl border border-gray-700 p-6 space-y-6">
                  <!-- Feature Banner -->
                  <div class="bg-gradient-to-r from-blue-900/30 to-purple-900/30 rounded-lg p-4 border border-blue-500/20">
                    <div class="flex items-center gap-4">
                      <div class="text-3xl">🔮</div>
                      <div>
                        <h3 class="font-bold text-blue-300">Enhanced Analysis Mode</h3>
                        <p class="text-sm text-gray-400">Predicts production incidents • Estimates $ cost • Awards achievements</p>
                      </div>
                    </div>
                  </div>

                  <app-code-input [languages]="languages" [code]="code" [selectedLanguage]="selectedLanguage" [context]="context" (codeChange)="code = $event" (languageChange)="selectedLanguage = $event" (contextChange)="context = $event"/>

                  <app-persona-selector [personas]="personas" [selectedPersona]="selectedPersona" (personaChange)="selectedPersona = $event"/>

                  <div class="flex items-center justify-between pt-4 border-t border-gray-700">
                    <div class="flex items-center gap-4">
                      <p class="text-xs text-gray-500">{{ selectedPersona === 'roast' ? '🔥 Roast Mode: Prepare to be destroyed' : 'Enhanced analysis with incident prediction' }}</p>
                    </div>
                    <button (click)="analyzeEnhanced()" [disabled]="isLoading || !code.trim()" class="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 disabled:from-gray-700 disabled:to-gray-700 px-8 py-3 rounded-lg font-bold transition-all flex items-center gap-2 text-lg">
                      @if (isLoading) {
                        <span class="animate-spin">🔮</span><span>Predicting Incidents...</span>
                      } @else {
                        <span>{{ selectedPersona === 'roast' ? '🔥' : '🚀' }}</span><span>{{ selectedPersona === 'roast' ? 'Roast My Code' : 'Analyze & Predict' }}</span>
                      }
                    </button>
                  </div>
                </div>

                @if (error) {
                  <div class="bg-red-500/10 border border-red-500/30 rounded-xl p-4 text-red-400">
                    <span>⚠️ {{ error }}</span>
                  </div>
                }
              } @else {
                <app-enhanced-results [result]="enhancedResult" (onNewAnalysis)="resetEnhancedAnalysis()"/>
              }
            </div>
          </div>
        }

        <!-- Multi-File Mode -->
        @if (mode === 'multi') {
          <div class="space-y-6">
            @if (!multiFileResult) {
              <div class="bg-gray-800/30 rounded-xl border border-gray-700 p-6 space-y-6">
                <app-file-upload (filesChange)="files = $event" (projectNameChange)="projectName = $event"/>

                @if (files.length > 0) {
                  <div>
                    <label class="text-sm font-medium text-gray-300 block mb-2">Context (optional)</label>
                    <input type="text" [(ngModel)]="multiContext" placeholder="What should this project do?" class="w-full bg-gray-800 border border-gray-700 rounded-lg px-4 py-2 text-sm"/>
                  </div>

                  <app-persona-selector [personas]="personas" [selectedPersona]="selectedPersona" (personaChange)="selectedPersona = $event"/>

                  <div class="flex items-center justify-between pt-4 border-t border-gray-700">
                    <p class="text-xs text-gray-500">{{ files.length }} files ready</p>
                    <button (click)="analyzeMultiple()" [disabled]="isLoading" class="bg-gradient-to-r from-purple-600 to-pink-600 px-8 py-3 rounded-lg font-bold">
                      @if (isLoading) { <span class="animate-spin">⚙️</span> } @else { 🚀 } Analyze Project
                    </button>
                  </div>
                }
              </div>

              @if (error) {
                <div class="bg-red-500/10 border border-red-500/30 rounded-xl p-4 text-red-400">⚠️ {{ error }}</div>
              }
            } @else {
              <app-multi-file-results [result]="multiFileResult" (onNewAnalysis)="resetMultiAnalysis()"/>
            }
          </div>
        }

        <!-- Demo Playground Mode -->
        @if (mode === 'demo') {
          <app-demo-playground (onSelectCode)="loadDemoCode($event)"/>
        }
      </main>

      <footer class="border-t border-gray-800 mt-12">
        <div class="max-w-7xl mx-auto px-4 py-6">
          <div class="flex items-center justify-between text-xs text-gray-600">
            <p>🔮 Incident Predictor • 💀 Famous Bug Matcher • 📋 Pre-Mortem • 📟 On-Call Forecast • ☯️ Code Karma • 🎮 Bug Playground</p>
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
  code = '';
  selectedLanguage = 'javascript';
  selectedPersona = 'brutal';
  context = '';

  // Results
  enhancedResult: EnhancedAnalysisResponse | null = null;
  multiFileResult: MultiFileAnalysisResponse | null = null;

  // Multi-file
  files: FileContent[] = [];
  projectName = '';
  multiContext = '';

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
          { id: 'brutal', name: 'Brutal Senior', description: 'Harshly critical, no mercy.' },
          { id: 'mentor', name: 'Constructive Mentor', description: 'Critical but educational.' },
          { id: 'edge-hunter', name: 'Edge Case Hunter', description: 'Boundary conditions expert.' },
          { id: 'roast', name: 'Code Roast 🔥', description: 'Savage, meme-worthy roasts.' }
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

  analyzeEnhanced(): void {
    if (!this.code.trim()) return;
    this.isLoading = true;
    this.error = '';

    this.analysisService.analyzeEnhanced({
      code: this.code,
      language: this.selectedLanguage,
      context: this.context,
      persona: this.selectedPersona
    }).subscribe({
      next: (res) => { this.enhancedResult = res; this.isLoading = false; },
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
    this.mode = 'single';
  }

  resetEnhancedAnalysis(): void { this.enhancedResult = null; }
  resetMultiAnalysis(): void { this.multiFileResult = null; this.files = []; }

  loadDemoCode(event: { code: string; language: string }): void {
    this.code = event.code;
    this.selectedLanguage = event.language;
    this.mode = 'single';
    this.enhancedResult = null;
  }

  clearHistory(): void {
    if (confirm('Clear all history?')) this.analysisService.clearHistory();
  }

  deleteHistoryItem(id: string): void {
    this.analysisService.deleteHistoryItem(id);
  }
}
