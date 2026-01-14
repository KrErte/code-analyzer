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
        You are a senior engineer predicting production incidents. Be concise and specific.

        %s

        RESPOND IN THIS EXACT JSON FORMAT (keep responses SHORT):
        {
            "score": <0-100>,
            "summary": "<1-2 sentences>",
            "findings": [{"severity": "<critical|warning|suggestion>", "line": <number or null>, "issue": "<title>", "explanation": "<brief>", "suggestion": "<fix>"}],
            "improvedCode": "<fixed code>",
            "predictedIncidents": [{"id": "<id>", "title": "<name>", "severity": "<P0|P1|P2|P3>", "scenario": "<when>", "whatHappens": "<brief>", "rootCause": "<cause>", "affectedLine": <line>, "timeToOccur": "<Immediately|Within hours|Within days|Within weeks|Within months>", "probabilityPercent": <0-100>, "businessImpact": "<impact>", "costEstimate": {"minDollars": <n>, "maxDollars": <n>, "breakdown": "<brief>"}, "preventionCode": "<fix>"}],
            "shipItScore": {"verdict": "<SHIP IT|MAYBE|NOPE|ARE YOU SERIOUS?>", "confidence": <0-100>, "reasoning": "<brief>", "mustFixBefore": ["<item>"], "niceToHave": ["<item>"], "tldr": "<one line>", "riskBreakdown": {"securityRisk": <0-100>, "stabilityRisk": <0-100>, "performanceRisk": <0-100>, "maintainabilityRisk": <0-100>, "dataLossRisk": <0-100>}},
            "costAnalysis": {"totalEstimatedCost": <$>, "engineeringHoursToFix": <hrs>, "potentialRevenueLoss": <$>, "technicalDebtCost": <$>, "recommendation": "<brief>"},
            "achievements": [{"id": "<id>", "name": "<name>", "icon": "<emoji>", "description": "<desc>", "unlocked": <bool>, "unlockedReason": "<why>"}],
            "famousBugMatches": [{"famousBugId": "<id>", "bugName": "<name>", "company": "<co>", "year": "<yr>", "icon": "<emoji>", "similarityPercent": <0-100>, "matchReason": "<why>", "financialImpact": "<$>", "yourCodePattern": "<snippet>", "historyPattern": "<what happened>", "lesson": "<lesson>"}],
            "preMortem": {"incidentTitle": "<name>", "severity": "<P0|P1|P2>", "date": "<future date>", "duration": "<time>", "executiveSummary": "<brief>", "rootCauses": ["<cause>"], "impactAssessment": "<impact>", "whoGetsBlamed": "<who>", "actionItems": ["<action>"]},
            "onCallForecast": {"painIndex": <0-100>, "overallVerdict": "<Peaceful|Rough|Nightmare|Career-Ending>", "predictedPages": <n>, "sleepInterruptions": <n>, "weekendRuined": <n>, "survivalTips": ["<tip>"], "coffeeCupsNeeded": <n>, "recommendedCopingMechanism": "<tip>"},
            "codeKarma": {"karmaScore": <-100 to +100>, "karmaVerdict": "<verdict>", "debtCreated": {"totalHours": <hrs>, "worstOffense": "<offense>"}, "reincarnationAs": "<what>", "futureYouMessage": "<msg>"}
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
        request.put("max_tokens", 4096);
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

        // Parse pre-mortem (simplified)
        PreMortem preMortem = null;
        if (node.has("preMortem")) {
            JsonNode pm = node.path("preMortem");
            preMortem = PreMortem.builder()
                    .incidentTitle(pm.path("incidentTitle").asText())
                    .severity(pm.path("severity").asText())
                    .date(pm.path("date").asText())
                    .duration(pm.path("duration").asText())
                    .executiveSummary(pm.path("executiveSummary").asText())
                    .rootCauses(parseStringList(pm.path("rootCauses")))
                    .impactAssessment(pm.path("impactAssessment").asText())
                    .whoGetsBlamed(pm.path("whoGetsBlamed").asText())
                    .actionItems(parseStringList(pm.path("actionItems")))
                    .build();
        }

        // Parse on-call forecast (simplified)
        OnCallForecast onCallForecast = null;
        if (node.has("onCallForecast")) {
            JsonNode ocf = node.path("onCallForecast");
            onCallForecast = OnCallForecast.builder()
                    .painIndex(ocf.path("painIndex").asInt())
                    .overallVerdict(ocf.path("overallVerdict").asText())
                    .predictedPages(ocf.path("predictedPages").asInt())
                    .sleepInterruptions(ocf.path("sleepInterruptions").asInt())
                    .weekendRuined(ocf.path("weekendRuined").asInt())
                    .survivalTips(parseStringList(ocf.path("survivalTips")))
                    .coffeeCupsNeeded(ocf.path("coffeeCupsNeeded").asInt())
                    .recommendedCopingMechanism(ocf.path("recommendedCopingMechanism").asText())
                    .build();
        }

        // Parse code karma (simplified)
        CodeKarma codeKarma = null;
        if (node.has("codeKarma")) {
            JsonNode ck = node.path("codeKarma");
            JsonNode dc = ck.path("debtCreated");

            codeKarma = CodeKarma.builder()
                    .karmaScore(ck.path("karmaScore").asInt())
                    .karmaVerdict(ck.path("karmaVerdict").asText())
                    .debtCreated(CodeKarma.TechDebtCreated.builder()
                            .totalHours(dc.path("totalHours").asInt())
                            .worstOffense(dc.path("worstOffense").asText())
                            .build())
                    .reincarnationAs(ck.path("reincarnationAs").asText())
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
