import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Persona } from '../../models/analysis.model';

@Component({
  selector: 'app-persona-selector',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-3">
      <label class="text-sm font-medium text-gray-300 block">Review Style</label>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
        @for (persona of personas; track persona.id) {
          <button
            (click)="selectPersona(persona.id)"
            class="persona-card p-4 rounded-lg border text-left"
            [class]="getPersonaClasses(persona.id)"
          >
            <div class="flex items-center gap-2 mb-2">
              <span class="text-xl">{{ getPersonaIcon(persona.id) }}</span>
              <span class="font-semibold text-sm">{{ persona.name }}</span>
            </div>
            <p class="text-xs text-gray-400 leading-relaxed">
              {{ persona.description }}
            </p>
          </button>
        }
      </div>
    </div>
  `
})
export class PersonaSelectorComponent {
  @Input() personas: Persona[] = [];
  @Input() selectedPersona = 'brutal';
  @Output() personaChange = new EventEmitter<string>();

  selectPersona(id: string): void {
    this.selectedPersona = id;
    this.personaChange.emit(id);
  }

  getPersonaIcon(id: string): string {
    const icons: Record<string, string> = {
      'brutal': '🔥',
      'mentor': '🎓',
      'edge-hunter': '🎯'
    };
    return icons[id] || '👤';
  }

  getPersonaClasses(id: string): string {
    const isSelected = this.selectedPersona === id;
    const baseClasses = 'transition-all duration-200';

    if (isSelected) {
      const selectedClasses: Record<string, string> = {
        'brutal': 'bg-red-500/10 border-red-500 ring-2 ring-red-500/50',
        'mentor': 'bg-emerald-500/10 border-emerald-500 ring-2 ring-emerald-500/50',
        'edge-hunter': 'bg-amber-500/10 border-amber-500 ring-2 ring-amber-500/50'
      };
      return `${baseClasses} ${selectedClasses[id] || 'bg-blue-500/10 border-blue-500'}`;
    }

    return `${baseClasses} bg-gray-800/50 border-gray-700 hover:border-gray-600 hover:bg-gray-800`;
  }
}
