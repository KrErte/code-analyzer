import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MultiFileAnalysisResponse, FileFinding, CrossFileIssue } from '../../models/analysis.model';

@Component({
  selector: 'app-multi-file-results',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (result) {
      <div class="space-y-6">
        <!-- Header Stats -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <!-- Overall Score -->
          <div class="bg-gradient-to-br from-gray-800 to-gray-900 rounded-xl p-4 border border-gray-700">
            <div class="flex items-center justify-between">
              <span class="text-sm text-gray-400">Bug Score</span>
              <div class="relative w-16 h-16">
                <svg class="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                  <circle cx="50" cy="50" r="40" stroke="currentColor" stroke-width="8" fill="none" class="text-gray-700"/>
                  <circle cx="50" cy="50" r="40" stroke="currentColor" stroke-width="8" fill="none"
                    [class]="getScoreColorClass(result.overallScore)"
                    stroke-linecap="round"
                    [style.stroke-dasharray]="251"
                    [style.stroke-dashoffset]="251 - (251 * result.overallScore / 100)"
                    class="transition-all duration-1000"/>
                </svg>
                <div class="absolute inset-0 flex items-center justify-center">
                  <span class="text-xl font-bold" [class]="getScoreTextClass(result.overallScore)">
                    {{ result.overallScore }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-gray-800/50 rounded-xl p-4 border border-gray-700">
            <div class="text-3xl font-bold text-blue-400">{{ result.totalFiles }}</div>
            <div class="text-sm text-gray-400">Files Analyzed</div>
          </div>

          <div class="bg-gray-800/50 rounded-xl p-4 border border-gray-700">
            <div class="text-3xl font-bold text-amber-400">{{ result.totalFindings }}</div>
            <div class="text-sm text-gray-400">Total Issues</div>
          </div>

          <div class="bg-gray-800/50 rounded-xl p-4 border border-gray-700">
            <div class="text-3xl font-bold text-purple-400">{{ result.crossFileIssues.length }}</div>
            <div class="text-sm text-gray-400">Cross-File Issues</div>
          </div>
        </div>

        <!-- Summary -->
        <div class="bg-gradient-to-r from-gray-800/50 to-gray-900/50 rounded-xl p-6 border border-gray-700">
          <h3 class="text-lg font-semibold mb-3 flex items-center gap-2">
            <span>📊</span> Executive Summary
          </h3>
          <p class="text-gray-300 leading-relaxed">{{ result.summary }}</p>
        </div>

        <!-- Architecture Review -->
        @if (result.architectureReview) {
          <div class="bg-gray-800/30 rounded-xl border border-gray-700 overflow-hidden">
            <div class="px-4 py-3 bg-purple-500/10 border-b border-gray-700">
              <h3 class="font-semibold flex items-center gap-2">
                <span>🏗️</span> Architecture Review
              </h3>
            </div>
            <div class="p-4 space-y-4">
              <p class="text-gray-300">{{ result.architectureReview.overview }}</p>

              <div class="grid md:grid-cols-3 gap-4">
                <!-- Strengths -->
                <div class="bg-emerald-500/10 rounded-lg p-4 border border-emerald-500/20">
                  <h4 class="font-medium text-emerald-400 mb-2 flex items-center gap-2">
                    <span>💪</span> Strengths
                  </h4>
                  <ul class="space-y-1">
                    @for (s of result.architectureReview.strengths; track s) {
                      <li class="text-sm text-gray-300 flex items-start gap-2">
                        <span class="text-emerald-500">✓</span> {{ s }}
                      </li>
                    }
                  </ul>
                </div>

                <!-- Concerns -->
                <div class="bg-red-500/10 rounded-lg p-4 border border-red-500/20">
                  <h4 class="font-medium text-red-400 mb-2 flex items-center gap-2">
                    <span>⚠️</span> Concerns
                  </h4>
                  <ul class="space-y-1">
                    @for (c of result.architectureReview.concerns; track c) {
                      <li class="text-sm text-gray-300 flex items-start gap-2">
                        <span class="text-red-500">!</span> {{ c }}
                      </li>
                    }
                  </ul>
                </div>

                <!-- Recommendations -->
                <div class="bg-blue-500/10 rounded-lg p-4 border border-blue-500/20">
                  <h4 class="font-medium text-blue-400 mb-2 flex items-center gap-2">
                    <span>💡</span> Recommendations
                  </h4>
                  <ul class="space-y-1">
                    @for (r of result.architectureReview.recommendations; track r) {
                      <li class="text-sm text-gray-300 flex items-start gap-2">
                        <span class="text-blue-500">→</span> {{ r }}
                      </li>
                    }
                  </ul>
                </div>
              </div>
            </div>
          </div>
        }

        <!-- Cross-File Issues -->
        @if (result.crossFileIssues.length > 0) {
          <div class="bg-gray-800/30 rounded-xl border border-amber-500/30 overflow-hidden">
            <div class="px-4 py-3 bg-amber-500/10 border-b border-amber-500/30">
              <h3 class="font-semibold flex items-center gap-2">
                <span>🔗</span> Cross-File Issues
                <span class="text-xs bg-amber-500/20 px-2 py-0.5 rounded">{{ result.crossFileIssues.length }}</span>
              </h3>
            </div>
            <div class="divide-y divide-gray-700/50">
              @for (issue of result.crossFileIssues; track issue.issue) {
                <div class="p-4">
                  <div class="flex items-start gap-3">
                    <span class="text-xl">🔗</span>
                    <div class="flex-1">
                      <h4 class="font-medium text-amber-400">{{ issue.issue }}</h4>
                      <p class="text-sm text-gray-400 mt-1">{{ issue.explanation }}</p>
                      <div class="flex flex-wrap gap-2 mt-2">
                        @for (file of issue.affectedFiles; track file) {
                          <span class="text-xs bg-gray-700 px-2 py-1 rounded">{{ file }}</span>
                        }
                      </div>
                      @if (issue.suggestion) {
                        <div class="mt-3 bg-gray-900/50 rounded p-3">
                          <span class="text-xs text-gray-500 uppercase">Suggestion:</span>
                          <p class="text-sm text-gray-300 mt-1">{{ issue.suggestion }}</p>
                        </div>
                      }
                    </div>
                  </div>
                </div>
              }
            </div>
          </div>
        }

        <!-- Per-File Findings -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold flex items-center gap-2">
            <span>📁</span> File-by-File Analysis
          </h3>

          @for (fileFinding of result.fileFindings; track fileFinding.filename) {
            <div class="bg-gray-800/30 rounded-xl border border-gray-700 overflow-hidden">
              <div
                (click)="toggleFile(fileFinding.filename)"
                class="px-4 py-3 bg-gray-700/30 flex items-center justify-between cursor-pointer hover:bg-gray-700/50 transition-colors"
              >
                <div class="flex items-center gap-3">
                  <span class="text-lg">{{ getFileIcon(fileFinding.filename) }}</span>
                  <span class="font-medium">{{ fileFinding.filename }}</span>
                  <span class="text-xs bg-gray-600 px-2 py-0.5 rounded">
                    {{ fileFinding.findings.length }} issues
                  </span>
                </div>
                <div class="flex items-center gap-3">
                  <div class="flex gap-1">
                    @for (sev of ['critical', 'warning', 'suggestion']; track sev) {
                      @if (getCountBySeverity(fileFinding, sev) > 0) {
                        <span [class]="getSeverityBadgeClass(sev)" class="text-xs px-2 py-0.5 rounded">
                          {{ getCountBySeverity(fileFinding, sev) }}
                        </span>
                      }
                    }
                  </div>
                  <span class="text-sm" [class]="getScoreTextClass(fileFinding.fileScore)">
                    Score: {{ fileFinding.fileScore }}
                  </span>
                  <span class="transition-transform" [class.rotate-180]="expandedFiles.has(fileFinding.filename)">
                    ▼
                  </span>
                </div>
              </div>

              @if (expandedFiles.has(fileFinding.filename)) {
                <div class="divide-y divide-gray-700/50">
                  @for (finding of fileFinding.findings; track $index) {
                    <div class="p-4">
                      <div class="flex items-start gap-3">
                        <span class="text-lg">{{ getSeverityIcon(finding.severity) }}</span>
                        <div class="flex-1">
                          <div class="flex items-center gap-2">
                            <span class="font-medium">{{ finding.issue }}</span>
                            @if (finding.line) {
                              <span class="text-xs bg-gray-700 px-2 py-0.5 rounded">
                                Line {{ finding.line }}
                              </span>
                            }
                            <span [class]="getSeverityBadgeClass(finding.severity)" class="text-xs px-2 py-0.5 rounded uppercase">
                              {{ finding.severity }}
                            </span>
                          </div>
                          <p class="text-sm text-gray-400 mt-1">{{ finding.explanation }}</p>
                          @if (finding.suggestion) {
                            <div class="mt-2 bg-gray-900/50 rounded p-3 font-mono text-sm">
                              {{ finding.suggestion }}
                            </div>
                          }
                        </div>
                      </div>
                    </div>
                  }
                  @if (fileFinding.findings.length === 0) {
                    <div class="p-4 text-center text-gray-500">
                      <span class="text-2xl">✨</span>
                      <p class="mt-2">No issues found in this file!</p>
                    </div>
                  }
                </div>
              }
            </div>
          }
        </div>

        <!-- Actions -->
        <div class="flex items-center justify-between pt-4 border-t border-gray-700">
          <button
            (click)="onNewAnalysis.emit()"
            class="text-sm text-gray-400 hover:text-white transition-colors flex items-center gap-2"
          >
            <span>←</span> Analyze More Files
          </button>
          <div class="flex gap-3">
            <button
              (click)="exportResults()"
              class="text-sm bg-gray-700 hover:bg-gray-600 px-4 py-2 rounded-lg transition-colors"
            >
              📥 Export JSON
            </button>
            <button
              (click)="shareResults()"
              class="text-sm bg-blue-600 hover:bg-blue-500 px-4 py-2 rounded-lg transition-colors"
            >
              🔗 Share
            </button>
          </div>
        </div>
      </div>
    }
  `
})
export class MultiFileResultsComponent {
  @Input() result: MultiFileAnalysisResponse | null = null;
  @Output() onNewAnalysis = new EventEmitter<void>();

  expandedFiles = new Set<string>();

  toggleFile(filename: string): void {
    if (this.expandedFiles.has(filename)) {
      this.expandedFiles.delete(filename);
    } else {
      this.expandedFiles.add(filename);
    }
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

  getFileIcon(filename: string): string {
    const ext = filename.split('.').pop()?.toLowerCase();
    const icons: Record<string, string> = {
      java: '☕',
      js: '🟨',
      jsx: '🟨',
      ts: '🔷',
      tsx: '🔷',
      py: '🐍'
    };
    return icons[ext || ''] || '📄';
  }

  getSeverityIcon(severity: string): string {
    const icons: Record<string, string> = {
      critical: '🚨',
      warning: '⚠️',
      suggestion: '💡'
    };
    return icons[severity] || '📌';
  }

  getSeverityBadgeClass(severity: string): string {
    const classes: Record<string, string> = {
      critical: 'bg-red-500/20 text-red-400',
      warning: 'bg-amber-500/20 text-amber-400',
      suggestion: 'bg-blue-500/20 text-blue-400'
    };
    return classes[severity] || 'bg-gray-500/20 text-gray-400';
  }

  getCountBySeverity(fileFinding: FileFinding, severity: string): number {
    return fileFinding.findings.filter(f => f.severity === severity).length;
  }

  exportResults(): void {
    if (!this.result) return;
    const blob = new Blob([JSON.stringify(this.result, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `analysis-${this.result.id}.json`;
    a.click();
    URL.revokeObjectURL(url);
  }

  shareResults(): void {
    if (this.result) {
      const url = `${window.location.origin}?multi=${this.result.id}`;
      navigator.clipboard.writeText(url);
      alert('Share URL copied to clipboard!');
    }
  }
}
