import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="min-h-screen bg-gradient-to-b from-gray-900 via-gray-800 to-gray-900">
      <!-- Hero Section -->
      <header class="relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-r from-blue-600/20 to-purple-600/20"></div>
        <nav class="relative z-10 flex items-center justify-between px-8 py-6 max-w-7xl mx-auto">
          <div class="flex items-center gap-3">
            <span class="text-3xl">🔮</span>
            <span class="text-2xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
              CodeKarma
            </span>
          </div>
          <div class="flex items-center gap-6">
            <a href="#features" class="text-gray-300 hover:text-white transition">Features</a>
            <a href="#pricing" class="text-gray-300 hover:text-white transition">Pricing</a>
            <a href="#roi" class="text-gray-300 hover:text-white transition">ROI Calculator</a>
            <button class="bg-blue-600 hover:bg-blue-500 px-6 py-2 rounded-lg font-medium transition">
              Start Free Trial
            </button>
          </div>
        </nav>

        <div class="relative z-10 max-w-7xl mx-auto px-8 py-24 text-center">
          <div class="inline-flex items-center gap-2 bg-blue-500/20 border border-blue-500/30 rounded-full px-4 py-2 mb-8">
            <span class="text-blue-400">🚀</span>
            <span class="text-sm text-blue-300">Used by 500+ engineering teams</span>
          </div>

          <h1 class="text-5xl md:text-7xl font-black mb-6 leading-tight">
            <span class="text-white">Stop Shipping</span><br>
            <span class="bg-gradient-to-r from-red-400 via-orange-400 to-yellow-400 bg-clip-text text-transparent">
              $50,000 Bugs
            </span>
          </h1>

          <p class="text-xl text-gray-300 mb-8 max-w-2xl mx-auto">
            AI-powered code analysis that predicts production incidents
            <strong class="text-white">before they happen</strong>.
            Save your team 10+ hours/week on code review.
          </p>

          <div class="flex flex-col sm:flex-row items-center justify-center gap-4 mb-12">
            <button (click)="scrollToDemo()" class="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 px-8 py-4 rounded-xl font-bold text-lg transition transform hover:scale-105">
              Try Free Analysis →
            </button>
            <button class="border border-gray-600 hover:border-gray-500 px-8 py-4 rounded-xl font-medium transition">
              Book Demo
            </button>
          </div>

          <!-- Social Proof -->
          <div class="flex items-center justify-center gap-8 text-gray-400">
            <div class="text-center">
              <div class="text-3xl font-bold text-white">2.3M</div>
              <div class="text-sm">Bugs Caught</div>
            </div>
            <div class="w-px h-12 bg-gray-700"></div>
            <div class="text-center">
              <div class="text-3xl font-bold text-white">$47M</div>
              <div class="text-sm">Incidents Prevented</div>
            </div>
            <div class="w-px h-12 bg-gray-700"></div>
            <div class="text-center">
              <div class="text-3xl font-bold text-white">89%</div>
              <div class="text-sm">Faster Reviews</div>
            </div>
          </div>
        </div>
      </header>

      <!-- Problem Section -->
      <section class="py-24 px-8 max-w-7xl mx-auto">
        <div class="text-center mb-16">
          <h2 class="text-4xl font-bold mb-4">The $5.5M Problem</h2>
          <p class="text-xl text-gray-400">Average cost of a single hour of downtime for enterprises</p>
        </div>

        <div class="grid md:grid-cols-3 gap-8">
          <div class="bg-red-900/20 border border-red-500/30 rounded-2xl p-8">
            <div class="text-4xl mb-4">⏰</div>
            <h3 class="text-xl font-bold mb-2 text-red-400">30% of Senior Dev Time</h3>
            <p class="text-gray-400">Spent on code review instead of building features</p>
          </div>
          <div class="bg-orange-900/20 border border-orange-500/30 rounded-2xl p-8">
            <div class="text-4xl mb-4">🔥</div>
            <h3 class="text-xl font-bold mb-2 text-orange-400">3AM Pages</h3>
            <p class="text-gray-400">Bad code ships → production breaks → engineers quit</p>
          </div>
          <div class="bg-yellow-900/20 border border-yellow-500/30 rounded-2xl p-8">
            <div class="text-4xl mb-4">💸</div>
            <h3 class="text-xl font-bold mb-2 text-yellow-400">$100K+ Per Incident</h3>
            <p class="text-gray-400">Engineering time, lost revenue, customer trust</p>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section id="features" class="py-24 px-8 bg-gray-800/50">
        <div class="max-w-7xl mx-auto">
          <div class="text-center mb-16">
            <h2 class="text-4xl font-bold mb-4">What Makes Us Different</h2>
            <p class="text-xl text-gray-400">Not just another linter. We predict the future.</p>
          </div>

          <div class="grid md:grid-cols-2 gap-8">
            <!-- Feature 1 -->
            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8 hover:border-blue-500/50 transition">
              <div class="flex items-center gap-4 mb-4">
                <div class="w-12 h-12 bg-blue-500/20 rounded-xl flex items-center justify-center text-2xl">🔮</div>
                <h3 class="text-xl font-bold">Incident Prediction</h3>
              </div>
              <p class="text-gray-400 mb-4">
                Not "might fail" - we predict SPECIFIC scenarios: "At 3AM during traffic spike,
                this will cause cascading timeouts costing $50K"
              </p>
              <div class="text-sm text-blue-400">→ Competitors just show rule violations</div>
            </div>

            <!-- Feature 2 -->
            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8 hover:border-green-500/50 transition">
              <div class="flex items-center gap-4 mb-4">
                <div class="w-12 h-12 bg-green-500/20 rounded-xl flex items-center justify-center text-2xl">💰</div>
                <h3 class="text-xl font-bold">Cost Estimation</h3>
              </div>
              <p class="text-gray-400 mb-4">
                Translate tech debt to dollars. "This N+1 query will cost $12,000/month in
                database fees." Justify refactoring to your VP.
              </p>
              <div class="text-sm text-green-400">→ Competitors show abstract "debt scores"</div>
            </div>

            <!-- Feature 3 -->
            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8 hover:border-purple-500/50 transition">
              <div class="flex items-center gap-4 mb-4">
                <div class="w-12 h-12 bg-purple-500/20 rounded-xl flex items-center justify-center text-2xl">🚦</div>
                <h3 class="text-xl font-bold">CI/CD Gate</h3>
              </div>
              <p class="text-gray-400 mb-4">
                Automatically block dangerous PRs. "SHIP IT" or "ARE YOU SERIOUS?" verdict
                with confidence score. One API call in your pipeline.
              </p>
              <div class="text-sm text-purple-400">→ Competitors require complex setup</div>
            </div>

            <!-- Feature 4 -->
            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8 hover:border-orange-500/50 transition">
              <div class="flex items-center gap-4 mb-4">
                <div class="w-12 h-12 bg-orange-500/20 rounded-xl flex items-center justify-center text-2xl">📊</div>
                <h3 class="text-xl font-bold">Team Dashboard</h3>
              </div>
              <p class="text-gray-400 mb-4">
                Show your VP: "Our tech debt costs $230K/year. Top 3 risks: auth-service,
                payment-api. Fix these → save $150K."
              </p>
              <div class="text-sm text-orange-400">→ Competitors show developer metrics only</div>
            </div>
          </div>
        </div>
      </section>

      <!-- ROI Calculator -->
      <section id="roi" class="py-24 px-8 max-w-7xl mx-auto">
        <div class="text-center mb-16">
          <h2 class="text-4xl font-bold mb-4">Calculate Your ROI</h2>
          <p class="text-xl text-gray-400">See exactly how much CodeKarma saves your team</p>
        </div>

        <div class="bg-gradient-to-r from-blue-900/30 to-purple-900/30 border border-blue-500/30 rounded-3xl p-8 md:p-12">
          <div class="grid md:grid-cols-2 gap-12">
            <!-- Inputs -->
            <div class="space-y-6">
              <div>
                <label class="block text-sm text-gray-400 mb-2">Number of Developers</label>
                <input type="range" min="5" max="200" [(ngModel)]="roiDevelopers" class="w-full">
                <div class="text-2xl font-bold mt-2">{{ roiDevelopers }} developers</div>
              </div>
              <div>
                <label class="block text-sm text-gray-400 mb-2">Hours Spent on Code Review (per dev/week)</label>
                <input type="range" min="2" max="20" [(ngModel)]="roiReviewHours" class="w-full">
                <div class="text-2xl font-bold mt-2">{{ roiReviewHours }} hours/week</div>
              </div>
              <div>
                <label class="block text-sm text-gray-400 mb-2">Production Incidents (per month)</label>
                <input type="range" min="0" max="20" [(ngModel)]="roiIncidents" class="w-full">
                <div class="text-2xl font-bold mt-2">{{ roiIncidents }} incidents/month</div>
              </div>
              <div>
                <label class="block text-sm text-gray-400 mb-2">Average Developer Salary</label>
                <input type="range" min="80000" max="250000" step="10000" [(ngModel)]="roiSalary" class="w-full">
                <div class="text-2xl font-bold mt-2">\${{ roiSalary | number }}/year</div>
              </div>
            </div>

            <!-- Results -->
            <div class="bg-gray-900/50 rounded-2xl p-8">
              <h3 class="text-xl font-bold mb-6">Your Annual Savings</h3>

              <div class="space-y-4 mb-8">
                <div class="flex justify-between items-center">
                  <span class="text-gray-400">Code Review Time Saved</span>
                  <span class="text-xl font-bold text-green-400">\${{ calculateReviewSavings() | number }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-400">Incidents Prevented (40%)</span>
                  <span class="text-xl font-bold text-green-400">\${{ calculateIncidentSavings() | number }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-gray-400">Productivity Improvement</span>
                  <span class="text-xl font-bold text-green-400">\${{ calculateProductivitySavings() | number }}</span>
                </div>
                <div class="border-t border-gray-700 pt-4 flex justify-between items-center">
                  <span class="text-lg">Total Annual Savings</span>
                  <span class="text-3xl font-bold text-green-400">\${{ calculateTotalSavings() | number }}</span>
                </div>
              </div>

              <div class="bg-green-500/20 border border-green-500/30 rounded-xl p-4 mb-6">
                <div class="text-sm text-green-300">CodeKarma Pro costs</div>
                <div class="text-xl font-bold text-white">\${{ roiDevelopers * 29 * 12 | number }}/year</div>
              </div>

              <div class="text-center">
                <div class="text-4xl font-black text-green-400">{{ calculateROI() }}x ROI</div>
                <div class="text-gray-400">Return on Investment</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Pricing -->
      <section id="pricing" class="py-24 px-8 bg-gray-800/50">
        <div class="max-w-7xl mx-auto">
          <div class="text-center mb-16">
            <h2 class="text-4xl font-bold mb-4">Simple, Transparent Pricing</h2>
            <p class="text-xl text-gray-400">Start free, scale as you grow</p>
          </div>

          <div class="grid md:grid-cols-3 gap-8 max-w-5xl mx-auto">
            <!-- Free -->
            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8">
              <div class="text-center mb-8">
                <h3 class="text-xl font-bold mb-2">Free</h3>
                <div class="text-4xl font-black">$0</div>
                <div class="text-gray-400">forever</div>
              </div>
              <ul class="space-y-3 mb-8">
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>10 analyses/month</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Basic incident prediction</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Ship-It Score</span>
                </li>
                <li class="flex items-center gap-2 text-gray-500">
                  <span>✗</span>
                  <span>CI/CD integration</span>
                </li>
                <li class="flex items-center gap-2 text-gray-500">
                  <span>✗</span>
                  <span>Team dashboard</span>
                </li>
              </ul>
              <button class="w-full border border-gray-600 hover:border-gray-500 py-3 rounded-xl font-medium transition">
                Get Started
              </button>
            </div>

            <!-- Pro - Highlighted -->
            <div class="bg-gradient-to-b from-blue-900/50 to-purple-900/50 border-2 border-blue-500 rounded-2xl p-8 transform scale-105">
              <div class="text-center mb-8">
                <div class="inline-block bg-blue-500 text-xs font-bold px-3 py-1 rounded-full mb-4">MOST POPULAR</div>
                <h3 class="text-xl font-bold mb-2">Pro</h3>
                <div class="text-4xl font-black">$29</div>
                <div class="text-gray-400">per developer/month</div>
              </div>
              <ul class="space-y-3 mb-8">
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Unlimited analyses</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Full incident prediction</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Cost estimation</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>CI/CD gate integration</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>GitHub/GitLab integration</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Slack notifications</span>
                </li>
              </ul>
              <button class="w-full bg-blue-600 hover:bg-blue-500 py-3 rounded-xl font-bold transition">
                Start Free Trial
              </button>
            </div>

            <!-- Enterprise -->
            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8">
              <div class="text-center mb-8">
                <h3 class="text-xl font-bold mb-2">Enterprise</h3>
                <div class="text-4xl font-black">Custom</div>
                <div class="text-gray-400">let's talk</div>
              </div>
              <ul class="space-y-3 mb-8">
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Everything in Pro</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Team dashboard</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Self-hosted option</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>SSO/SAML</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Audit logs</span>
                </li>
                <li class="flex items-center gap-2">
                  <span class="text-green-400">✓</span>
                  <span>Dedicated support</span>
                </li>
              </ul>
              <button class="w-full border border-gray-600 hover:border-gray-500 py-3 rounded-xl font-medium transition">
                Contact Sales
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Integration Section -->
      <section class="py-24 px-8 max-w-7xl mx-auto">
        <div class="text-center mb-16">
          <h2 class="text-4xl font-bold mb-4">Works With Your Stack</h2>
          <p class="text-xl text-gray-400">2-minute setup. No configuration required.</p>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-8">
          <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6 text-center hover:border-gray-500 transition">
            <div class="text-4xl mb-3">🐙</div>
            <div class="font-medium">GitHub</div>
            <div class="text-sm text-gray-400">Actions & Apps</div>
          </div>
          <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6 text-center hover:border-gray-500 transition">
            <div class="text-4xl mb-3">🦊</div>
            <div class="font-medium">GitLab</div>
            <div class="text-sm text-gray-400">CI/CD</div>
          </div>
          <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6 text-center hover:border-gray-500 transition">
            <div class="text-4xl mb-3">💬</div>
            <div class="font-medium">Slack</div>
            <div class="text-sm text-gray-400">Notifications</div>
          </div>
          <div class="bg-gray-800/50 border border-gray-700 rounded-xl p-6 text-center hover:border-gray-500 transition">
            <div class="text-4xl mb-3">📟</div>
            <div class="font-medium">VS Code</div>
            <div class="text-sm text-gray-400">Extension</div>
          </div>
        </div>

        <!-- GitHub Action Example -->
        <div class="mt-12 bg-gray-900 rounded-2xl border border-gray-700 overflow-hidden">
          <div class="flex items-center gap-2 px-4 py-3 bg-gray-800 border-b border-gray-700">
            <span class="w-3 h-3 bg-red-500 rounded-full"></span>
            <span class="w-3 h-3 bg-yellow-500 rounded-full"></span>
            <span class="w-3 h-3 bg-green-500 rounded-full"></span>
            <span class="ml-4 text-sm text-gray-400">.github/workflows/codekarma.yml</span>
          </div>
          <pre class="p-6 text-sm overflow-x-auto"><code class="text-gray-300">- name: CodeKarma Analysis
  uses: codekarma/analyze@v1
  with:
    api-key: \${{ secrets.CODEKARMA_KEY }}
    fail-on: "ARE YOU SERIOUS?"  # Block dangerous PRs</code></pre>
        </div>
      </section>

      <!-- Testimonials -->
      <section class="py-24 px-8 bg-gray-800/50">
        <div class="max-w-7xl mx-auto">
          <div class="text-center mb-16">
            <h2 class="text-4xl font-bold mb-4">Loved by Engineering Teams</h2>
          </div>

          <div class="grid md:grid-cols-3 gap-8">
            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8">
              <div class="flex items-center gap-1 mb-4">
                <span class="text-yellow-400">★★★★★</span>
              </div>
              <p class="text-gray-300 mb-6">
                "CodeKarma caught a critical N+1 query that would have cost us $30K during Black Friday.
                Paid for itself 100x over."
              </p>
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center font-bold">JK</div>
                <div>
                  <div class="font-medium">Jake Kim</div>
                  <div class="text-sm text-gray-400">VP Engineering, ScaleUp Inc</div>
                </div>
              </div>
            </div>

            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8">
              <div class="flex items-center gap-1 mb-4">
                <span class="text-yellow-400">★★★★★</span>
              </div>
              <p class="text-gray-300 mb-6">
                "Our senior devs were spending 30% of their time on code review.
                Now CodeKarma does the first pass and they focus on architecture."
              </p>
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-purple-500 rounded-full flex items-center justify-center font-bold">SM</div>
                <div>
                  <div class="font-medium">Sarah Martinez</div>
                  <div class="text-sm text-gray-400">CTO, DevTools.io</div>
                </div>
              </div>
            </div>

            <div class="bg-gray-900/50 border border-gray-700 rounded-2xl p-8">
              <div class="flex items-center gap-1 mb-4">
                <span class="text-yellow-400">★★★★★</span>
              </div>
              <p class="text-gray-300 mb-6">
                "The cost estimation feature changed everything.
                I can finally show my CEO why we need to refactor the auth service."
              </p>
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-green-500 rounded-full flex items-center justify-center font-bold">AR</div>
                <div>
                  <div class="font-medium">Alex Rodriguez</div>
                  <div class="text-sm text-gray-400">Engineering Manager, FinTech Co</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA -->
      <section class="py-24 px-8">
        <div class="max-w-4xl mx-auto text-center">
          <h2 class="text-4xl md:text-5xl font-bold mb-6">
            Stop Shipping Bugs.<br>
            <span class="bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
              Start Shipping Confidence.
            </span>
          </h2>
          <p class="text-xl text-gray-400 mb-8">
            Join 500+ teams who trust CodeKarma to protect their production
          </p>
          <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
            <button class="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 px-8 py-4 rounded-xl font-bold text-lg transition transform hover:scale-105">
              Start Free Trial →
            </button>
            <span class="text-gray-400">No credit card required</span>
          </div>
        </div>
      </section>

      <!-- Footer -->
      <footer class="border-t border-gray-800 py-12 px-8">
        <div class="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-8">
          <div class="flex items-center gap-3">
            <span class="text-2xl">🔮</span>
            <span class="text-xl font-bold">CodeKarma</span>
          </div>
          <div class="flex items-center gap-8 text-gray-400">
            <a href="#" class="hover:text-white transition">Documentation</a>
            <a href="#" class="hover:text-white transition">API</a>
            <a href="#" class="hover:text-white transition">Privacy</a>
            <a href="#" class="hover:text-white transition">Terms</a>
          </div>
          <div class="text-gray-500">© 2024 CodeKarma. All rights reserved.</div>
        </div>
      </footer>
    </div>
  `
})
export class LandingComponent {
  roiDevelopers = 20;
  roiReviewHours = 8;
  roiIncidents = 4;
  roiSalary = 150000;

  calculateReviewSavings(): number {
    // 50% of review time saved
    const hourlyRate = this.roiSalary / 2080;
    const hoursSaved = this.roiDevelopers * this.roiReviewHours * 0.5 * 52;
    return Math.round(hoursSaved * hourlyRate);
  }

  calculateIncidentSavings(): number {
    // 40% of incidents prevented, $50K avg cost
    const incidentsPrevented = this.roiIncidents * 12 * 0.4;
    return Math.round(incidentsPrevented * 50000);
  }

  calculateProductivitySavings(): number {
    // 10% productivity improvement
    return Math.round(this.roiDevelopers * this.roiSalary * 0.1);
  }

  calculateTotalSavings(): number {
    return this.calculateReviewSavings() + this.calculateIncidentSavings() + this.calculateProductivitySavings();
  }

  calculateROI(): string {
    const cost = this.roiDevelopers * 29 * 12;
    const savings = this.calculateTotalSavings();
    return (savings / cost).toFixed(1);
  }

  scrollToDemo(): void {
    document.getElementById('demo')?.scrollIntoView({ behavior: 'smooth' });
  }
}
