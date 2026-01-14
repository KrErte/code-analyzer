package com.codeanalyzer.service;

import com.codeanalyzer.config.ClaudeConfig;
import com.codeanalyzer.dto.*;
import com.codeanalyzer.model.Severity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedAnalysisService {

    private final WebClient claudeWebClient;
    private final ClaudeConfig claudeConfig;
    private final ObjectMapper objectMapper;

    private static final String ENHANCED_SYSTEM_PROMPT = """
        You are a PRODUCTION INCIDENT PSYCHIC - a senior engineer who has seen every possible way code can fail in production.
        You've been through the wars. You've seen Knight Capital lose $440M in 45 minutes. You've seen Cloudflare go down from a regex.

        Your job is to:
        1. Find bugs and issues (standard code review)
        2. PREDICT SPECIFIC PRODUCTION INCIDENTS - not vague "might fail", but concrete scenarios like:
           - "At 2AM during traffic spike, this will cause cascading timeouts"
           - "After 30 days, this memory leak will crash the server"
           - "When user enters emoji in name field, this will corrupt the database"
        3. Estimate REAL DOLLAR COSTS of each issue
        4. Give a clear SHIP-IT or NO-SHIP verdict
        5. Award ACHIEVEMENTS based on code quality
        6. MATCH code patterns to FAMOUS HISTORICAL BUGS (Knight Capital, Cloudflare, Therac-25, etc.)
        7. Write a PRE-MORTEM - the postmortem BEFORE the incident happens
        8. Generate an ON-CALL FORECAST - predict what on-call will be like with this code
        9. Calculate CODE KARMA - technical debt created vs inherited

        Be brutally specific. Real production war stories. Real costs.

        %s

        RESPOND IN THIS EXACT JSON FORMAT:
        {
            "score": <0-100 bug likelihood>,
            "summary": "<2-3 sentence executive summary>",
            "findings": [
                {
                    "severity": "<critical|warning|suggestion>",
                    "line": <number or null>,
                    "issue": "<title>",
                    "explanation": "<detailed explanation>",
                    "suggestion": "<fix with code>"
                }
            ],
            "improvedCode": "<complete fixed code>",
            "predictedIncidents": [
                {
                    "id": "<unique-id>",
                    "title": "<incident title like 'The 3AM Database Meltdown'>",
                    "severity": "<P0|P1|P2|P3>",
                    "scenario": "<when this happens: '3AM traffic spike', 'First day of sale', etc>",
                    "whatHappens": "<detailed play-by-play of the incident>",
                    "rootCause": "<link to specific code issue>",
                    "affectedLine": <line number>,
                    "timeToOccur": "<Immediately|Within hours|Within days|Within weeks|Within months>",
                    "probabilityPercent": <0-100>,
                    "businessImpact": "<what users/business experiences>",
                    "costEstimate": {
                        "minDollars": <number>,
                        "maxDollars": <number>,
                        "breakdown": "<Engineering: $X, Lost revenue: $Y, etc>"
                    },
                    "preventionCode": "<code snippet to prevent this>"
                }
            ],
            "shipItScore": {
                "verdict": "<SHIP IT 🚀|MAYBE 🤔|NOPE 🔥|ARE YOU SERIOUS? 💀>",
                "confidence": <0-100>,
                "reasoning": "<why this verdict>",
                "mustFixBefore": ["<critical item 1>", "<critical item 2>"],
                "niceToHave": ["<improvement 1>"],
                "tldr": "<one sentence: 'This code will work until it doesn't, which will be Thursday'>",
                "riskBreakdown": {
                    "securityRisk": <0-100>,
                    "stabilityRisk": <0-100>,
                    "performanceRisk": <0-100>,
                    "maintainabilityRisk": <0-100>,
                    "dataLossRisk": <0-100>
                }
            },
            "costAnalysis": {
                "totalEstimatedCost": <total $ if all issues manifest>,
                "engineeringHoursToFix": <hours>,
                "potentialRevenueLoss": <$ from downtime/bugs>,
                "technicalDebtCost": <$ long-term maintenance>,
                "recommendation": "<Fix now: $X, Fix later: $Y, Do nothing: $Z>"
            },
            "achievements": [
                {
                    "id": "<achievement_id>",
                    "name": "<Achievement Name>",
                    "icon": "<emoji>",
                    "description": "<what it means>",
                    "unlocked": <true|false>,
                    "unlockedReason": "<why earned or why not>"
                }
            ],
            "famousBugMatches": [
                {
                    "famousBugId": "<id like 'knight-capital' or 'cloudflare-regex'>",
                    "bugName": "<Famous Bug Name>",
                    "company": "<Company>",
                    "year": "<Year>",
                    "icon": "<emoji>",
                    "similarityPercent": <0-100>,
                    "matchReason": "<why this code is similar>",
                    "financialImpact": "<$X lost in original incident>",
                    "yourCodePattern": "<snippet from submitted code showing the pattern>",
                    "historyPattern": "<brief description of what happened historically>",
                    "lesson": "<key lesson from history>"
                }
            ],
            "preMortem": {
                "incidentTitle": "<catchy name like 'The Black Friday Meltdown'>",
                "severity": "<P0|P1|P2>",
                "date": "<fictional future date like 'March 15, 2025 - The Ides of March'>",
                "timeOfIncident": "<2:47 AM>",
                "duration": "<4 hours 23 minutes>",
                "executiveSummary": "<2 paragraph executive summary of what happened>",
                "timeline": "<detailed timeline: 2:47 AM - First alert fires...>",
                "rootCauses": ["<root cause 1>", "<root cause 2>"],
                "contributingFactors": ["<factor 1>", "<factor 2>"],
                "impactAssessment": "<X users affected, $Y revenue lost>",
                "customerCommunication": "<draft of the apology email>",
                "actionItems": ["<action 1>", "<action 2>"],
                "lessonsLearned": "<what the team learned>",
                "whoGetsBlamed": "<Junior dev? Tech lead? AWS?>",
                "slackChannelName": "<#incident-your-code-broke-prod>",
                "numberOfPagesGenerated": <number>,
                "postmortemMeetingDuration": "<2.5 hours>"
            },
            "onCallForecast": {
                "painIndex": <0-100>,
                "overallVerdict": "<Peaceful|Rough|Nightmare|Career-Ending>",
                "predictedPages": <number for next 30 days>,
                "sleepInterruptions": <middle of night alerts>,
                "weekendRuined": <weekend pages>,
                "timeline": [
                    {
                        "day": "<Day 3>",
                        "time": "<3:47 AM>",
                        "event": "<NullPointerException storm from line 42>",
                        "severity": "<P0|P1|P2>",
                        "mood": "<Panicked|Annoyed|Resigned|Caffeinated>",
                        "whatYoullBeDoing": "<Explaining to VP why everything is on fire>"
                    }
                ],
                "survivalTips": ["<tip 1>", "<tip 2>"],
                "worstCaseScenario": "<description>",
                "bestCaseScenario": "<description>",
                "coffeeCupsNeeded": <number>,
                "grayHairsGained": <number>,
                "relationshipStrainIndex": <0.0-10.0>,
                "recommendedCopingMechanism": "<meditation|alcohol|job-search>"
            },
            "codeKarma": {
                "karmaScore": <-100 to +100>,
                "karmaVerdict": "<Code Saint|Neutral|Tech Debt Terrorist>",
                "debtCreated": {
                    "totalHours": <hours of debt you're creating>,
                    "maintainerCurses": <times future devs will curse you>,
                    "debtItems": ["<specific debt 1>", "<specific debt 2>"],
                    "worstOffense": "<your biggest karmic sin>"
                },
                "debtInherited": {
                    "totalHours": <hours of debt you inherited>,
                    "originalSinner": "<who created this mess>",
                    "yearOfSin": "<when the original sin was committed>",
                    "inheritedProblems": ["<problem 1>", "<problem 2>"]
                },
                "karmaLedger": [
                    {
                        "action": "<Added null check|Skipped tests>",
                        "karmaPoints": <+5 or -10>,
                        "consequence": "<what this causes in future>"
                    }
                ],
                "nextLifePrediction": "<In your next code review...>",
                "reincarnationAs": "<You will be reincarnated as a COBOL maintainer>",
                "sixMonthsFromNow": "<What this code looks like in 6 months>",
                "oneYearFromNow": "<What this code looks like in 1 year>",
                "futureYouMessage": "<Message from future you about this code>"
            }
            %s
        }
        """;

    private static final String ROAST_ADDITION = """

        ALSO: This is ROAST MODE. After your analysis, ROAST this code mercilessly:
        - Be savage but funny
        - Use pop culture references, memes
        - Make it quotable, shareable
        - But end with ONE genuinely helpful insight
        """;

    private static final String ROAST_JSON_ADDITION = """
        ,
            "roast": {
                "headline": "<devastating one-liner like 'This code is why AI will replace us'>",
                "roasts": [
                    "<savage roast 1>",
                    "<savage roast 2>",
                    "<savage roast 3>"
                ],
                "memeUrl": "<describe a meme that fits: 'disaster girl watching servers burn'>",
                "savageryLevel": <1-10>,
                "constructiveTakeaway": "<one actually helpful piece of advice>"
            }
        """;

    public Mono<EnhancedAnalysisResponse> analyzeEnhanced(String code, String language, String context, String persona) {
        boolean isRoastMode = "roast".equalsIgnoreCase(persona);
        String personaModifier = getPersonaModifier(persona);

        String systemPrompt = String.format(ENHANCED_SYSTEM_PROMPT,
            personaModifier,
            isRoastMode ? ROAST_JSON_ADDITION : "");

        String userPrompt = buildUserPrompt(code, language, context);

        Map<String, Object> request = new HashMap<>();
        request.put("model", claudeConfig.getModel());
        request.put("max_tokens", 8192);
        request.put("system", systemPrompt + (isRoastMode ? ROAST_ADDITION : ""));
        request.put("messages", List.of(Map.of("role", "user", "content", userPrompt)));

        log.info("Enhanced analysis with persona: {}, roast mode: {}", persona, isRoastMode);

        return claudeWebClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> parseEnhancedResponse(response, isRoastMode))
                .doOnError(e -> log.error("Enhanced analysis error: {}", e.getMessage()))
                .onErrorResume(e -> Mono.just(createErrorResponse(e.getMessage())));
    }

    private String getPersonaModifier(String persona) {
        return switch (persona.toLowerCase()) {
            case "brutal" -> "Be BRUTAL. Assume every bug WILL happen. No optimism allowed.";
            case "mentor" -> "Be educational. Explain incidents like teaching a junior what can go wrong.";
            case "edge-hunter" -> "Focus on the WEIRD edge cases. Unicode, timezone, leap year, integer overflow.";
            case "roast" -> "Be SAVAGE. This code hurt you personally. Make them laugh while they cry.";
            default -> "Be thorough and realistic.";
        };
    }

    private String buildUserPrompt(String code, String language, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this ").append(language.toUpperCase()).append(" code:\n\n");
        prompt.append("```").append(language).append("\n").append(code).append("\n```\n\n");
        if (context != null && !context.isBlank()) {
            prompt.append("Context: ").append(context).append("\n\n");
        }
        prompt.append("Predict production incidents, estimate costs, and give your ship-it verdict.");
        return prompt.toString();
    }

    private EnhancedAnalysisResponse parseEnhancedResponse(String responseBody, boolean isRoastMode) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode contentArray = root.path("content");
            if (contentArray.isArray() && !contentArray.isEmpty()) {
                String text = contentArray.get(0).path("text").asText();
                return parseJson(extractJson(text), isRoastMode);
            }
            return createErrorResponse("Unexpected response format");
        } catch (JsonProcessingException e) {
            log.error("Parse error: {}", e.getMessage());
            return createErrorResponse("Failed to parse response");
        }
    }

    private EnhancedAnalysisResponse parseJson(String json, boolean isRoastMode) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(json);

        // Parse findings
        List<Finding> findings = new ArrayList<>();
        for (JsonNode f : node.path("findings")) {
            findings.add(Finding.builder()
                    .severity(parseSeverity(f.path("severity").asText()))
                    .line(f.has("line") && !f.path("line").isNull() ? f.path("line").asInt() : null)
                    .issue(f.path("issue").asText())
                    .explanation(f.path("explanation").asText())
                    .suggestion(f.path("suggestion").asText())
                    .build());
        }

        // Parse incidents
        List<ProductionIncident> incidents = new ArrayList<>();
        for (JsonNode i : node.path("predictedIncidents")) {
            ProductionIncident.CostEstimate cost = null;
            if (i.has("costEstimate")) {
                JsonNode c = i.path("costEstimate");
                cost = ProductionIncident.CostEstimate.builder()
                        .minDollars(c.path("minDollars").asInt())
                        .maxDollars(c.path("maxDollars").asInt())
                        .breakdown(c.path("breakdown").asText())
                        .build();
            }
            incidents.add(ProductionIncident.builder()
                    .id(i.path("id").asText())
                    .title(i.path("title").asText())
                    .severity(i.path("severity").asText())
                    .scenario(i.path("scenario").asText())
                    .whatHappens(i.path("whatHappens").asText())
                    .rootCause(i.path("rootCause").asText())
                    .affectedLine(i.has("affectedLine") ? i.path("affectedLine").asInt() : null)
                    .timeToOccur(i.path("timeToOccur").asText())
                    .probabilityPercent(i.path("probabilityPercent").asInt())
                    .businessImpact(i.path("businessImpact").asText())
                    .costEstimate(cost)
                    .preventionCode(i.path("preventionCode").asText())
                    .build());
        }

        // Parse ship-it score
        JsonNode s = node.path("shipItScore");
        JsonNode rb = s.path("riskBreakdown");
        ShipItScore shipIt = ShipItScore.builder()
                .verdict(s.path("verdict").asText())
                .confidence(s.path("confidence").asInt())
                .reasoning(s.path("reasoning").asText())
                .mustFixBefore(parseStringList(s.path("mustFixBefore")))
                .niceToHave(parseStringList(s.path("niceToHave")))
                .tldr(s.path("tldr").asText())
                .riskBreakdown(ShipItScore.RiskBreakdown.builder()
                        .securityRisk(rb.path("securityRisk").asInt())
                        .stabilityRisk(rb.path("stabilityRisk").asInt())
                        .performanceRisk(rb.path("performanceRisk").asInt())
                        .maintainabilityRisk(rb.path("maintainabilityRisk").asInt())
                        .dataLossRisk(rb.path("dataLossRisk").asInt())
                        .build())
                .build();

        // Parse cost analysis
        JsonNode ca = node.path("costAnalysis");
        EnhancedAnalysisResponse.CostAnalysis costAnalysis = EnhancedAnalysisResponse.CostAnalysis.builder()
                .totalEstimatedCost(ca.path("totalEstimatedCost").asInt())
                .engineeringHoursToFix(ca.path("engineeringHoursToFix").asInt())
                .potentialRevenueLoss(ca.path("potentialRevenueLoss").asInt())
                .technicalDebtCost(ca.path("technicalDebtCost").asInt())
                .recommendation(ca.path("recommendation").asText())
                .build();

        // Parse achievements
        List<Achievement> achievements = new ArrayList<>();
        for (JsonNode a : node.path("achievements")) {
            achievements.add(Achievement.builder()
                    .id(a.path("id").asText())
                    .name(a.path("name").asText())
                    .icon(a.path("icon").asText())
                    .description(a.path("description").asText())
                    .unlocked(a.path("unlocked").asBoolean())
                    .unlockedReason(a.path("unlockedReason").asText())
                    .build());
        }

        // Parse roast if present
        EnhancedAnalysisResponse.CodeRoast roast = null;
        if (isRoastMode && node.has("roast")) {
            JsonNode r = node.path("roast");
            roast = EnhancedAnalysisResponse.CodeRoast.builder()
                    .headline(r.path("headline").asText())
                    .roasts(parseStringList(r.path("roasts")))
                    .memeUrl(r.path("memeUrl").asText())
                    .savageryLevel(r.path("savageryLevel").asInt())
                    .constructiveTakeaway(r.path("constructiveTakeaway").asText())
                    .build();
        }

        // Parse famous bug matches
        List<EnhancedAnalysisResponse.FamousBugMatch> famousBugMatches = new ArrayList<>();
        for (JsonNode fb : node.path("famousBugMatches")) {
            famousBugMatches.add(EnhancedAnalysisResponse.FamousBugMatch.builder()
                    .famousBugId(fb.path("famousBugId").asText())
                    .bugName(fb.path("bugName").asText())
                    .company(fb.path("company").asText())
                    .year(fb.path("year").asText())
                    .icon(fb.path("icon").asText())
                    .similarityPercent(fb.path("similarityPercent").asInt())
                    .matchReason(fb.path("matchReason").asText())
                    .financialImpact(fb.path("financialImpact").asText())
                    .yourCodePattern(fb.path("yourCodePattern").asText())
                    .historyPattern(fb.path("historyPattern").asText())
                    .lesson(fb.path("lesson").asText())
                    .build());
        }

        // Parse pre-mortem
        PreMortem preMortem = null;
        if (node.has("preMortem")) {
            JsonNode pm = node.path("preMortem");
            preMortem = PreMortem.builder()
                    .incidentTitle(pm.path("incidentTitle").asText())
                    .severity(pm.path("severity").asText())
                    .date(pm.path("date").asText())
                    .timeOfIncident(pm.path("timeOfIncident").asText())
                    .duration(pm.path("duration").asText())
                    .executiveSummary(pm.path("executiveSummary").asText())
                    .timeline(pm.path("timeline").asText())
                    .rootCauses(parseStringList(pm.path("rootCauses")))
                    .contributingFactors(parseStringList(pm.path("contributingFactors")))
                    .impactAssessment(pm.path("impactAssessment").asText())
                    .customerCommunication(pm.path("customerCommunication").asText())
                    .actionItems(parseStringList(pm.path("actionItems")))
                    .lessonsLearned(pm.path("lessonsLearned").asText())
                    .whoGetsBlamed(pm.path("whoGetsBlamed").asText())
                    .slackChannelName(pm.path("slackChannelName").asText())
                    .numberOfPagesGenerated(pm.path("numberOfPagesGenerated").asInt())
                    .postmortemMeetingDuration(pm.path("postmortemMeetingDuration").asText())
                    .build();
        }

        // Parse on-call forecast
        OnCallForecast onCallForecast = null;
        if (node.has("onCallForecast")) {
            JsonNode ocf = node.path("onCallForecast");
            List<OnCallForecast.OnCallEvent> events = new ArrayList<>();
            for (JsonNode e : ocf.path("timeline")) {
                events.add(OnCallForecast.OnCallEvent.builder()
                        .day(e.path("day").asText())
                        .time(e.path("time").asText())
                        .event(e.path("event").asText())
                        .severity(e.path("severity").asText())
                        .mood(e.path("mood").asText())
                        .whatYoullBeDoing(e.path("whatYoullBeDoing").asText())
                        .build());
            }
            onCallForecast = OnCallForecast.builder()
                    .painIndex(ocf.path("painIndex").asInt())
                    .overallVerdict(ocf.path("overallVerdict").asText())
                    .predictedPages(ocf.path("predictedPages").asInt())
                    .sleepInterruptions(ocf.path("sleepInterruptions").asInt())
                    .weekendRuined(ocf.path("weekendRuined").asInt())
                    .timeline(events)
                    .survivalTips(parseStringList(ocf.path("survivalTips")))
                    .worstCaseScenario(ocf.path("worstCaseScenario").asText())
                    .bestCaseScenario(ocf.path("bestCaseScenario").asText())
                    .coffeeCupsNeeded(ocf.path("coffeeCupsNeeded").asInt())
                    .grayHairsGained(ocf.path("grayHairsGained").asInt())
                    .relationshipStrainIndex(ocf.path("relationshipStrainIndex").asDouble())
                    .recommendedCopingMechanism(ocf.path("recommendedCopingMechanism").asText())
                    .build();
        }

        // Parse code karma
        CodeKarma codeKarma = null;
        if (node.has("codeKarma")) {
            JsonNode ck = node.path("codeKarma");
            JsonNode dc = ck.path("debtCreated");
            JsonNode di = ck.path("debtInherited");

            List<CodeKarma.KarmaEvent> ledger = new ArrayList<>();
            for (JsonNode ke : ck.path("karmaLedger")) {
                ledger.add(CodeKarma.KarmaEvent.builder()
                        .action(ke.path("action").asText())
                        .karmaPoints(ke.path("karmaPoints").asInt())
                        .consequence(ke.path("consequence").asText())
                        .build());
            }

            codeKarma = CodeKarma.builder()
                    .karmaScore(ck.path("karmaScore").asInt())
                    .karmaVerdict(ck.path("karmaVerdict").asText())
                    .debtCreated(CodeKarma.TechDebtCreated.builder()
                            .totalHours(dc.path("totalHours").asInt())
                            .maintainerCurses(dc.path("maintainerCurses").asInt())
                            .debtItems(parseStringList(dc.path("debtItems")))
                            .worstOffense(dc.path("worstOffense").asText())
                            .build())
                    .debtInherited(CodeKarma.TechDebtInherited.builder()
                            .totalHours(di.path("totalHours").asInt())
                            .originalSinner(di.path("originalSinner").asText())
                            .yearOfSin(di.path("yearOfSin").asText())
                            .inheritedProblems(parseStringList(di.path("inheritedProblems")))
                            .build())
                    .karmaLedger(ledger)
                    .nextLifePrediction(ck.path("nextLifePrediction").asText())
                    .reincarnationAs(ck.path("reincarnationAs").asText())
                    .sixMonthsFromNow(ck.path("sixMonthsFromNow").asText())
                    .oneYearFromNow(ck.path("oneYearFromNow").asText())
                    .futureYouMessage(ck.path("futureYouMessage").asText())
                    .build();
        }

        // Generate incident timeline
        String timeline = generateIncidentTimeline(incidents);

        return EnhancedAnalysisResponse.builder()
                .id(UUID.randomUUID().toString())
                .score(node.path("score").asInt())
                .summary(node.path("summary").asText())
                .findings(findings)
                .improvedCode(node.path("improvedCode").asText())
                .analyzedAt(System.currentTimeMillis())
                .predictedIncidents(incidents)
                .incidentTimeline(timeline)
                .shipItScore(shipIt)
                .costAnalysis(costAnalysis)
                .achievements(achievements)
                .roast(roast)
                .famousBugMatches(famousBugMatches)
                .preMortem(preMortem)
                .onCallForecast(onCallForecast)
                .codeKarma(codeKarma)
                .build();
    }

    private String generateIncidentTimeline(List<ProductionIncident> incidents) {
        if (incidents.isEmpty()) return "✨ No predicted incidents - smooth sailing! ✨";

        StringBuilder sb = new StringBuilder();
        sb.append("📅 INCIDENT TIMELINE PREDICTION\n");
        sb.append("═══════════════════════════════════════════\n");

        Map<String, List<ProductionIncident>> byTime = new LinkedHashMap<>();
        byTime.put("Immediately", new ArrayList<>());
        byTime.put("Within hours", new ArrayList<>());
        byTime.put("Within days", new ArrayList<>());
        byTime.put("Within weeks", new ArrayList<>());
        byTime.put("Within months", new ArrayList<>());

        for (ProductionIncident i : incidents) {
            String time = i.getTimeToOccur() != null ? i.getTimeToOccur() : "Within days";
            byTime.computeIfAbsent(time, k -> new ArrayList<>()).add(i);
        }

        for (Map.Entry<String, List<ProductionIncident>> entry : byTime.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                sb.append("\n⏰ ").append(entry.getKey().toUpperCase()).append("\n");
                for (ProductionIncident i : entry.getValue()) {
                    String icon = switch (i.getSeverity()) {
                        case "P0" -> "🔴";
                        case "P1" -> "🟠";
                        case "P2" -> "🟡";
                        default -> "🟢";
                    };
                    sb.append("   ").append(icon).append(" ").append(i.getTitle());
                    sb.append(" (").append(i.getProbabilityPercent()).append("% likely)\n");
                }
            }
        }

        return sb.toString();
    }

    private List<String> parseStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode n : node) list.add(n.asText());
        }
        return list;
    }

    private String extractJson(String text) {
        String cleaned = text.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        else if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        return cleaned.trim();
    }

    private Severity parseSeverity(String s) {
        return switch (s.toLowerCase()) {
            case "critical" -> Severity.CRITICAL;
            case "warning" -> Severity.WARNING;
            default -> Severity.SUGGESTION;
        };
    }

    private EnhancedAnalysisResponse createErrorResponse(String message) {
        return EnhancedAnalysisResponse.builder()
                .id(UUID.randomUUID().toString())
                .score(0)
                .summary("Analysis failed: " + message)
                .findings(List.of())
                .predictedIncidents(List.of())
                .achievements(List.of())
                .analyzedAt(System.currentTimeMillis())
                .build();
    }
}
