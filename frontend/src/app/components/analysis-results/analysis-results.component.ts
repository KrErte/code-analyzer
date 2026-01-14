import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalysisResponse, Finding } from '../../models/analysis.model';

@Component({
  selector: 'app-analysis-results',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (result) {
      <div class="space-y-6">
        <!-- Score Section -->
        <div class="bg-gray-800/50 rounded-xl p-6 border border-gray-700">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="text-lg font-semibold mb-1">Bug Likelihood Score</h3>
              <p class="text-sm text-gray-400">{{ getScoreDescription(result.score) }}</p>
            </div>
            <div class="relative w-24 h-24">
              <svg class="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                <circle
                  cx="50" cy="50" r="45"
                  stroke="currentColor"
                  stroke-width="8"
                  fill="none"
                  class="text-gray-700"
                />
                <circle
                  cx="50" cy="50" r="45"
                  stroke="currentColor"
                  stroke-width="8"
                  fill="none"
                  [class]="getScoreColorClass(result.score)"
                  stroke-linecap="round"
                  [style.stroke-dasharray]="283"
                  [style.stroke-dashoffset]="283 - (283 * result.score / 100)"
                  class="score-ring"
                />
              </svg>
              <div class="absolute inset-0 flex items-center justify-center">
                <span class="text-2xl font-bold" [class]="getScoreTextClass(result.score)">
                  {{ result.score }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Summary -->
        <div class="bg-gray-800/50 rounded-xl p-6 border border-gray-700">
          <h3 class="text-lg font-semibold mb-3">Summary</h3>
          <p class="text-gray-300 leading-relaxed">{{ result.summary }}</p>
        </div>

        <!-- Findings -->
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold">Findings</h3>
            <div class="flex gap-2 text-xs">
              <span class="badge-critical px-2 py-1 rounded">
                {{ getCriticalCount() }} Critical
              </span>
              <span class="badge-warning px-2 py-1 rounded">
                {{ getWarningCount() }} Warnings
              </span>
              <span class="badge-suggestion px-2 py-1 rounded">
                {{ getSuggestionCount() }} Suggestions
              </span>
            </div>
          </div>

          @for (finding of result.findings; track $index) {
            <div class="bg-gray-800/50 rounded-xl border overflow-hidden"
                 [class]="getFindingBorderClass(finding.severity)">
              <div class="px-4 py-3 flex items-center gap-3"
                   [class]="getFindingHeaderClass(finding.severity)">
                <span class="text-lg">{{ getSeverityIcon(finding.severity) }}</span>
                <div class="flex-1">
                  <div class="flex items-center gap-2">
                    <span class="font-semibold">{{ finding.issue }}</span>
                    @if (finding.line) {
                      <span class="text-xs bg-gray-700 px-2 py-0.5 rounded">
                        Line {{ finding.line }}
                      </span>
                    }
                  </div>
                </div>
                <span class="text-xs uppercase tracking-wide opacity-75">
                  {{ finding.severity }}
                </span>
              </div>
              <div class="p-4 space-y-3">
                <div>
                  <h4 class="text-xs uppercase tracking-wide text-gray-500 mb-1">Explanation</h4>
                  <p class="text-sm text-gray-300">{{ finding.explanation }}</p>
                </div>
                @if (finding.suggestion) {
                  <div>
                    <h4 class="text-xs uppercase tracking-wide text-gray-500 mb-1">Suggestion</h4>
                    <p class="text-sm text-gray-300 font-mono bg-gray-900/50 p-3 rounded">
                      {{ finding.suggestion }}
                    </p>
                  </div>
                }
              </div>
            </div>
          }
        </div>

        <!-- Improved Code -->
        @if (result.improvedCode) {
          <div class="bg-gray-800/50 rounded-xl border border-gray-700 overflow-hidden">
            <div class="px-4 py-3 bg-gray-700/50 flex items-center justify-between">
              <h3 class="font-semibold">Improved Code</h3>
              <button
                (click)="copyCode()"
                class="text-xs bg-blue-600 hover:bg-blue-500 px-3 py-1.5 rounded transition-colors"
              >
                {{ copied ? 'Copied!' : 'Copy Code' }}
              </button>
            </div>
            <pre class="p-4 overflow-x-auto text-sm"><code class="font-mono text-gray-300">{{ result.improvedCode }}</code></pre>
          </div>
        }

        <!-- Actions -->
        <div class="flex items-center justify-between pt-4 border-t border-gray-700">
          <button
            (click)="onNewAnalysis.emit()"
            class="text-sm text-gray-400 hover:text-white transition-colors"
          >
            ← Analyze Another
          </button>
          <button
            (click)="shareResult()"
            class="text-sm bg-gray-700 hover:bg-gray-600 px-4 py-2 rounded-lg transition-colors"
          >
            Share Result
          </button>
        </div>
      </div>
    }
  `
})
export class AnalysisResultsComponent {
  @Input() result: AnalysisResponse | null = null;
  @Output() onNewAnalysis = new EventEmitter<void>();

  copied = false;

  getCriticalCount(): number {
    return this.result?.findings.filter(f => f.severity === 'critical').length || 0;
  }

  getWarningCount(): number {
    return this.result?.findings.filter(f => f.severity === 'warning').length || 0;
  }

  getSuggestionCount(): number {
    return this.result?.findings.filter(f => f.severity === 'suggestion').length || 0;
  }

  getScoreDescription(score: number): string {
    if (score <= 20) return 'Solid code with minor suggestions';
    if (score <= 40) return 'Some issues but generally functional';
    if (score <= 60) return 'Multiple problems that need attention';
    if (score <= 80) return 'Significant issues, high risk of bugs';
    return 'Critical problems, should not go to production';
  }

  getScoreColorClass(score: number): string {
    if (score <= 20) return 'text-emerald-500';
    if (score <= 40) return 'text-blue-500';
    if (score <= 60) return 'text-amber-500';
    if (score <= 80) return 'text-orange-500';
    return 'text-red-500';
  }

  getScoreTextClass(score: number): string {
    if (score <= 20) return 'text-emerald-400';
    if (score <= 40) return 'text-blue-400';
    if (score <= 60) return 'text-amber-400';
    if (score <= 80) return 'text-orange-400';
    return 'text-red-400';
  }

  getSeverityIcon(severity: string): string {
    const icons: Record<string, string> = {
      critical: '🚨',
      warning: '⚠️',
      suggestion: '💡'
    };
    return icons[severity] || '📌';
  }

  getFindingBorderClass(severity: string): string {
    const classes: Record<string, string> = {
      critical: 'border-red-500/30',
      warning: 'border-amber-500/30',
      suggestion: 'border-blue-500/30'
    };
    return classes[severity] || 'border-gray-700';
  }

  getFindingHeaderClass(severity: string): string {
    const classes: Record<string, string> = {
      critical: 'bg-red-500/10',
      warning: 'bg-amber-500/10',
      suggestion: 'bg-blue-500/10'
    };
    return classes[severity] || 'bg-gray-700/50';
  }

  copyCode(): void {
    if (this.result?.improvedCode) {
      navigator.clipboard.writeText(this.result.improvedCode);
      this.copied = true;
      setTimeout(() => this.copied = false, 2000);
    }
  }

  shareResult(): void {
    if (this.result) {
      const url = `${window.location.origin}?analysis=${this.result.id}`;
      navigator.clipboard.writeText(url);
      alert('Share URL copied to clipboard!');
    }
  }
}
