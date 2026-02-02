import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FileContent } from '../../models/analysis.model';
import { AnalysisService } from '../../services/analysis.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-4">
      <!-- Drop Zone -->
      <div
        (dragover)="onDragOver($event)"
        (dragleave)="onDragLeave($event)"
        (drop)="onDrop($event)"
        [class]="dropZoneClass"
        class="relative border-2 border-dashed rounded-xl p-8 text-center transition-all duration-300"
      >
        <input
          type="file"
          #fileInput
          (change)="onFileSelect($event)"
          multiple
          accept=".java,.js,.jsx,.ts,.tsx,.py,.zip"
          class="hidden"
        />

        <div class="space-y-4">
          <div class="text-5xl">
            {{ isDragging ? '📂' : '🗂️' }}
          </div>
          <div>
            <p class="text-lg font-medium text-gray-200">
              {{ isDragging ? 'Drop files here!' : 'Drag & drop files or folders' }}
            </p>
            <p class="text-sm text-gray-400 mt-1">
              Supports .java, .js, .ts, .py, .zip (max 20 files)
            </p>
          </div>
          <button
            (click)="fileInput.click()"
            class="px-6 py-2 bg-blue-600 hover:bg-blue-500 rounded-lg font-medium transition-colors"
          >
            Browse Files
          </button>
        </div>

        <!-- Animated background on drag -->
        @if (isDragging) {
          <div class="absolute inset-0 bg-blue-500/10 rounded-xl pointer-events-none animate-pulse"></div>
        }
      </div>

      <!-- Project Name -->
      @if (files.length > 0) {
        <div>
          <label class="text-sm font-medium text-gray-300 block mb-2">Project Name (optional)</label>
          <input
            type="text"
            [(ngModel)]="projectName"
            placeholder="My Awesome Project"
            class="w-full bg-gray-800 border border-gray-700 rounded-lg px-4 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
      }

      <!-- File List -->
      @if (files.length > 0) {
        <div class="bg-gray-800/50 rounded-xl border border-gray-700 overflow-hidden">
          <div class="px-4 py-3 bg-gray-700/30 flex items-center justify-between">
            <span class="font-medium text-sm">{{ files.length }} files selected</span>
            <div class="flex gap-2">
              <span class="text-xs text-gray-400">{{ getTotalSize() }}</span>
              <button
                (click)="clearFiles()"
                class="text-xs text-red-400 hover:text-red-300 transition-colors"
              >
                Clear All
              </button>
            </div>
          </div>

          <div class="max-h-64 overflow-y-auto divide-y divide-gray-700/50">
            @for (file of files; track file.filename; let i = $index) {
              <div class="px-4 py-2 flex items-center gap-3 hover:bg-gray-700/30 group">
                <span class="text-lg">{{ getFileIcon(file.language) }}</span>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium truncate">{{ file.filename }}</p>
                  <p class="text-xs text-gray-500">{{ file.language }} • {{ getFileSize(file.content) }}</p>
                </div>
                <button
                  (click)="removeFile(i)"
                  class="opacity-0 group-hover:opacity-100 text-gray-500 hover:text-red-400 transition-all"
                >
                  ✕
                </button>
              </div>
            }
          </div>
        </div>
      }

      <!-- Stats Preview -->
      @if (files.length > 0) {
        <div class="grid grid-cols-4 gap-3">
          <div class="bg-gray-800/50 rounded-lg p-3 text-center border border-gray-700">
            <div class="text-2xl font-bold text-blue-400">{{ files.length }}</div>
            <div class="text-xs text-gray-400">Files</div>
          </div>
          <div class="bg-gray-800/50 rounded-lg p-3 text-center border border-gray-700">
            <div class="text-2xl font-bold text-emerald-400">{{ getTotalLines() }}</div>
            <div class="text-xs text-gray-400">Lines</div>
          </div>
          <div class="bg-gray-800/50 rounded-lg p-3 text-center border border-gray-700">
            <div class="text-2xl font-bold text-amber-400">{{ getLanguageCount() }}</div>
            <div class="text-xs text-gray-400">Languages</div>
          </div>
          <div class="bg-gray-800/50 rounded-lg p-3 text-center border border-gray-700">
            <div class="text-2xl font-bold text-purple-400">{{ getTotalSize() }}</div>
            <div class="text-xs text-gray-400">Size</div>
          </div>
        </div>
      }
    </div>
  `
})
export class FileUploadComponent {
  private analysisService = inject(AnalysisService);

  @Output() filesChange = new EventEmitter<FileContent[]>();
  @Output() projectNameChange = new EventEmitter<string>();

  files: FileContent[] = [];
  projectName = '';
  isDragging = false;

  get dropZoneClass(): string {
    if (this.isDragging) {
      return 'border-blue-500 bg-blue-500/5';
    }
    return 'border-gray-700 hover:border-gray-600 bg-gray-800/30';
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  async onDrop(event: DragEvent): Promise<void> {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    const items = event.dataTransfer?.items;
    if (items) {
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.kind === 'file') {
          const entry = item.webkitGetAsEntry?.();
          if (entry) {
            await this.processEntry(entry);
          } else {
            const file = item.getAsFile();
            if (file) await this.processFile(file);
          }
        }
      }
    }
    this.emitChanges();
  }

  async processEntry(entry: FileSystemEntry, path = ''): Promise<void> {
    if (entry.isFile) {
      const fileEntry = entry as FileSystemFileEntry;
      const file = await new Promise<File>((resolve) => {
        fileEntry.file(resolve);
      });
      await this.processFile(file, path);
    } else if (entry.isDirectory) {
      const dirEntry = entry as FileSystemDirectoryEntry;
      const reader = dirEntry.createReader();
      const entries = await new Promise<FileSystemEntry[]>((resolve) => {
        reader.readEntries(resolve);
      });
      for (const childEntry of entries) {
        await this.processEntry(childEntry, `${path}${entry.name}/`);
      }
    }
  }

  async onFileSelect(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      for (const file of Array.from(input.files)) {
        await this.processFile(file);
      }
      this.emitChanges();
    }
    input.value = '';
  }

  async processFile(file: File, path = ''): Promise<void> {
    if (this.files.length >= 20) return;

    const ext = file.name.split('.').pop()?.toLowerCase();

    // Handle ZIP files
    if (ext === 'zip') {
      await this.processZipFile(file);
      return;
    }

    const validExtensions = ['java', 'js', 'jsx', 'ts', 'tsx', 'py', 'pyw'];
    if (!validExtensions.includes(ext || '')) return;

    const content = await file.text();
    const language = this.analysisService.detectLanguage(file.name);

    this.files.push({
      filename: file.name,
      path: path + file.name,
      content,
      language
    });
  }

  async processZipFile(file: File): Promise<void> {
    // Use JSZip-like approach with native APIs where possible
    // For now, we'll use a simple approach - in production you'd use JSZip
    try {
      const JSZip = (window as any).JSZip;
      if (!JSZip) {
        console.warn('JSZip not loaded, ZIP support limited');
        return;
      }
      const zip = await JSZip.loadAsync(file);
      for (const [filename, zipEntry] of Object.entries(zip.files)) {
        if ((zipEntry as any).dir) continue;
        const ext = filename.split('.').pop()?.toLowerCase();
        const validExtensions = ['java', 'js', 'jsx', 'ts', 'tsx', 'py'];
        if (!validExtensions.includes(ext || '')) continue;

        const content = await (zipEntry as any).async('string');
        this.files.push({
          filename: filename.split('/').pop() || filename,
          path: filename,
          content,
          language: this.analysisService.detectLanguage(filename)
        });

        if (this.files.length >= 20) break;
      }
    } catch (e) {
      console.error('Error processing ZIP:', e);
    }
  }

  removeFile(index: number): void {
    this.files.splice(index, 1);
    this.emitChanges();
  }

  clearFiles(): void {
    this.files = [];
    this.projectName = '';
    this.emitChanges();
  }

  emitChanges(): void {
    this.filesChange.emit([...this.files]);
    this.projectNameChange.emit(this.projectName);
  }

  getFileIcon(language: string): string {
    const icons: Record<string, string> = {
      java: '☕',
      javascript: '🟨',
      typescript: '🔷',
      python: '🐍'
    };
    return icons[language] || '📄';
  }

  getFileSize(content: string): string {
    const bytes = new Blob([content]).size;
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  getTotalSize(): string {
    const bytes = this.files.reduce((sum, f) => sum + new Blob([f.content]).size, 0);
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  getTotalLines(): number {
    return this.files.reduce((sum, f) => sum + f.content.split('\n').length, 0);
  }

  getLanguageCount(): number {
    return new Set(this.files.map(f => f.language)).size;
  }
}
