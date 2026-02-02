package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Tech Debt as a tradeable stock
 * Watch your technical debt go up and down like the stock market
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechDebtStock {
    private String ticker; // "DEBT", "SPAGHETTI", "LEGACY"
    private String companyName; // "Your Codebase Inc."

    private StockPrice currentPrice;
    private StockPrice dayChange;
    private StockPrice weekChange;
    private StockPrice monthChange;
    private StockPrice ytdChange;

    private List<PriceHistory> priceHistory;
    private StockMetrics metrics;

    private String analystRating; // "STRONG SELL", "HOLD YOUR NOSE"
    private List<AnalystNote> analystNotes;

    private String tradingAdvice; // "Insider tip: Refactor before Q4"
    private MarketSentiment marketSentiment;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockPrice {
        private double price; // In "debt hours"
        private double percentChange;
        private String direction; // "📈" or "📉"
        private String emoji;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceHistory {
        private String date;
        private double open;
        private double high;
        private double low;
        private double close;
        private String event; // "Major deploy", "Framework upgrade"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockMetrics {
        private double peRatio; // Pain-to-Engineering ratio
        private double marketCap; // Total debt value
        private double dividendYield; // Bugs generated per day
        private double volatility; // How unstable
        private String sector; // "Legacy Systems", "Startup Chaos"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalystNote {
        private String analyst; // "Goldman Sucks Engineering"
        private String rating; // "AVOID AT ALL COSTS"
        private String note;
        private String priceTarget; // "Target: $0 after refactor"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketSentiment {
        private int bullish; // % who think it's fine
        private int bearish; // % who want to rewrite
        private int panic; // % who have updated their resume
        private String overallMood; // "Extreme Fear"
    }
}
