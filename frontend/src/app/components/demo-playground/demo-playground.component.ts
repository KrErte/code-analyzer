import { Component, OnInit, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalysisService } from '../../services/analysis.service';
import { MockScenario, FamousBug } from '../../models/enhanced.model';

@Component({
  selector: 'app-demo-playground',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="bg-gradient-to-r from-purple-900/40 to-pink-900/40 rounded-xl p-6 border border-purple-500/30">
        <div class="flex items-center gap-4">
          <div class="text-4xl">🎮</div>
          <div>
            <h2 class="text-2xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-400">
              Bug Hunting Playground
            </h2>
            <p class="text-gray-400">Learn from history's most expensive bugs. Try mock scenarios. Master the art of bug detection.</p>
          </div>
        </div>
      </div>

      <!-- Tab Switcher -->
      <div class="flex gap-2">
        <button (click)="activeTab = 'scenarios'"
                [class]="activeTab === 'scenarios' ? 'bg-purple-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'"
                class="px-4 py-2 rounded-lg font-medium transition-all">
          🎯 Mock Scenarios
        </button>
        <button (click)="activeTab = 'famous'"
                [class]="activeTab === 'famous' ? 'bg-pink-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'"
                class="px-4 py-2 rounded-lg font-medium transition-all">
          💀 Famous Bugs Hall of Fame
        </button>
        <button (click)="activeTab = 'random'"
                [class]="activeTab === 'random' ? 'bg-orange-600 text-white' : 'bg-gray-800 text-gray-400 hover:text-white'"
                class="px-4 py-2 rounded-lg font-medium transition-all">
          🎲 Random Challenge
        </button>
      </div>

      <!-- Mock Scenarios Tab -->
      @if (activeTab === 'scenarios') {
        <div class="space-y-4">
          <!-- Category Filter -->
          <div class="flex flex-wrap gap-2">
            <button (click)="categoryFilter = ''"
                    [class]="categoryFilter === '' ? 'bg-gray-600' : 'bg-gray-800'"
                    class="px-3 py-1 rounded-full text-sm text-gray-300 hover:bg-gray-700">
              All
            </button>
            @for (cat of categories; track cat) {
              <button (click)="categoryFilter = cat.id"
                      [class]="categoryFilter === cat.id ? cat.color : 'bg-gray-800'"
                      class="px-3 py-1 rounded-full text-sm text-gray-300 hover:bg-gray-700">
                {{ cat.icon }} {{ cat.name }}
              </button>
            }
          </div>

          <!-- Scenarios Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            @for (scenario of filteredScenarios; track scenario.id) {
              <div class="bg-gray-800/50 rounded-xl border border-gray-700 p-4 hover:border-purple-500/50 transition-all cursor-pointer group"
                   (click)="selectScenario(scenario)">
                <div class="flex items-start justify-between">
                  <div class="flex items-center gap-3">
                    <span class="text-2xl">{{ scenario.icon }}</span>
                    <div>
                      <h3 class="font-bold text-white group-hover:text-purple-300 transition-colors">{{ scenario.name }}</h3>
                      <p class="text-xs text-gray-500">{{ scenario.category }} • {{ scenario.difficulty }}</p>
                    </div>
                  </div>
                  <span class="text-xs px-2 py-1 rounded-full"
                        [class]="getDifficultyClass(scenario.difficulty)">
                    {{ scenario.difficulty }}
                  </span>
                </div>
                <p class="text-sm text-gray-400 mt-2">{{ scenario.description }}</p>
                <div class="flex items-center justify-between mt-3">
                  <span class="text-xs text-gray-600">{{ scenario.language }}</span>
                  <button class="text-xs text-purple-400 hover:text-purple-300 flex items-center gap-1">
                    Try Challenge <span>→</span>
                  </button>
                </div>
              </div>
            }
          </div>
        </div>
      }

      <!-- Famous Bugs Tab -->
      @if (activeTab === 'famous') {
        <div class="space-y-4">
          @for (bug of famousBugs; track bug.id) {
            <div class="bg-gray-800/50 rounded-xl border border-gray-700 overflow-hidden hover:border-pink-500/50 transition-all"
                 [class.border-pink-500/30]="expandedBug === bug.id">
              <!-- Header -->
              <div class="p-4 cursor-pointer" (click)="toggleBug(bug.id)">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-4">
                    <span class="text-3xl">{{ bug.icon }}</span>
                    <div>
                      <h3 class="font-bold text-white text-lg">{{ bug.name }}</h3>
                      <p class="text-sm text-gray-400">{{ bug.company }} • {{ bug.year }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="text-red-400 font-bold">{{ bug.financialImpact }}</p>
                    <p class="text-xs text-gray-500">Financial Impact</p>
                  </div>
                </div>
                <p class="text-gray-400 mt-2">{{ bug.description }}</p>
              </div>

              <!-- Expanded Content -->
              @if (expandedBug === bug.id) {
                <div class="border-t border-gray-700 p-4 space-y-4 bg-gray-900/50">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <h4 class="text-sm font-medium text-pink-400 mb-2">What Happened</h4>
                      <p class="text-sm text-gray-300">{{ bug.whatHappened }}</p>
                    </div>
                    <div>
                      <h4 class="text-sm font-medium text-pink-400 mb-2">Root Cause</h4>
                      <p class="text-sm text-gray-300">{{ bug.rootCause }}</p>
                    </div>
                  </div>

                  <div class="bg-gray-800 rounded-lg p-4">
                    <h4 class="text-sm font-medium text-yellow-400 mb-2">The Code Pattern That Caused It</h4>
                    <pre class="text-xs text-gray-300 overflow-x-auto whitespace-pre-wrap">{{ bug.sampleCode }}</pre>
                  </div>

                  <div class="flex items-center justify-between">
                    <div class="flex flex-wrap gap-2">
                      @for (tag of bug.tags; track tag) {
                        <span class="text-xs px-2 py-1 bg-gray-700 rounded-full text-gray-400">#{{ tag }}</span>
                      }
                    </div>
                    <button (click)="loadFamousBugCode(bug)"
                            class="bg-pink-600 hover:bg-pink-500 px-4 py-2 rounded-lg text-sm font-medium transition-all">
                      🔬 Analyze This Pattern
                    </button>
                  </div>

                  <div class="bg-gradient-to-r from-yellow-900/30 to-orange-900/30 rounded-lg p-4 border border-yellow-500/20">
                    <p class="text-sm text-yellow-300"><strong>Lesson:</strong> {{ bug.lesson }}</p>
                  </div>
                </div>
              }
            </div>
          }
        </div>
      }

      <!-- Random Challenge Tab -->
      @if (activeTab === 'random') {
        <div class="text-center py-12">
          @if (!randomChallenge) {
            <div class="space-y-6">
              <div class="text-6xl animate-bounce">🎲</div>
              <h3 class="text-2xl font-bold text-white">Ready for a Random Challenge?</h3>
              <p class="text-gray-400">Get a random buggy code scenario and test your bug-hunting skills!</p>
              <button (click)="generateRandomChallenge()"
                      class="bg-gradient-to-r from-orange-600 to-red-600 hover:from-orange-500 hover:to-red-500 px-8 py-4 rounded-xl font-bold text-lg transition-all transform hover:scale-105">
                🎰 Generate Random Challenge
              </button>
            </div>
          } @else {
            <div class="text-left space-y-4">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <span class="text-3xl">{{ randomChallenge.icon }}</span>
                  <div>
                    <h3 class="text-xl font-bold text-white">{{ randomChallenge.name }}</h3>
                    <p class="text-sm text-gray-400">{{ randomChallenge.difficulty }} Challenge</p>
                  </div>
                </div>
                <button (click)="randomChallenge = null"
                        class="text-gray-400 hover:text-white">
                  🔄 New Challenge
                </button>
              </div>

              <div class="bg-yellow-900/20 border border-yellow-500/30 rounded-lg p-4">
                <p class="text-yellow-300 font-medium">🎯 Your Challenge:</p>
                <p class="text-gray-300 mt-1">{{ randomChallenge.challenge }}</p>
              </div>

              <div class="bg-gray-800 rounded-lg p-4">
                <pre class="text-sm text-gray-300 overflow-x-auto whitespace-pre-wrap">{{ randomChallenge.code }}</pre>
              </div>

              <div class="flex items-center justify-between">
                <button (click)="showHints = !showHints"
                        class="text-purple-400 hover:text-purple-300 text-sm">
                  {{ showHints ? '🙈 Hide Hints' : '💡 Show Hints' }}
                </button>
                <button (click)="selectScenario(randomChallenge)"
                        class="bg-gradient-to-r from-purple-600 to-pink-600 px-6 py-2 rounded-lg font-medium">
                  🔍 Analyze with AI
                </button>
              </div>

              @if (showHints) {
                <div class="bg-purple-900/20 border border-purple-500/30 rounded-lg p-4">
                  <p class="text-purple-300 font-medium mb-2">💡 Hints:</p>
                  <ul class="list-disc list-inside space-y-1">
                    @for (hint of randomChallenge.hints; track hint) {
                      <li class="text-gray-300 text-sm">{{ hint }}</li>
                    }
                  </ul>
                </div>
              }

              <div class="bg-gray-800/50 rounded-lg p-4">
                <p class="text-sm text-gray-400"><strong>Real World Example:</strong> {{ randomChallenge.realWorldExample }}</p>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `
})
export class DemoPlaygroundComponent implements OnInit {
  private analysisService = inject(AnalysisService);

  @Output() onSelectCode = new EventEmitter<{ code: string; language: string }>();

  mockScenarios: MockScenario[] = [];
  famousBugs: FamousBug[] = [];
  activeTab: 'scenarios' | 'famous' | 'random' = 'scenarios';
  categoryFilter = '';
  expandedBug: string | null = null;
  randomChallenge: MockScenario | null = null;
  showHints = false;

  categories = [
    { id: 'security', name: 'Security', icon: '🔒', color: 'bg-red-600' },
    { id: 'performance', name: 'Performance', icon: '🐌', color: 'bg-yellow-600' },
    { id: 'data-loss', name: 'Data Loss', icon: '💾', color: 'bg-purple-600' },
    { id: 'career-ending', name: 'Career Ending', icon: '💀', color: 'bg-pink-600' }
  ];

  ngOnInit(): void {
    this.loadMockScenarios();
    this.loadFamousBugs();
  }

  private loadMockScenarios(): void {
    this.analysisService.getMockScenarios().subscribe({
      next: (scenarios) => this.mockScenarios = scenarios,
      error: () => console.error('Failed to load mock scenarios')
    });
  }

  private loadFamousBugs(): void {
    this.analysisService.getFamousBugs().subscribe({
      next: (bugs) => this.famousBugs = bugs,
      error: () => console.error('Failed to load famous bugs')
    });
  }

  get filteredScenarios(): MockScenario[] {
    if (!this.categoryFilter) return this.mockScenarios;
    return this.mockScenarios.filter(s => s.category === this.categoryFilter);
  }

  selectScenario(scenario: MockScenario): void {
    this.onSelectCode.emit({ code: scenario.code, language: scenario.language });
  }

  loadFamousBugCode(bug: FamousBug): void {
    this.onSelectCode.emit({ code: bug.sampleCode, language: 'java' });
  }

  toggleBug(id: string): void {
    this.expandedBug = this.expandedBug === id ? null : id;
  }

  generateRandomChallenge(): void {
    if (this.mockScenarios.length > 0) {
      const idx = Math.floor(Math.random() * this.mockScenarios.length);
      this.randomChallenge = this.mockScenarios[idx];
      this.showHints = false;
    }
  }

  getDifficultyClass(difficulty: string): string {
    const classes: Record<string, string> = {
      'Intern': 'bg-green-600/20 text-green-400',
      'Junior': 'bg-blue-600/20 text-blue-400',
      'Senior': 'bg-yellow-600/20 text-yellow-400',
      'Principal': 'bg-orange-600/20 text-orange-400',
      'CTO': 'bg-red-600/20 text-red-400'
    };
    return classes[difficulty] || 'bg-gray-600/20 text-gray-400';
  }
}
