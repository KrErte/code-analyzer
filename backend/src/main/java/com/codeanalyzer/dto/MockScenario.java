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
public class MockScenario {
    private String id;
    private String name;
    private String icon;
    private String category; // "security", "performance", "data-loss", "career-ending"
    private String difficulty; // "Intern", "Junior", "Senior", "Principal", "CTO"
    private String description;
    private String language;
    private String code;
    private List<String> hints;
    private String realWorldExample;
    private String challenge; // What to look for

    public static List<MockScenario> getAll() {
        return List.of(
            // Security nightmares
            MockScenario.builder()
                .id("sql-injection-classic")
                .name("The SQL Injection Time Bomb")
                .icon("💉")
                .category("security")
                .difficulty("Intern")
                .description("Classic SQL injection hiding in plain sight")
                .language("java")
                .code("""
                    @RestController
                    public class UserController {
                        @Autowired
                        private JdbcTemplate jdbc;

                        @GetMapping("/user/search")
                        public List<User> searchUsers(@RequestParam String name) {
                            String query = "SELECT * FROM users WHERE name LIKE '%" + name + "%'";
                            return jdbc.query(query, new UserRowMapper());
                        }

                        @PostMapping("/user/login")
                        public String login(@RequestBody LoginRequest req) {
                            String query = "SELECT * FROM users WHERE email='" + req.getEmail() +
                                          "' AND password='" + req.getPassword() + "'";
                            User user = jdbc.queryForObject(query, new UserRowMapper());
                            return generateToken(user);
                        }
                    }
                    """)
                .hints(List.of("What if name contains a quote?", "Bobby Tables says hi"))
                .realWorldExample("Equifax breach 2017 - 147 million records exposed")
                .challenge("Find all SQL injection points and explain the attack vector")
                .build(),

            MockScenario.builder()
                .id("insecure-deserialization")
                .name("The Deserialization Doom")
                .icon("📦")
                .category("security")
                .difficulty("Senior")
                .description("Insecure deserialization leading to RCE")
                .language("java")
                .code("""
                    @RestController
                    public class ImportController {
                        @PostMapping("/import/config")
                        public String importConfig(@RequestBody byte[] data) {
                            try {
                                ObjectInputStream ois = new ObjectInputStream(
                                    new ByteArrayInputStream(data));
                                Config config = (Config) ois.readObject();
                                applyConfig(config);
                                return "Config imported";
                            } catch (Exception e) {
                                return "Import failed";
                            }
                        }
                    }

                    // Somewhere in dependencies: commons-collections 3.1 😈
                    """)
                .hints(List.of("What objects can be deserialized?", "Google 'ysoserial'"))
                .realWorldExample("Apache Struts RCE 2017 - Used in Equifax breach")
                .challenge("Explain how an attacker could achieve remote code execution")
                .build(),

            // Performance disasters
            MockScenario.builder()
                .id("n-plus-one")
                .name("The N+1 Query Massacre")
                .icon("🐌")
                .category("performance")
                .difficulty("Junior")
                .description("N+1 query pattern that will kill your database")
                .language("java")
                .code("""
                    @Service
                    public class OrderService {
                        @Autowired
                        private OrderRepository orderRepo;
                        @Autowired
                        private CustomerRepository customerRepo;
                        @Autowired
                        private ProductRepository productRepo;

                        public List<OrderDTO> getAllOrdersWithDetails() {
                            List<Order> orders = orderRepo.findAll(); // Query 1

                            return orders.stream().map(order -> {
                                // Query 2, 3, 4... N for each order
                                Customer c = customerRepo.findById(order.getCustomerId()).get();
                                List<Product> products = order.getProductIds().stream()
                                    .map(id -> productRepo.findById(id).get()) // N more queries!
                                    .collect(Collectors.toList());

                                return new OrderDTO(order, c, products);
                            }).collect(Collectors.toList());
                        }
                    }
                    // 100 orders with 5 products each = 1 + 100 + 500 = 601 queries 💀
                    """)
                .hints(List.of("Count the queries", "What's JOIN?"))
                .realWorldExample("Every startup's first scaling crisis")
                .challenge("Calculate total queries for 1000 orders with 10 products each")
                .build(),

            MockScenario.builder()
                .id("memory-leak-classic")
                .name("The Slow Memory Death")
                .icon("💾")
                .category("performance")
                .difficulty("Senior")
                .description("Subtle memory leak that crashes prod after 3 days")
                .language("java")
                .code("""
                    @Service
                    public class CacheService {
                        private static final Map<String, Object> cache = new HashMap<>();
                        private static final List<EventListener> listeners = new ArrayList<>();

                        public void cacheResult(String key, Object value) {
                            cache.put(key, value); // Never expires, never evicted
                        }

                        public void registerListener(EventListener listener) {
                            listeners.add(listener); // Never unregistered
                        }

                        @EventListener
                        public void onUserSession(UserSessionEvent event) {
                            // Cache user data forever
                            cache.put("user:" + event.getUserId(), event.getData());

                            // Register anonymous listener that holds reference
                            registerListener(e -> {
                                System.out.println("User " + event.getUserId());
                            });
                        }
                    }
                    // After 1M users: OutOfMemoryError 💀
                    """)
                .hints(List.of("What happens when users log out?", "Static + growing = boom"))
                .realWorldExample("Netflix's 2012 Christmas outage from memory leaks")
                .challenge("Identify all memory leak sources and propose fixes")
                .build(),

            // Data loss nightmares
            MockScenario.builder()
                .id("race-condition-money")
                .name("The Double-Spend Race")
                .icon("🏎️")
                .category("data-loss")
                .difficulty("Senior")
                .description("Race condition that loses money")
                .language("java")
                .code("""
                    @Service
                    public class WalletService {
                        @Autowired
                        private WalletRepository walletRepo;

                        public void transfer(Long fromId, Long toId, BigDecimal amount) {
                            Wallet from = walletRepo.findById(fromId).get();
                            Wallet to = walletRepo.findById(toId).get();

                            if (from.getBalance().compareTo(amount) >= 0) {
                                // Race condition window: another thread reads same balance
                                from.setBalance(from.getBalance().subtract(amount));
                                to.setBalance(to.getBalance().add(amount));

                                walletRepo.save(from);
                                walletRepo.save(to);
                            }
                        }
                    }

                    // Two simultaneous $100 transfers from account with $100:
                    // Both see balance=$100, both succeed, account now at -$100 💸
                    """)
                .hints(List.of("What if two requests hit simultaneously?", "SELECT FOR UPDATE"))
                .realWorldExample("Multiple crypto exchange hacks from race conditions")
                .challenge("Explain the race condition and show how to exploit it")
                .build(),

            MockScenario.builder()
                .id("cascade-delete")
                .name("The Cascade Catastrophe")
                .icon("🌊")
                .category("data-loss")
                .difficulty("Junior")
                .description("One delete that wipes your entire database")
                .language("java")
                .code("""
                    @Entity
                    public class Organization {
                        @Id
                        private Long id;

                        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
                        private List<Department> departments;
                    }

                    @Entity
                    public class Department {
                        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
                        private List<Employee> employees;
                    }

                    @Entity
                    public class Employee {
                        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
                        private List<Document> documents;

                        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
                        private List<Transaction> transactions;
                    }

                    // Admin: "Let me just clean up this test org..."
                    orgRepo.deleteById(1L);
                    // Goodbye: 50 departments, 10,000 employees, 1M documents, 50M transactions
                    """)
                .hints(List.of("Follow the cascades", "What's soft delete?"))
                .realWorldExample("GitLab's 2017 database deletion incident")
                .challenge("Trace the full cascade path and count affected records")
                .build(),

            // Career-ending bugs
            MockScenario.builder()
                .id("infinite-loop-billing")
                .name("The Billing Loop of Doom")
                .icon("💳")
                .category("career-ending")
                .difficulty("Principal")
                .description("Bug that charges customers infinitely")
                .language("java")
                .code("""
                    @Service
                    public class BillingService {
                        @Autowired
                        private PaymentGateway paymentGateway;

                        @Scheduled(fixedRate = 60000)
                        public void processRetryQueue() {
                            List<FailedPayment> failures = getFailedPayments();

                            for (FailedPayment fp : failures) {
                                try {
                                    paymentGateway.charge(fp.getCustomerId(), fp.getAmount());
                                    // BUG: Only removes from queue if no exception
                                    markAsProcessed(fp);
                                } catch (PaymentDeclinedException e) {
                                    // Card declined = stays in queue forever
                                    // Next minute: try again
                                    // Forever: customer gets 1440 failed charge attempts per day
                                    log.warn("Payment declined, will retry");
                                }
                            }
                        }
                    }
                    // Customer's bank: "Why are there 50,000 charge attempts?"
                    // Legal team: *starts typing*
                    """)
                .hints(List.of("What's the exit condition?", "Max retry count?"))
                .realWorldExample("Multiple SaaS companies have faced lawsuits for infinite retries")
                .challenge("Find the bug and calculate charges per day for 1000 declined customers")
                .build(),

            MockScenario.builder()
                .id("email-blast")
                .name("The Accidental Email Apocalypse")
                .icon("📧")
                .category("career-ending")
                .difficulty("Junior")
                .description("Bug that emails all customers simultaneously")
                .language("java")
                .code("""
                    @Service
                    public class NotificationService {
                        @Autowired
                        private EmailService emailService;
                        @Autowired
                        private CustomerRepository customerRepo;

                        public void sendPromotion(String customerId, String promoCode) {
                            Customer customer = customerRepo.findById(customerId)
                                .orElse(null);

                            if (customer == null) {
                                // "Let's default to all customers for testing"
                                // TODO: Remove before production
                                customerRepo.findAll().forEach(c ->
                                    emailService.send(c.getEmail(), promoCode));
                            } else {
                                emailService.send(customer.getEmail(), promoCode);
                            }
                        }
                    }

                    // Dev: passes null customerId in test
                    // Result: 5 million emails sent in 2 minutes
                    // Email provider: account suspended
                    // Customers: "Why am I getting 47 copies of this?"
                    """)
                .hints(List.of("What happens with invalid input?", "TODO comments in prod"))
                .realWorldExample("HPE accidentally emailed 33,000 employees they were fired")
                .challenge("Identify the bug and calculate total emails if called 50 times with null")
                .build()
        );
    }
}
