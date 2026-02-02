import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Language } from '../../models/analysis.model';

@Component({
  selector: 'app-code-input',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <label class="text-sm font-medium text-gray-300">Your Code</label>
        <div class="flex items-center gap-2">
          <label class="text-xs text-gray-400">Language:</label>
          <select
            [(ngModel)]="selectedLanguage"
            (ngModelChange)="onLanguageChange($event)"
            class="bg-gray-800 border border-gray-700 rounded px-3 py-1 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            @for (lang of languages; track lang.id) {
              <option [value]="lang.id">{{ lang.name }}</option>
            }
          </select>
        </div>
      </div>

      <div class="relative">
        <textarea
          [(ngModel)]="code"
          (ngModelChange)="onCodeChange($event)"
          [placeholder]="placeholder"
          class="code-textarea h-80 font-mono text-sm"
          spellcheck="false"
        ></textarea>
        <div class="absolute bottom-3 right-3 text-xs text-gray-500">
          {{ code.length }} characters
        </div>
      </div>

      <div>
        <label class="text-sm font-medium text-gray-300 block mb-2">
          Context (optional)
        </label>
        <input
          type="text"
          [(ngModel)]="context"
          (ngModelChange)="onContextChange($event)"
          placeholder="What should this code do? Any specific concerns?"
          class="w-full bg-gray-800 border border-gray-700 rounded-lg px-4 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />
      </div>
    </div>
  `
})
export class CodeInputComponent {
  @Input() languages: Language[] = [];
  @Input() code = '';
  @Input() selectedLanguage = 'javascript';
  @Input() context = '';

  @Output() codeChange = new EventEmitter<string>();
  @Output() languageChange = new EventEmitter<string>();
  @Output() contextChange = new EventEmitter<string>();

  placeholder = `// Paste your code here...
function example() {
  // The AI will analyze this code for bugs,
  // edge cases, and potential issues
}`;

  onCodeChange(value: string): void {
    this.codeChange.emit(value);
    this.detectLanguage(value);
  }

  onLanguageChange(value: string): void {
    this.languageChange.emit(value);
  }

  onContextChange(value: string): void {
    this.contextChange.emit(value);
  }

  private detectLanguage(code: string): void {
    if (!code.trim()) return;

    // Simple language detection based on syntax patterns
    const patterns: Record<string, RegExp[]> = {
      java: [/public\s+class\s+/, /public\s+static\s+void\s+main/, /System\.out\.print/],
      typescript: [/interface\s+\w+/, /:\s*(string|number|boolean|any)\b/, /import\s+.*\s+from\s+['"]/, /<\w+>/],
      python: [/def\s+\w+\s*\(/, /import\s+\w+/, /from\s+\w+\s+import/, /:\s*$/m, /print\s*\(/],
      javascript: [/function\s+\w+/, /const\s+\w+\s*=/, /let\s+\w+\s*=/, /=>\s*{/, /console\.log/]
    };

    for (const [lang, regexes] of Object.entries(patterns)) {
      const matches = regexes.filter(r => r.test(code)).length;
      if (matches >= 2) {
        if (this.selectedLanguage !== lang) {
          this.selectedLanguage = lang;
          this.languageChange.emit(lang);
        }
        return;
      }
    }
  }
}
