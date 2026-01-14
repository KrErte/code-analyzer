package com.codeanalyzer.service;

import com.codeanalyzer.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UltraAnalysisService {

    private final EnhancedAnalysisService enhancedAnalysisService;

    public Mono<UltraAnalysisResponse> analyzeUltra(String code, String language, String context, String persona) {
        return enhancedAnalysisService.analyzeEnhanced(code, language, context, persona)
                .map(enhanced -> {
                    String id = UUID.randomUUID().toString();

                    return UltraAnalysisResponse.builder()
                            .id(id)
                            .analyzedAt(System.currentTimeMillis())
                            .coreAnalysis(enhanced)
                            .bugWeather(generateBugWeather(enhanced))
                            .codeInsurance(generateInsuranceQuote(enhanced))
                            .incidentSimulation(generateIncidentSimulation(enhanced))
                            .developerDNA(generateDeveloperDNA(enhanced, code))
                            .shareableCards(generateShareableCards(id, enhanced))
                            .build();
                });
    }

    private BugWeather generateBugWeather(EnhancedAnalysisResponse analysis) {
        int score = analysis.getScore();
        int incidents = analysis.getPredictedIncidents().size();

        String condition;
        String icon;
        String advisory;

        if (score >= 80) {
            condition = "SEVERE BUG STORM";
            icon = "⛈️";
            advisory = "EXTREME BUG WARNING";
        } else if (score >= 60) {
            condition = "Heavy Bug Showers";
            icon = "🌧️";
            advisory = "Bug Storm Watch";
        } else if (score >= 40) {
            condition = "Partly Buggy";
            icon = "⛅";
            advisory = "Bug Advisory";
        } else if (score >= 20) {
            condition = "Mostly Clear";
            icon = "🌤️";
            advisory = null;
        } else {
            condition = "Clear Skies";
            icon = "☀️";
            advisory = null;
        }

        List<BugWeather.HourlyForecast> hourly = new ArrayList<>();
        String[] hours = {"9AM", "10AM", "11AM", "12PM", "1PM", "2PM", "3PM", "4PM", "5PM", "6PM"};
        String[] events = {"Standup", "Code Review", "Lunch Deploy", "Sprint Demo", "PR Merge", "Meeting", "Deploy Window", "On-call Handoff", "EOD Push", "Friday Deploy"};

        Random rand = new Random(score);
        for (int i = 0; i < hours.length; i++) {
            int prob = Math.min(100, score + rand.nextInt(30) - 15);
            hourly.add(BugWeather.HourlyForecast.builder()
                    .hour(hours[i])
                    .condition(prob > 70 ? "Stormy" : prob > 40 ? "Cloudy" : "Clear")
                    .icon(prob > 70 ? "⛈️" : prob > 40 ? "☁️" : "☀️")
                    .bugProbability(prob)
                    .event(events[i])
                    .build());
        }

        List<BugWeather.DailyForecast> weekly = new ArrayList<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] specialEvents = {null, "Patch Tuesday", null, "Sprint End", "DANGER: Friday Deploy", "Weekend Deploy Risk", null};

        for (int i = 0; i < 7; i++) {
            int high = Math.min(100, score + rand.nextInt(20));
            int low = Math.max(0, score - rand.nextInt(30));
            boolean isFriday = days[i].equals("Fri");
            weekly.add(BugWeather.DailyForecast.builder()
                    .day(days[i])
                    .condition(isFriday ? "EXTREME DANGER" : high > 60 ? "Buggy" : "Fair")
                    .icon(isFriday ? "💀" : high > 60 ? "🌧️" : "⛅")
                    .highBugTemp(isFriday ? 100 : high)
                    .lowBugTemp(low)
                    .incidentProbability(isFriday ? 95 : high)
                    .specialEvent(specialEvents[i])
                    .build());
        }

        List<String> warnings = new ArrayList<>();
        if (score > 60) warnings.add("🚨 HIGH BUG ALERT: Avoid deploying today");
        if (incidents > 2) warnings.add("⚠️ INCIDENT CLUSTER WARNING: Multiple incidents predicted");
        if (analysis.getShipItScore() != null && analysis.getShipItScore().getVerdict().contains("SERIOUS")) {
            warnings.add("☢️ CODE RED: This code is a natural disaster waiting to happen");
        }

        return BugWeather.builder()
                .currentCondition(condition)
                .icon(icon)
                .temperature(score)
                .temperatureFeelsLike(score > 80 ? "Feels like mass resignation" : score > 50 ? "Feels like long debugging session" : "Feels like smooth sailing")
                .humidity(analysis.getCostAnalysis() != null ? Math.min(100, analysis.getCostAnalysis().getTechnicalDebtCost() / 1000) : 50)
                .windDirection("Bugs blowing in from " + (rand.nextBoolean() ? "legacy-service" : "third-party-api"))
                .windSpeed(incidents * 10)
                .advisory(advisory)
                .advisoryDetails(advisory != null ? "Based on " + incidents + " predicted incidents and bug score of " + score : null)
                .hourlyForecast(hourly)
                .weeklyForecast(weekly)
                .warnings(warnings)
                .seasonalPattern(BugWeather.SeasonalPattern.builder()
                        .currentSeason(LocalDate.now().getMonthValue() >= 10 ? "Q4 Crunch Season" : "Regular Season")
                        .historicalNote("87% of P0 incidents occur during deploy windows")
                        .upcomingDangerDates(List.of("Black Friday", "Cyber Monday", "Year-End Freeze"))
                        .build())
                .build();
    }

    private CodeInsurance generateInsuranceQuote(EnhancedAnalysisResponse analysis) {
        int score = analysis.getScore();
        int basePremium = 500;
        int riskMultiplier = 1 + (score / 20);
        int monthlyPremium = basePremium * riskMultiplier;

        String riskTier;
        if (score >= 80) riskTier = "UNINSURABLE";
        else if (score >= 60) riskTier = "Extreme";
        else if (score >= 40) riskTier = "High";
        else if (score >= 20) riskTier = "Moderate";
        else riskTier = "Low";

        List<CodeInsurance.RiskFactor> riskFactors = new ArrayList<>();

        if (analysis.getPredictedIncidents().size() > 0) {
            riskFactors.add(CodeInsurance.RiskFactor.builder()
                    .factor("Predicted Incidents")
                    .icon("🚨")
                    .impactPercent(analysis.getPredictedIncidents().size() * 15)
                    .explanation(analysis.getPredictedIncidents().size() + " incidents predicted")
                    .build());
        }

        if (analysis.getCodeKarma() != null && analysis.getCodeKarma().getKarmaScore() < 0) {
            riskFactors.add(CodeInsurance.RiskFactor.builder()
                    .factor("Negative Code Karma")
                    .icon("☯️")
                    .impactPercent(Math.abs(analysis.getCodeKarma().getKarmaScore() / 2))
                    .explanation("Technical debt creates future claims")
                    .build());
        }

        if (analysis.getShipItScore() != null) {
            var rb = analysis.getShipItScore().getRiskBreakdown();
            if (rb != null && rb.getSecurityRisk() > 50) {
                riskFactors.add(CodeInsurance.RiskFactor.builder()
                        .factor("Security Vulnerabilities")
                        .icon("🔓")
                        .impactPercent(rb.getSecurityRisk())
                        .explanation("High security risk = high breach probability")
                        .build());
            }
        }

        List<CodeInsurance.Discount> discounts = List.of(
                CodeInsurance.Discount.builder()
                        .name("Add Unit Tests")
                        .percentOff(15)
                        .requirement("80%+ code coverage")
                        .applied(false)
                        .build(),
                CodeInsurance.Discount.builder()
                        .name("Circuit Breaker Discount")
                        .percentOff(10)
                        .requirement("Implement circuit breakers")
                        .applied(false)
                        .build(),
                CodeInsurance.Discount.builder()
                        .name("Code Review Bonus")
                        .percentOff(5)
                        .requirement("Mandatory 2-person review")
                        .applied(false)
                        .build()
        );

        List<String> exclusions = new ArrayList<>();
        exclusions.add("Friday deployments after 4 PM");
        exclusions.add("Code written during hackathons");
        exclusions.add("Intern code without review");
        exclusions.add("'Quick fixes' that bypass CI");
        if (score > 70) exclusions.add("Any deployment of this specific code");

        return CodeInsurance.builder()
                .policyNumber("BUG-" + System.currentTimeMillis() % 100000)
                .riskTier(riskTier)
                .monthlyQuote(CodeInsurance.Quote.builder()
                        .premium(monthlyPremium)
                        .currency("USD")
                        .breakdown(String.format("Base: $%d, Risk Surcharge: $%d", basePremium, monthlyPremium - basePremium))
                        .build())
                .annualQuote(CodeInsurance.Quote.builder()
                        .premium(monthlyPremium * 10) // 2 months free for annual
                        .currency("USD")
                        .breakdown("Annual discount: 2 months free")
                        .build())
                .deductible(score > 60 ? 50000 : 25000)
                .coverageLimit(1000000)
                .coveredIncidents(List.of("Database outages", "API failures", "Memory leaks", "Security breaches (non-negligent)"))
                .exclusions(exclusions)
                .riskFactors(riskFactors)
                .availableDiscounts(discounts)
                .underwriterNotes(score > 80 ? "We've seen war zones with better code." : score > 50 ? "Recommend remediation before coverage." : "Standard risk profile.")
                .claimsHistory("Based on code patterns: Estimated 2.3 claims per year")
                .industryComparison(CodeInsurance.Comparison.builder()
                        .yourPremium(monthlyPremium)
                        .industryAverage(800)
                        .topPerformers(300)
                        .verdict(monthlyPremium > 1500 ? "Your code costs " + (monthlyPremium / 300) + "x more to insure than well-written code" : "Within acceptable range")
                        .build())
                .build();
    }

    private IncidentSimulation generateIncidentSimulation(EnhancedAnalysisResponse analysis) {
        if (analysis.getPredictedIncidents().isEmpty()) {
            return null;
        }

        ProductionIncident mainIncident = analysis.getPredictedIncidents().get(0);
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        List<IncidentSimulation.SlackMessage> slackTimeline = List.of(
                IncidentSimulation.SlackMessage.builder()
                        .timestamp("T+" + 0 + "min")
                        .channel("#alerts")
                        .sender("PagerDuty Bot")
                        .senderRole("Bot")
                        .avatar("🤖")
                        .message("🚨 ALERT: " + mainIncident.getTitle() + " - " + mainIncident.getSeverity())
                        .reactions(List.of("👀", "😱"))
                        .threadReplies(0)
                        .build(),
                IncidentSimulation.SlackMessage.builder()
                        .timestamp("T+2min")
                        .channel("#incidents")
                        .sender("On-Call Engineer")
                        .senderRole("SRE")
                        .avatar("😰")
                        .message("Looking into this now...")
                        .reactions(List.of("🙏"))
                        .threadReplies(3)
                        .build(),
                IncidentSimulation.SlackMessage.builder()
                        .timestamp("T+5min")
                        .channel("#incidents")
                        .sender("On-Call Engineer")
                        .senderRole("SRE")
                        .avatar("😱")
                        .message("Oh no. This is bad. Really bad.")
                        .reactions(List.of("😬", "🍿", "this-is-fine"))
                        .threadReplies(12)
                        .build(),
                IncidentSimulation.SlackMessage.builder()
                        .timestamp("T+8min")
                        .channel("#engineering")
                        .sender("VP Engineering")
                        .senderRole("VP")
                        .avatar("😤")
                        .message("@channel Can someone explain what's happening? I'm getting calls from the CEO.")
                        .reactions(List.of())
                        .threadReplies(0)
                        .build(),
                IncidentSimulation.SlackMessage.builder()
                        .timestamp("T+15min")
                        .channel("#incidents")
                        .sender("Original Author")
                        .senderRole("Developer")
                        .avatar("💀")
                        .message("That... might be my code. I pushed it Friday at 5:47 PM.")
                        .reactions(List.of("press-f", "rip", "oof"))
                        .threadReplies(47)
                        .build(),
                IncidentSimulation.SlackMessage.builder()
                        .timestamp("T+30min")
                        .channel("#ceo-direct")
                        .sender("CEO")
                        .senderRole("CEO")
                        .avatar("👔")
                        .message("I need a full postmortem on my desk by EOD. This is unacceptable.")
                        .reactions(List.of())
                        .threadReplies(0)
                        .build()
        );

        List<IncidentSimulation.PagerDutyAlert> pagerDutyAlerts = List.of(
                IncidentSimulation.PagerDutyAlert.builder()
                        .timestamp(time)
                        .alertName(mainIncident.getTitle())
                        .severity(mainIncident.getSeverity())
                        .service("production")
                        .assignedTo("on-call-primary")
                        .status("Triggered")
                        .escalationLevel(1)
                        .sound("air-raid")
                        .build(),
                IncidentSimulation.PagerDutyAlert.builder()
                        .timestamp("T+15min")
                        .alertName("Escalation: " + mainIncident.getTitle())
                        .severity(mainIncident.getSeverity())
                        .service("production")
                        .assignedTo("engineering-manager")
                        .status("Triggered")
                        .escalationLevel(2)
                        .sound("persistent")
                        .build()
        );

        List<IncidentSimulation.Tweet> tweets = List.of(
                IncidentSimulation.Tweet.builder()
                        .handle("@angrycustomer1")
                        .displayName("Frustrated User")
                        .verified(false)
                        .tweet("@YourStartup is down AGAIN?? This is the third time this month. Switching to competitor.")
                        .likes(234)
                        .retweets(45)
                        .sentiment("angry")
                        .build(),
                IncidentSimulation.Tweet.builder()
                        .handle("@techblogger")
                        .displayName("Tech News Daily")
                        .verified(true)
                        .tweet("BREAKING: @YourStartup experiencing major outage. Users reporting data loss. Story developing...")
                        .likes(1247)
                        .retweets(523)
                        .sentiment("mocking")
                        .build(),
                IncidentSimulation.Tweet.builder()
                        .handle("@competitorCEO")
                        .displayName("Competitor CEO")
                        .verified(true)
                        .tweet("We're here for you if you need a reliable alternative. DMs open. 🙏")
                        .likes(892)
                        .retweets(156)
                        .sentiment("opportunistic")
                        .build()
        );

        List<IncidentSimulation.Email> emails = List.of(
                IncidentSimulation.Email.builder()
                        .timestamp("T+45min")
                        .from("CEO")
                        .fromTitle("Chief Executive Officer")
                        .to("Engineering Team")
                        .subject("RE: Production Incident - URGENT")
                        .body("Team,\n\nI just got off the phone with our largest customer. They are threatening to cancel their contract.\n\nI need answers:\n1. What happened?\n2. Why did it happen?\n3. How do we make sure it NEVER happens again?\n\nI want a meeting in 30 minutes.")
                        .tone("furious")
                        .ccedLegal(false)
                        .build(),
                IncidentSimulation.Email.builder()
                        .timestamp("T+2hrs")
                        .from("Legal")
                        .fromTitle("General Counsel")
                        .to("Engineering Leadership")
                        .subject("RE: Incident Documentation Request")
                        .body("Please preserve all logs, chat messages, and communications related to this incident. We may need them for regulatory compliance.\n\nDo not discuss details externally.")
                        .tone("formal")
                        .ccedLegal(true)
                        .build()
        );

        int cost = mainIncident.getCostEstimate() != null ? mainIncident.getCostEstimate().getMaxDollars() : 100000;

        return IncidentSimulation.builder()
                .incidentId("INC-" + System.currentTimeMillis() % 10000)
                .codename("Operation " + mainIncident.getTitle().split(" ")[0])
                .severity(mainIncident.getSeverity())
                .triggerEvent(mainIncident.getScenario())
                .slackTimeline(slackTimeline)
                .pagerDutyAlerts(pagerDutyAlerts)
                .twitterReactions(tweets)
                .emailChain(emails)
                .statusPage(IncidentSimulation.StatusPageUpdate.builder()
                        .status("Major Outage")
                        .message("We are currently experiencing issues with our service. Our team is actively investigating.")
                        .affectedServices(List.of("API", "Dashboard", "Webhooks"))
                        .lastUpdated(time)
                        .updateHistory(List.of(
                                time + " - Investigating reports of service degradation",
                                "T+10min - Issue identified, working on fix",
                                "T+30min - Fix deployed, monitoring"
                        ))
                        .build())
                .metrics(IncidentSimulation.IncidentMetrics.builder()
                        .timeToDetectMinutes(2)
                        .timeToAcknowledgeMinutes(5)
                        .timeToMitigateMinutes(45)
                        .timeToResolveMinutes(180)
                        .customersAffected(15000)
                        .supportTickets(847)
                        .angryTweets(234)
                        .revenueImpact(cost * 0.8)
                        .stockPriceImpact(-2.3)
                        .build())
                .postmortemDraft(IncidentSimulation.PostmortemDraft.builder()
                        .title("Postmortem: " + mainIncident.getTitle())
                        .severity(mainIncident.getSeverity())
                        .summary(mainIncident.getWhatHappens())
                        .rootCause(mainIncident.getRootCause())
                        .timeline(List.of(
                                "T+0 - Alert fires",
                                "T+2min - On-call acknowledges",
                                "T+5min - Severity escalated to " + mainIncident.getSeverity(),
                                "T+45min - Root cause identified",
                                "T+60min - Fix deployed",
                                "T+180min - All clear declared"
                        ))
                        .actionItems(List.of(
                                "Add monitoring for this failure mode",
                                "Implement circuit breaker",
                                "Update runbook",
                                "Schedule blameless postmortem"
                        ))
                        .blamelessAnalysis("While we practice blameless postmortems, it should be noted that this code was deployed on Friday at 5:47 PM without proper review.")
                        .lessonsLearned("Never deploy on Friday. Never skip code review. Never trust 'it works on my machine.'")
                        .build())
                .build();
    }

    private DeveloperDNA generateDeveloperDNA(EnhancedAnalysisResponse analysis, String code) {
        int score = analysis.getScore();
        Random rand = new Random(code.hashCode());

        // Analyze code patterns for DNA
        int soPercent = code.contains("stackoverflow") || code.length() < 100 ? 40 + rand.nextInt(20) : 20 + rand.nextInt(15);
        int tutorialPercent = code.contains("TODO") || code.contains("FIXME") ? 25 + rand.nextInt(15) : 10 + rand.nextInt(10);
        int engineeringPercent = Math.max(5, 100 - soPercent - tutorialPercent - 15 - rand.nextInt(10));
        int prayerPercent = score > 60 ? 15 + rand.nextInt(10) : 5 + rand.nextInt(5);
        int aiPercent = 100 - soPercent - tutorialPercent - engineeringPercent - prayerPercent;

        String[] personalities = {"INTJ-NullPointer", "ENFP-Spaghetti", "ISTJ-Defensive", "ENTP-Hacker", "INFP-Overthinker", "ESTJ-Enterprise"};
        String[] spirits = {"Chaos Monkey", "Rubber Duck", "Stack Overflow Parrot", "Git Blame Detective", "Coffee-Driven Machine", "Imposter Syndrome Gremlin"};
        String[] alignments = {"Lawful Good (writes tests first)", "Chaotic Neutral (moves fast, breaks things)", "True Neutral (just ships code)", "Chaotic Evil (deploys on Friday)", "Lawful Evil (enterprise architect)"};

        String personality = personalities[Math.abs(code.hashCode()) % personalities.length];
        String spirit = spirits[Math.abs(code.hashCode() / 2) % spirits.length];
        String alignment = score > 70 ? alignments[3] : score > 40 ? alignments[2] : alignments[0];

        List<DeveloperDNA.Ancestor> ancestors = List.of(
                DeveloperDNA.Ancestor.builder()
                        .name(score > 60 ? "Mark Zuckerberg" : score > 30 ? "Linus Torvalds" : "Grace Hopper")
                        .trait(score > 60 ? "Move fast and break things mentality" : score > 30 ? "Strong opinions, weakly held" : "Attention to detail")
                        .similarityPercent(60 + rand.nextInt(30))
                        .quote(score > 60 ? "\"Move fast and break things\"" : score > 30 ? "\"Talk is cheap. Show me the code.\"" : "\"The most dangerous phrase is 'We've always done it this way.'\"")
                        .build(),
                DeveloperDNA.Ancestor.builder()
                        .name("The Developer Who Wrote That Legacy System")
                        .trait("Leaving cryptic comments")
                        .similarityPercent(30 + rand.nextInt(20))
                        .quote("\"// don't touch this, it works somehow\"")
                        .build()
        );

        String idealPartner = score > 60 ? "The Meticulous Reviewer" : "The Move-Fast Shipper";
        String avoid = score > 60 ? "Another chaotic deployer" : "Overly cautious gatekeepers";

        return DeveloperDNA.builder()
                .devId("DNA-" + Math.abs(code.hashCode() % 100000))
                .personalityType(personality)
                .spiritAnimal(spirit)
                .alignment(alignment)
                .codeOrigins(DeveloperDNA.DNABreakdown.builder()
                        .stackOverflowPercent(soPercent)
                        .tutorialCodePercent(tutorialPercent)
                        .actualEngineeringPercent(engineeringPercent)
                        .prayersAndHopesPercent(prayerPercent)
                        .legacyCopyPastePercent(rand.nextInt(15))
                        .aiGeneratedPercent(aiPercent)
                        .build())
                .codingStyle(DeveloperDNA.CodingStyle.builder()
                        .primaryStyle(score > 60 ? "The Speedrunner" : score > 30 ? "The Pragmatist" : "The Craftsman")
                        .secondaryStyle(score > 50 ? "The Optimist" : "The Pessimist")
                        .strengths(List.of("Ships fast", "Gets things done", "Creative problem solving"))
                        .weaknesses(List.of(score > 60 ? "Error handling" : "Over-engineering", score > 50 ? "Testing" : "Moving too slow"))
                        .debuggingApproach(score > 70 ? "Console.log everything" : score > 40 ? "Debugger + coffee" : "Systematic analysis")
                        .documentationHabit(score > 60 ? "What's documentation?" : score > 30 ? "TODO: add docs" : "Comprehensive")
                        .testingPhilosophy(score > 70 ? "If it compiles, ship it" : score > 40 ? "Happy path only" : "TDD enthusiast")
                        .build())
                .codingAncestors(ancestors)
                .teamCompatibility(DeveloperDNA.CompatibilityReport.builder()
                        .idealPartner(idealPartner)
                        .avoidAtAllCosts(avoid)
                        .teamDynamics(List.of(
                                DeveloperDNA.TeamDynamic.builder()
                                        .devType("The Architect")
                                        .compatibilityScore(score > 50 ? 40 : 80)
                                        .prediction(score > 50 ? "Will argue about design patterns" : "Great collaboration potential")
                                        .build(),
                                DeveloperDNA.TeamDynamic.builder()
                                        .devType("The DevOps Engineer")
                                        .compatibilityScore(score > 60 ? 30 : 70)
                                        .prediction(score > 60 ? "They will block your deploys" : "Smooth pipeline integration")
                                        .build()
                        ))
                        .build())
                .careerPrediction(DeveloperDNA.CareerPrediction.builder()
                        .fiveYears(score > 70 ? "Senior Incident Responder" : score > 40 ? "Staff Engineer" : "Principal Engineer")
                        .tenYears(score > 70 ? "Startup founder (failed)" : score > 40 ? "Engineering Manager" : "CTO")
                        .peakTitle(score > 60 ? "Distinguished Fire Fighter" : "Distinguished Engineer")
                        .alternativeCareer(score > 70 ? "DevRel (sharing war stories)" : score > 40 ? "Technical Writer" : "Woodworking")
                        .build())
                .build();
    }

    private UltraAnalysisResponse.ShareableCards generateShareableCards(String id, EnhancedAnalysisResponse analysis) {
        String verdict = analysis.getShipItScore() != null ? analysis.getShipItScore().getVerdict() : "UNKNOWN";
        int karma = analysis.getCodeKarma() != null ? analysis.getCodeKarma().getKarmaScore() : 0;

        String twitterText = String.format(
                "My code just got analyzed by Code Karma 🎰\n\nVerdict: %s\nKarma Score: %d\nPredicted Incidents: %d\n\nHow bad is YOUR code? 👀\n\n#CodeKarma #DevLife",
                verdict, karma, analysis.getPredictedIncidents().size()
        );

        String linkedInText = String.format(
                "I used Code Karma to analyze my code and learned some hard truths.\n\nVerdict: %s\nCode Karma: %d\n\nEvery developer should try this - it's like a health checkup for your code. What's your score?",
                verdict, karma
        );

        return UltraAnalysisResponse.ShareableCards.builder()
                .weatherCardUrl("/api/cards/weather/" + id)
                .insuranceQuoteUrl("/api/cards/insurance/" + id)
                .dnaCardUrl("/api/cards/dna/" + id)
                .verdictCardUrl("/api/cards/verdict/" + id)
                .twitterShareText(twitterText)
                .linkedInShareText(linkedInText)
                .build();
    }
}
