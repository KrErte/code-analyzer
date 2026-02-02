import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalysisHistory } from '../../models/analysis.model';

@Component({
  selector: 'app-history-panel',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-gray-800/30 rounded-xl border border-gray-700 overflow-hidden">
      <div class="px-4 py-3 bg-gray-700/30 flex items-center justify-between">
        <h3 class="font-semibold text-sm">Recent Analyses</h3>
        @if (history.length > 0) {
          <button
            (click)="onClearHistory.emit()"
            class="text-xs text-gray-400 hover:text-red-400 transition-colors"
          >
            Clear All
          </button>
        }
      </div>

      @if (history.length === 0) {
        <div class="p-6 text-center text-gray-500 text-sm">
          No analysis history yet
        </div>
      } @else {
        <div class="divide-y divide-gray-700/50 max-h-80 overflow-y-auto">
          @for (item of history; track item.id) {
            <div
              class="p-3 hover:bg-gray-700/30 cursor-pointer transition-colors group"
              (click)="onSelectHistory.emit(item)"
            >
              <div class="flex items-center justify-between mb-1">
                <span class="text-xs uppercase text-gray-500">{{ item.language }}</span>
                <div class="flex items-center gap-2">
                  <span class="text-xs" [class]="getScoreClass(item.response.score)">
                    Score: {{ item.response.score }}
                  </span>
                  <button
                    (click)="deleteItem($event, item.id)"
                    class="opacity-0 group-hover:opacity-100 text-gray-500 hover:text-red-400 transition-all"
                  >
                    ×
                  </button>
                </div>
              </div>
              <p class="text-sm text-gray-300 truncate font-mono">
                {{ getCodePreview(item.code) }}
              </p>
              <div class="flex items-center gap-2 mt-1">
                <span class="text-xs text-gray-500">{{ getTimeAgo(item.createdAt) }}</span>
                <span class="text-xs text-gray-600">•</span>
                <span class="text-xs text-gray-500">{{ item.response.findings.length }} findings</span>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `
})
export class HistoryPanelComponent {
  @Input() history: AnalysisHistory[] = [];
  @Output() onSelectHistory = new EventEmitter<AnalysisHistory>();
  @Output() onClearHistory = new EventEmitter<void>();
  @Output() onDeleteItem = new EventEmitter<string>();

  getCodePreview(code: string): string {
    const lines = code.split('\n').filter(l => l.trim());
    const firstLine = lines[0] || '';
    return firstLine.length > 60 ? firstLine.substring(0, 60) + '...' : firstLine;
  }

  getTimeAgo(timestamp: number): string {
    const seconds = Math.floor((Date.now() - timestamp) / 1000);

    if (seconds < 60) return 'Just now';
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
    return `${Math.floor(seconds / 86400)}d ago`;
  }

  getScoreClass(score: number): string {
    if (score <= 20) return 'text-emerald-400';
    if (score <= 40) return 'text-blue-400';
    if (score <= 60) return 'text-amber-400';
    if (score <= 80) return 'text-orange-400';
    return 'text-red-400';
  }

  deleteItem(event: Event, id: string): void {
    event.stopPropagation();
    this.onDeleteItem.emit(id);
  }
}
