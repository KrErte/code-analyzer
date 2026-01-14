package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamousBug {
    private String id;
    private String name;
    private String company;
    private String year;
    private String icon;
    private String description;
    private String whatHappened;
    private String rootCause;
    private String financialImpact;
    private String codePattern;
    private String lesson;
    private List<String> tags;
    private String sampleCode;

    public static List<FamousBug> getAll() {
        return List.of(
            FamousBug.builder()
                .id("knight-capital")
                .name("Knight Capital Apocalypse")
                .company("Knight Capital Group")
                .year("2012")
                .icon("💀")
                .description("$440 million lost in 45 minutes due to a deployment bug")
                .whatHappened("Old dead code was accidentally reactivated during deployment, causing the system to execute millions of erroneous trades")
                .rootCause("Feature flag reuse - they reused an old flag that triggered dormant code")
                .financialImpact("$440,000,000 - Bankrupted the company")
                .codePattern("DEAD_CODE_REACTIVATION")
                .lesson("Delete dead code. Feature flags are not comments.")
                .tags(List.of("deployment", "feature-flags", "dead-code", "finance"))
                .sampleCode("""
                    // Knight Capital style disaster
                    public class TradingEngine {
                        // DEPRECATED: Old algo, do not use
                        // TODO: Remove this eventually
                        private boolean USE_OLD_ALGO = false; // Reused flag!

                        public void executeTrade(Order order) {
                            if (USE_OLD_ALGO) {
                                // This code hasn't run in 8 years...
                                executeAggressiveMarketMaker(order); // 💀
                            }
                        }

                        private void executeAggressiveMarketMaker(Order o) {
                            while (true) { // Buy everything forever
                                market.buy(o.getSymbol(), Integer.MAX_VALUE);
                            }
                        }
                    }
                    """)
                .build(),

            FamousBug.builder()
                .id("cloudflare-regex")
                .name("Cloudflare Regex Catastrophe")
                .company("Cloudflare")
                .year("2019")
                .icon("🌩️")
                .description("Global outage from a single regex that consumed all CPU")
                .whatHappened("A regex with catastrophic backtracking brought down Cloudflare's entire network for 27 minutes")
                .rootCause("Regex: (?:(?:\\\")+)+  caused exponential backtracking")
                .financialImpact("Millions in SLA credits, massive reputation damage")
                .codePattern("REGEX_DOS")
                .lesson("Test regexes for backtracking. Use regex timeouts.")
                .tags(List.of("regex", "performance", "dos", "global-outage"))
                .sampleCode("""
                    // Cloudflare style regex bomb
                    public class WAFRule {
                        // This regex will destroy your CPU
                        private static final Pattern EVIL_PATTERN =
                            Pattern.compile("(?:(?:\\\\\\")+)+");

                        public boolean checkRequest(String input) {
                            // On certain inputs, this never returns
                            return EVIL_PATTERN.matcher(input).matches();
                        }
                    }

                    // Input that kills it: \\"\\"\\"\\"\\"\\"\\"\\"\\"\\"\\"\\"\\"x
                    """)
                .build(),

            FamousBug.builder()
                .id("github-leap-second")
                .name("GitHub Leap Second Meltdown")
                .company("GitHub")
                .year("2012")
                .icon("⏰")
                .description("Leap second caused MySQL servers to spike to 100% CPU")
                .whatHappened("When the leap second hit, a Linux kernel bug caused futex to spin, killing MySQL")
                .rootCause("Kernel bug + no handling for time going backwards")
                .financialImpact("Hours of downtime, mass developer frustration")
                .codePattern("TIME_ASSUMPTION")
                .lesson("Time is not monotonic. Handle time going backwards.")
                .tags(List.of("time", "kernel", "database", "leap-second"))
                .sampleCode("""
                    // Time assumption bug
                    public class CacheManager {
                        private long lastCleanup = System.currentTimeMillis();

                        public void maybeCleanup() {
                            long now = System.currentTimeMillis();
                            // BUG: What if now < lastCleanup? (leap second, NTP adjustment)
                            if (now - lastCleanup > 60000) {
                                cleanup();
                                lastCleanup = now;
                            }
                            // With leap second: now - lastCleanup = -1000
                            // Result: cleanup() NEVER runs again
                        }
                    }
                    """)
                .build(),

            FamousBug.builder()
                .id("aws-s3-typo")
                .name("AWS S3 Typo Apocalypse")
                .company("Amazon Web Services")
                .year("2017")
                .icon("☁️")
                .description("A typo took down half the internet for 4 hours")
                .whatHappened("An engineer mistyped a command, removing more S3 servers than intended, cascading to break everything")
                .rootCause("No confirmation for destructive operations, no rate limiting on removals")
                .financialImpact("$150+ million across affected companies")
                .codePattern("DANGEROUS_DEFAULTS")
                .lesson("Destructive operations need confirmation, rate limits, and blast radius controls")
                .tags(List.of("operations", "destructive", "confirmation", "blast-radius"))
                .sampleCode("""
                    // AWS S3 typo style danger
                    public class ServerManager {
                        public void removeServers(String query) {
                            // No confirmation, no limit, no safety
                            List<Server> toRemove = db.query(query);

                            // Typo in query = goodbye production
                            for (Server s : toRemove) {
                                s.terminate(); // Hope you meant to do this!
                            }
                            // Intended: remove 3 servers
                            // Actual: removed 30,000 servers
                        }
                    }
                    """)
                .build(),

            FamousBug.builder()
                .id("heathrow-t5")
                .name("Heathrow T5 Baggage Catastrophe")
                .company("British Airways")
                .year("2008")
                .icon("✈️")
                .description("New terminal's baggage system lost 42,000 bags")
                .whatHappened("Untested edge cases in baggage routing caused complete system failure on launch day")
                .rootCause("Insufficient load testing, didn't test with real-world chaos")
                .financialImpact("$32 million direct losses, massive PR disaster")
                .codePattern("UNTESTED_EDGE_CASES")
                .lesson("Test with production-like chaos, not just happy paths")
                .tags(List.of("testing", "edge-cases", "launch", "load-testing"))
                .sampleCode("""
                    // Heathrow T5 style undertesting
                    public class BaggageRouter {
                        public Belt routeBag(Bag bag) {
                            Belt belt = findOptimalBelt(bag);

                            // Works great in testing!
                            // Never tested: What if belt is full?
                            // Never tested: What if bag has no tag?
                            // Never tested: What if 10,000 bags arrive at once?
                            // Never tested: What if belt breaks mid-route?

                            return belt; // null = bag lost forever
                        }
                    }
                    """)
                .build(),

            FamousBug.builder()
                .id("therac-25")
                .name("Therac-25 Race Condition")
                .company("AECL")
                .year("1985-1987")
                .icon("☢️")
                .description("Software bug in radiation machine killed patients")
                .whatHappened("Race condition allowed massive radiation overdoses, killing 3 and injuring 3")
                .rootCause("Race condition between UI and hardware control, no hardware interlocks")
                .financialImpact("Incalculable - human lives lost")
                .codePattern("RACE_CONDITION")
                .lesson("Critical systems need hardware interlocks, not just software checks")
                .tags(List.of("race-condition", "safety-critical", "concurrency", "medical"))
                .sampleCode("""
                    // Therac-25 style race condition
                    public class RadiationController {
                        private volatile boolean safetyCheckPassed = false;
                        private volatile int radiationLevel = 0;

                        // Thread 1: UI sets level
                        public void setLevel(int level) {
                            radiationLevel = level;
                            // BUG: safetyCheckPassed still true from last run!
                        }

                        // Thread 2: Fires radiation
                        public void fire() {
                            if (safetyCheckPassed) {
                                // Race: level changed AFTER check passed
                                emit(radiationLevel); // 💀 Wrong level!
                            }
                        }
                    }
                    """)
                .build(),

            FamousBug.builder()
                .id("null-island")
                .name("Null Island Phenomenon")
                .company("Various")
                .year("Ongoing")
                .icon("🏝️")
                .description("0,0 coordinates become a dumping ground for bad data")
                .whatHappened("When GPS/location parsing fails, defaulting to 0,0 creates a phantom island of errors")
                .rootCause("Using 0,0 as default instead of null/error handling")
                .financialImpact("Data quality disasters, wrong deliveries, analytics pollution")
                .codePattern("ZERO_DEFAULT")
                .lesson("Never use valid values as error defaults. Fail explicitly.")
                .tags(List.of("defaults", "error-handling", "data-quality", "geolocation"))
                .sampleCode("""
                    // Null Island style default disaster
                    public class LocationService {
                        public Coordinates parseLocation(String input) {
                            try {
                                return Coordinates.parse(input);
                            } catch (Exception e) {
                                // "Safe" default that creates Null Island
                                return new Coordinates(0, 0); // 🏝️
                            }
                        }

                        // Now you have:
                        // - 10,000 pizzas delivered to the ocean
                        // - Analytics showing huge engagement at 0,0
                        // - A very confused fishing boat
                    }
                    """)
                .build(),

            FamousBug.builder()
                .id("mars-climate")
                .name("Mars Climate Orbiter")
                .company("NASA / Lockheed Martin")
                .year("1999")
                .icon("🚀")
                .description("$327 million spacecraft lost due to unit mismatch")
                .whatHappened("One team used metric, another used imperial, spacecraft crashed into Mars")
                .rootCause("No unit type safety, implicit conversions assumed")
                .financialImpact("$327,600,000 spacecraft destroyed")
                .codePattern("UNIT_MISMATCH")
                .lesson("Use type-safe units. Never pass raw numbers for physical quantities.")
                .tags(List.of("units", "type-safety", "integration", "space"))
                .sampleCode("""
                    // Mars Climate Orbiter style unit disaster
                    public class ThrustCalculator {
                        // Which unit is this? Who knows!
                        public double calculateThrust(double force, double mass) {
                            return force / mass; // Newtons? Pounds? 🤷
                        }
                    }

                    // Team A:
                    double thrust = calc.calculateThrust(4.45, 1.0); // Newtons

                    // Team B thinks it's pounds:
                    navigator.adjustCourse(thrust); // 💥 Off by 4.45x

                    // $327M spacecraft: *crashes into Mars*
                    """)
                .build()
        );
    }
}
