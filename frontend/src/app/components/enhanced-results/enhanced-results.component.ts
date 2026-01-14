import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnhancedAnalysisResponse, ProductionIncident, Achievement } from '../../models/enhanced.model';

@Component({
  selector: 'app-enhanced-results',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (result) {
      <div class="space-y-6">
        <!-- SHIP IT VERDICT - THE BIG ONE -->
        <div class="relative overflow-hidden rounded-2xl border-2"
             [class]="getVerdictBorderClass()">
          <div class="absolute inset-0 opacity-10" [class]="getVerdictBgClass()"></div>
          <div class="relative p-8 text-center">
            <div class="text-6xl mb-4">{{ getVerdictEmoji() }}</div>
            <h2 class="text-4xl font-black mb-2">{{ result.shipItScore.verdict }}</h2>
            <p class="text-xl text-gray-300 mb-4">{{ result.shipItScore.tldr }}</p>
            <div class="inline-flex items-center gap-2 bg-black/30 rounded-full px-4 py-2">
              <span class="text-sm text-gray-400">Confidence:</span>
              <span class="text-lg font-bold">{{ result.shipItScore.confidence }}%</span>
            </div>
          </div>
        </div>

        <!-- ROAST MODE (if active) -->
        @if (result.roast) {
          <div class="bg-gradient-to-r from-orange-900/30 to-red-900/30 rounded-xl border border-orange-500/30 overflow-hidden">
            <div class="px-6 py-4 bg-gradient-to-r from-orange-600/20 to-red-600/20 border-b border-orange-500/30">
              <h3 class="text-2xl font-bold flex items-center gap-3">
                <span>🔥</span>
                <span class="bg-gradient-to-r from-orange-400 to-red-400 bg-clip-text text-transparent">
                  CODE ROAST
                </span>
                <span class="text-sm bg-red-500 px-2 py-1 rounded">
                  Savagery: {{ result.roast.savageryLevel }}/10
                </span>
              </h3>
            </div>
            <div class="p-6 space-y-4">
              <p class="text-2xl font-bold text-orange-300 italic">
                "{{ result.roast.headline }}"
              </p>
              <div class="space-y-3">
                @for (roast of result.roast.roasts; track roast) {
                  <div class="flex items-start gap-3 bg-black/20 rounded-lg p-3">
                    <span class="text-xl">💀</span>
                    <p class="text-gray-300">{{ roast }}</p>
                  </div>
                }
              </div>
              <div class="mt-4 pt-4 border-t border-orange-500/30">
                <p class="text-sm text-gray-400">📸 Meme energy: {{ result.roast.memeUrl }}</p>
              </div>
              <div class="bg-emerald-500/10 border border-emerald-500/30 rounded-lg p-4">
                <p class="text-sm text-emerald-300">
                  <span class="font-bold">But seriously:</span> {{ result.roast.constructiveTakeaway }}
                </p>
              </div>
            </div>
          </div>
        }

        <!-- Stats Row -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="bg-gray-800/50 rounded-xl p-4 border border-gray-700 text-center">
            <div class="text-3xl font-bold" [class]="getScoreColor(result.score)">{{ result.score }}</div>
            <div class="text-sm text-gray-400">Bug Score</div>
          </div>
          <div class="bg-gray-800/50 rounded-xl p-4 border border-gray-700 text-center">
            <div class="text-3xl font-bold text-red-400">{{ result.predictedIncidents.length }}</div>
            <div class="text-sm text-gray-400">Predicted Incidents</div>
          </div>
          <div class="bg-gray-800/50 rounded-xl p-4 border border-gray-700 text-center">
            <div class="text-3xl font-bold text-amber-400">\${{ formatCost(result.costAnalysis?.totalEstimatedCost) }}</div>
            <div class="text-sm text-gray-400">Risk Cost</div>
          </div>
          <div class="bg-gray-800/50 rounded-xl p-4 border border-gray-700 text-center">
            <div class="text-3xl font-bold text-purple-400">{{ getUnlockedAchievements().length }}</div>
            <div class="text-sm text-gray-400">Achievements</div>
          </div>
        </div>

        <!-- PRODUCTION INCIDENTS -->
        @if (result.predictedIncidents.length > 0) {
          <div class="bg-red-900/10 rounded-xl border border-red-500/30 overflow-hidden">
            <div class="px-4 py-3 bg-red-500/10 border-b border-red-500/30">
              <h3 class="text-lg font-bold flex items-center gap-2">
                <span>🚨</span> Production Incident Predictions
                <span class="text-xs bg-red-500/30 px-2 py-1 rounded">SIMULATION</span>
              </h3>
            </div>

            <!-- Timeline -->
            <div class="p-4 bg-black/20 border-b border-red-500/20">
              <pre class="text-xs font-mono text-gray-300 whitespace-pre-wrap">{{ result.incidentTimeline }}</pre>
            </div>

            <div class="divide-y divide-red-500/20">
              @for (incident of result.predictedIncidents; track incident.id) {
                <div class="p-4">
                  <div class="flex items-start gap-4">
                    <div class="text-3xl">{{ getSeverityEmoji(incident.severity) }}</div>
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-2">
                        <span class="font-bold text-lg">{{ incident.title }}</span>
                        <span [class]="getSeverityClass(incident.severity)" class="text-xs px-2 py-1 rounded font-bold">
                          {{ incident.severity }}
                        </span>
                        <span class="text-xs bg-gray-700 px-2 py-1 rounded">
                          {{ incident.probabilityPercent }}% likely
                        </span>
                      </div>

                      <p class="text-sm text-amber-300 mb-2">
                        <span class="font-medium">Scenario:</span> {{ incident.scenario }}
                      </p>

                      <p class="text-sm text-gray-300 mb-3">{{ incident.whatHappens }}</p>

                      <div class="grid md:grid-cols-2 gap-4">
                        <div class="bg-black/30 rounded-lg p-3">
                          <p class="text-xs text-gray-500 uppercase mb-1">Business Impact</p>
                          <p class="text-sm text-red-300">{{ incident.businessImpact }}</p>
                        </div>
                        @if (incident.costEstimate) {
                          <div class="bg-black/30 rounded-lg p-3">
                            <p class="text-xs text-gray-500 uppercase mb-1">Cost Estimate</p>
                            <p class="text-sm text-amber-300">
                              \${{ formatCost(incident.costEstimate.minDollars) }} - \${{ formatCost(incident.costEstimate.maxDollars) }}
                            </p>
                            <p class="text-xs text-gray-500 mt-1">{{ incident.costEstimate.breakdown }}</p>
                          </div>
                        }
                      </div>

                      @if (incident.preventionCode) {
                        <details class="mt-3">
                          <summary class="cursor-pointer text-sm text-blue-400 hover:text-blue-300">
                            View Prevention Code
                          </summary>
                          <pre class="mt-2 bg-gray-900 rounded p-3 text-xs overflow-x-auto"><code>{{ incident.preventionCode }}</code></pre>
                        </details>
                      }
                    </div>
                  </div>
                </div>
              }
            </div>
          </div>
        }

        <!-- RISK BREAKDOWN -->
        <div class="bg-gray-800/30 rounded-xl border border-gray-700 p-6">
          <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
            <span>📊</span> Risk Breakdown
          </h3>
          <div class="grid grid-cols-5 gap-4">
            @for (risk of getRiskItems(); track risk.name) {
              <div class="text-center">
                <div class="relative w-16 h-16 mx-auto mb-2">
                  <svg class="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="40" stroke="currentColor" stroke-width="8" fill="none" class="text-gray-700"/>
                    <circle cx="50" cy="50" r="40" stroke="currentColor" stroke-width="8" fill="none"
                      [class]="getRiskColor(risk.value)"
                      stroke-linecap="round"
                      [style.stroke-dasharray]="251"
                      [style.stroke-dashoffset]="251 - (251 * risk.value / 100)"/>
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span class="text-sm font-bold">{{ risk.value }}</span>
                  </div>
                </div>
                <p class="text-xs text-gray-400">{{ risk.name }}</p>
              </div>
            }
          </div>
        </div>

        <!-- COST ANALYSIS -->
        @if (result.costAnalysis) {
          <div class="bg-amber-900/10 rounded-xl border border-amber-500/30 p-6">
            <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
              <span>💰</span> Cost Analysis
            </h3>
            <div class="grid md:grid-cols-4 gap-4 mb-4">
              <div class="bg-black/30 rounded-lg p-4 text-center">
                <div class="text-2xl font-bold text-amber-400">\${{ formatCost(result.costAnalysis.totalEstimatedCost) }}</div>
                <div class="text-xs text-gray-400">Total Risk</div>
              </div>
              <div class="bg-black/30 rounded-lg p-4 text-center">
                <div class="text-2xl font-bold text-blue-400">{{ result.costAnalysis.engineeringHoursToFix }}h</div>
                <div class="text-xs text-gray-400">Fix Time</div>
              </div>
              <div class="bg-black/30 rounded-lg p-4 text-center">
                <div class="text-2xl font-bold text-red-400">\${{ formatCost(result.costAnalysis.potentialRevenueLoss) }}</div>
                <div class="text-xs text-gray-400">Revenue Risk</div>
              </div>
              <div class="bg-black/30 rounded-lg p-4 text-center">
                <div class="text-2xl font-bold text-purple-400">\${{ formatCost(result.costAnalysis.technicalDebtCost) }}</div>
                <div class="text-xs text-gray-400">Tech Debt</div>
              </div>
            </div>
            <p class="text-sm text-gray-300 bg-black/30 rounded-lg p-3">
              <span class="font-bold">Recommendation:</span> {{ result.costAnalysis.recommendation }}
            </p>
          </div>
        }

        <!-- ACHIEVEMENTS -->
        @if (result.achievements && result.achievements.length > 0) {
          <div class="bg-purple-900/10 rounded-xl border border-purple-500/30 p-6">
            <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
              <span>🏆</span> Achievements
            </h3>
            <div class="flex flex-wrap gap-3">
              @for (achievement of result.achievements; track achievement.id) {
                <div
                  class="flex items-center gap-2 px-4 py-2 rounded-lg border transition-all"
                  [class]="achievement.unlocked ? 'bg-purple-500/20 border-purple-500/50' : 'bg-gray-800/50 border-gray-700 opacity-50'"
                >
                  <span class="text-2xl" [class.grayscale]="!achievement.unlocked">{{ achievement.icon }}</span>
                  <div>
                    <p class="font-medium text-sm">{{ achievement.name }}</p>
                    <p class="text-xs text-gray-400">{{ achievement.unlocked ? achievement.unlockedReason : achievement.description }}</p>
                  </div>
                  @if (achievement.unlocked) {
                    <span class="text-emerald-400 text-lg">✓</span>
                  }
                </div>
              }
            </div>
          </div>
        }

        <!-- MUST FIX -->
        @if (result.shipItScore.mustFixBefore.length > 0) {
          <div class="bg-red-900/10 rounded-xl border border-red-500/30 p-6">
            <h3 class="text-lg font-bold mb-3 flex items-center gap-2 text-red-400">
              <span>🚫</span> Must Fix Before Deploy
            </h3>
            <ul class="space-y-2">
              @for (item of result.shipItScore.mustFixBefore; track item) {
                <li class="flex items-center gap-2 text-sm">
                  <span class="text-red-500">✗</span>
                  <span>{{ item }}</span>
                </li>
              }
            </ul>
          </div>
        }

        <!-- Findings (Collapsed) -->
        <details class="bg-gray-800/30 rounded-xl border border-gray-700">
          <summary class="px-4 py-3 cursor-pointer font-medium">
            📋 All Findings ({{ result.findings.length }})
          </summary>
          <div class="p-4 space-y-3 border-t border-gray-700">
            @for (finding of result.findings; track $index) {
              <div class="bg-gray-900/50 rounded-lg p-3">
                <div class="flex items-center gap-2 mb-1">
                  <span>{{ finding.severity === 'critical' ? '🚨' : finding.severity === 'warning' ? '⚠️' : '💡' }}</span>
                  <span class="font-medium">{{ finding.issue }}</span>
                  @if (finding.line) {
                    <span class="text-xs bg-gray-700 px-2 py-0.5 rounded">Line {{ finding.line }}</span>
                  }
                </div>
                <p class="text-sm text-gray-400">{{ finding.explanation }}</p>
              </div>
            }
          </div>
        </details>

        <!-- Improved Code -->
        @if (result.improvedCode) {
          <details class="bg-gray-800/30 rounded-xl border border-gray-700">
            <summary class="px-4 py-3 cursor-pointer font-medium">
              ✨ Improved Code
            </summary>
            <div class="p-4 border-t border-gray-700">
              <div class="flex justify-end mb-2">
                <button (click)="copyCode()" class="text-xs bg-blue-600 hover:bg-blue-500 px-3 py-1 rounded">
                  {{ copied ? 'Copied!' : 'Copy' }}
                </button>
              </div>
              <pre class="bg-gray-900 rounded p-4 overflow-x-auto text-sm"><code>{{ result.improvedCode }}</code></pre>
            </div>
          </details>
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
            (click)="shareResults()"
            class="text-sm bg-gradient-to-r from-blue-600 to-purple-600 px-6 py-2 rounded-lg"
          >
            Share Results
          </button>
        </div>
      </div>
    }
  `
})
export class EnhancedResultsComponent {
  @Input() result: EnhancedAnalysisResponse | null = null;
  @Output() onNewAnalysis = new EventEmitter<void>();

  copied = false;

  getVerdictEmoji(): string {
    const verdict = this.result?.shipItScore?.verdict || '';
    if (verdict.includes('SHIP IT')) return '🚀';
    if (verdict.includes('MAYBE')) return '🤔';
    if (verdict.includes('NOPE')) return '🔥';
    return '💀';
  }

  getVerdictBorderClass(): string {
    const verdict = this.result?.shipItScore?.verdict || '';
    if (verdict.includes('SHIP IT')) return 'border-emerald-500 bg-emerald-500/5';
    if (verdict.includes('MAYBE')) return 'border-amber-500 bg-amber-500/5';
    return 'border-red-500 bg-red-500/5';
  }

  getVerdictBgClass(): string {
    const verdict = this.result?.shipItScore?.verdict || '';
    if (verdict.includes('SHIP IT')) return 'bg-emerald-500';
    if (verdict.includes('MAYBE')) return 'bg-amber-500';
    return 'bg-red-500';
  }

  getScoreColor(score: number): string {
    if (score <= 20) return 'text-emerald-400';
    if (score <= 40) return 'text-blue-400';
    if (score <= 60) return 'text-amber-400';
    return 'text-red-400';
  }

  getSeverityEmoji(severity: string): string {
    switch (severity) {
      case 'P0': return '🔴';
      case 'P1': return '🟠';
      case 'P2': return '🟡';
      default: return '🟢';
    }
  }

  getSeverityClass(severity: string): string {
    switch (severity) {
      case 'P0': return 'bg-red-500 text-white';
      case 'P1': return 'bg-orange-500 text-white';
      case 'P2': return 'bg-yellow-500 text-black';
      default: return 'bg-green-500 text-white';
    }
  }

  formatCost(cost: number | undefined): string {
    if (!cost) return '0';
    if (cost >= 1000000) return (cost / 1000000).toFixed(1) + 'M';
    if (cost >= 1000) return (cost / 1000).toFixed(1) + 'K';
    return cost.toString();
  }

  getUnlockedAchievements(): Achievement[] {
    return this.result?.achievements?.filter(a => a.unlocked) || [];
  }

  getRiskItems() {
    const rb = this.result?.shipItScore?.riskBreakdown;
    if (!rb) return [];
    return [
      { name: 'Security', value: rb.securityRisk },
      { name: 'Stability', value: rb.stabilityRisk },
      { name: 'Performance', value: rb.performanceRisk },
      { name: 'Maintain', value: rb.maintainabilityRisk },
      { name: 'Data Loss', value: rb.dataLossRisk }
    ];
  }

  getRiskColor(value: number): string {
    if (value <= 25) return 'text-emerald-500';
    if (value <= 50) return 'text-amber-500';
    if (value <= 75) return 'text-orange-500';
    return 'text-red-500';
  }

  copyCode(): void {
    if (this.result?.improvedCode) {
      navigator.clipboard.writeText(this.result.improvedCode);
      this.copied = true;
      setTimeout(() => this.copied = false, 2000);
    }
  }

  shareResults(): void {
    if (this.result) {
      navigator.clipboard.writeText(window.location.origin + '?enhanced=' + this.result.id);
      alert('Share URL copied!');
    }
  }
}
