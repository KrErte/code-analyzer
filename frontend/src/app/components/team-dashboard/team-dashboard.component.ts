import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-team-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-gray-900 p-8">
      <!-- Header -->
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-3xl font-bold">Team Dashboard</h1>
          <p class="text-gray-400">Engineering Health Overview</p>
        </div>
        <div class="flex items-center gap-4">
          <select class="bg-gray-800 border border-gray-700 rounded-lg px-4 py-2">
            <option>Last 30 days</option>
            <option>Last 7 days</option>
            <option>Last 90 days</option>
          </select>
          <button class="bg-blue-600 hover:bg-blue-500 px-4 py-2 rounded-lg">Export Report</button>
        </div>
      </div>

      <!-- Executive Summary -->
      <div class="bg-gradient-to-r from-red-900/30 to-orange-900/30 border border-red-500/30 rounded-2xl p-6 mb-8">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="text-6xl">{{ healthEmoji }}</div>
            <div>
              <div class="text-sm text-gray-400">Overall Code Health</div>
              <div class="text-3xl font-bold" [class]="getHealthClass()">{{ healthStatus }}</div>
              <div class="text-sm text-gray-400">{{ summary }}</div>
            </div>
          </div>
          <div class="text-right">
            <div class="text-sm text-gray-400">Estimated Risk Exposure</div>
            <div class="text-4xl font-bold text-red-400">\${{ riskExposure | number }}</div>
            <div class="text-sm" [class]="trendClass">{{ trend }}</div>
          </div>
        </div>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6">
          <div class="text-sm text-gray-400 mb-1">Critical Issues</div>
          <div class="text-4xl font-bold text-red-400">{{ criticalIssues }}</div>
          <div class="text-sm text-red-400">+2 from last week</div>
        </div>
        <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6">
          <div class="text-sm text-gray-400 mb-1">Code Reviews Saved</div>
          <div class="text-4xl font-bold text-green-400">{{ reviewsSaved }}h</div>
          <div class="text-sm text-green-400">\${{ reviewsSavedDollars | number }} saved</div>
        </div>
        <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6">
          <div class="text-sm text-gray-400 mb-1">Incidents Prevented</div>
          <div class="text-4xl font-bold text-blue-400">{{ incidentsPrevented }}</div>
          <div class="text-sm text-blue-400">\${{ incidentsPreventedDollars | number }} saved</div>
        </div>
        <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6">
          <div class="text-sm text-gray-400 mb-1">Deploy Success Rate</div>
          <div class="text-4xl font-bold text-purple-400">{{ deploySuccessRate }}%</div>
          <div class="text-sm text-purple-400">{{ blockedDeploys }} blocked</div>
        </div>
      </div>

      <div class="grid md:grid-cols-2 gap-8 mb-8">
        <!-- Risk by Service -->
        <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6">
          <h3 class="text-lg font-bold mb-4">Risk by Service</h3>
          <div class="space-y-4">
            @for (service of riskByService; track service.name) {
              <div class="flex items-center gap-4">
                <div class="w-32 truncate text-gray-300">{{ service.name }}</div>
                <div class="flex-1">
                  <div class="h-4 bg-gray-700 rounded-full overflow-hidden">
                    <div class="h-full rounded-full transition-all"
                         [class]="getRiskBarClass(service.risk)"
                         [style.width.%]="service.risk"></div>
                  </div>
                </div>
                <div class="w-16 text-right">
                  <span [class]="getRiskTextClass(service.risk)">{{ service.risk }}%</span>
                </div>
                <div class="w-24 text-right text-gray-400">\${{ service.cost | number }}</div>
              </div>
            }
          </div>
        </div>

        <!-- Cost Breakdown -->
        <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6">
          <h3 class="text-lg font-bold mb-4">Tech Debt Cost Breakdown</h3>
          <div class="space-y-4">
            @for (item of costBreakdown; track item.category) {
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <span class="text-2xl">{{ item.icon }}</span>
                  <span class="text-gray-300">{{ item.category }}</span>
                </div>
                <div class="text-right">
                  <div class="font-bold">\${{ item.annual | number }}/yr</div>
                  <div class="text-sm text-gray-400">{{ item.percent }}%</div>
                </div>
              </div>
            }
            <div class="border-t border-gray-600 pt-4 flex justify-between items-center">
              <span class="font-bold">Total Tech Debt Cost</span>
              <span class="text-2xl font-bold text-red-400">\${{ totalDebtCost | number }}/yr</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Priority Issues -->
      <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6 mb-8">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-bold">Priority Issues</h3>
          <span class="text-sm text-gray-400">Fix these first for maximum impact</span>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="text-left text-gray-400 text-sm border-b border-gray-700">
                <th class="pb-3 pr-4">#</th>
                <th class="pb-3 pr-4">Issue</th>
                <th class="pb-3 pr-4">Service</th>
                <th class="pb-3 pr-4">Severity</th>
                <th class="pb-3 pr-4">Est. Cost</th>
                <th class="pb-3 pr-4">Fix Time</th>
                <th class="pb-3">Owner</th>
              </tr>
            </thead>
            <tbody>
              @for (issue of priorityIssues; track issue.rank) {
                <tr class="border-b border-gray-700/50 hover:bg-gray-700/20">
                  <td class="py-4 pr-4 text-gray-500">{{ issue.rank }}</td>
                  <td class="py-4 pr-4">
                    <div class="font-medium">{{ issue.title }}</div>
                    <div class="text-sm text-gray-400">{{ issue.file }}</div>
                  </td>
                  <td class="py-4 pr-4 text-gray-300">{{ issue.service }}</td>
                  <td class="py-4 pr-4">
                    <span class="px-2 py-1 rounded text-xs font-bold"
                          [class]="getSeverityClass(issue.severity)">
                      {{ issue.severity }}
                    </span>
                  </td>
                  <td class="py-4 pr-4 text-amber-400 font-medium">\${{ issue.cost | number }}</td>
                  <td class="py-4 pr-4 text-gray-300">{{ issue.fixHours }}h</td>
                  <td class="py-4">
                    <div class="flex items-center gap-2">
                      <div class="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center text-xs font-bold">
                        {{ issue.owner.charAt(0) }}
                      </div>
                      <span class="text-gray-300">{{ issue.owner }}</span>
                    </div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>

      <!-- ROI Summary -->
      <div class="bg-gradient-to-r from-green-900/30 to-emerald-900/30 border border-green-500/30 rounded-2xl p-6">
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-lg font-bold mb-2">CodeKarma ROI This Month</h3>
            <p class="text-gray-400">Based on time saved and incidents prevented</p>
          </div>
          <div class="text-right">
            <div class="text-sm text-gray-400">Total Savings</div>
            <div class="text-4xl font-bold text-green-400">\${{ monthlySavings | number }}</div>
            <div class="text-sm text-green-400">{{ roiMultiple }}x return on investment</div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class TeamDashboardComponent implements OnInit {
  // Executive Summary
  healthEmoji = '⚠️';
  healthStatus = 'At Risk';
  healthScore = 62;
  summary = '3 critical issues need immediate attention';
  riskExposure = 287000;
  trend = '+12% from last month';
  trendClass = 'text-red-400';

  // Stats
  criticalIssues = 3;
  reviewsSaved = 127;
  reviewsSavedDollars = 15240;
  incidentsPrevented = 4;
  incidentsPreventedDollars = 180000;
  deploySuccessRate = 94;
  blockedDeploys = 7;

  // Risk by Service
  riskByService = [
    { name: 'auth-service', risk: 87, cost: 120000 },
    { name: 'payment-api', risk: 72, cost: 85000 },
    { name: 'user-service', risk: 45, cost: 42000 },
    { name: 'notification', risk: 23, cost: 18000 },
    { name: 'analytics', risk: 12, cost: 8000 },
  ];

  // Cost Breakdown
  costBreakdown = [
    { category: 'Security Vulnerabilities', icon: '🔓', annual: 95000, percent: 35 },
    { category: 'Performance Issues', icon: '🐌', annual: 68000, percent: 25 },
    { category: 'Reliability Risks', icon: '💥', annual: 54000, percent: 20 },
    { category: 'Maintainability', icon: '🔧', annual: 41000, percent: 15 },
    { category: 'Other', icon: '📦', annual: 14000, percent: 5 },
  ];

  totalDebtCost = 272000;

  // Priority Issues
  priorityIssues = [
    { rank: 1, title: 'SQL Injection in login endpoint', file: 'auth-service/login.ts:47', service: 'auth-service', severity: 'Critical', cost: 75000, fixHours: 4, owner: 'Alex' },
    { rank: 2, title: 'N+1 query in order history', file: 'payment-api/orders.ts:128', service: 'payment-api', severity: 'Critical', cost: 45000, fixHours: 8, owner: 'Sarah' },
    { rank: 3, title: 'Memory leak in websocket handler', file: 'notification/ws.ts:89', service: 'notification', severity: 'Critical', cost: 32000, fixHours: 6, owner: 'Mike' },
    { rank: 4, title: 'Missing rate limiting', file: 'auth-service/api.ts:12', service: 'auth-service', severity: 'High', cost: 28000, fixHours: 3, owner: 'Alex' },
    { rank: 5, title: 'Hardcoded credentials', file: 'payment-api/config.ts:5', service: 'payment-api', severity: 'High', cost: 22000, fixHours: 1, owner: 'Sarah' },
  ];

  // ROI
  monthlySavings = 195240;
  roiMultiple = 11.2;

  ngOnInit(): void {}

  getHealthClass(): string {
    if (this.healthScore >= 80) return 'text-green-400';
    if (this.healthScore >= 60) return 'text-yellow-400';
    if (this.healthScore >= 40) return 'text-orange-400';
    return 'text-red-400';
  }

  getRiskBarClass(risk: number): string {
    if (risk >= 70) return 'bg-red-500';
    if (risk >= 40) return 'bg-orange-500';
    if (risk >= 20) return 'bg-yellow-500';
    return 'bg-green-500';
  }

  getRiskTextClass(risk: number): string {
    if (risk >= 70) return 'text-red-400 font-bold';
    if (risk >= 40) return 'text-orange-400';
    if (risk >= 20) return 'text-yellow-400';
    return 'text-green-400';
  }

  getSeverityClass(severity: string): string {
    switch (severity) {
      case 'Critical': return 'bg-red-500 text-white';
      case 'High': return 'bg-orange-500 text-white';
      case 'Medium': return 'bg-yellow-500 text-black';
      default: return 'bg-gray-500 text-white';
    }
  }
}
