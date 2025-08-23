package org.paysim.paysim.actors;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;

import org.paysim.paysim.PaySim;
import org.paysim.paysim.base.StepActionProfile;
import org.paysim.paysim.base.Transaction;
import org.paysim.paysim.parameters.BalancesClients;
import org.paysim.paysim.parameters.Parameters;
import org.paysim.paysim.utils.RandomCollection;
import sim.util.distribution.Binomial;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class SwissClient extends Client {
    private static final String CLIENT_IDENTIFIER = "SC";
    
    // Unique Person ID for tracking patterns
    private final String personId;
    
    // Swiss-specific demographic attributes
    private String ageGroup; // YOUNG, MIDDLE, SENIOR
    private String incomeLevel; // LOW, MEDIUM, HIGH
    private String location; // URBAN, SUBURBAN, RURAL
    private boolean hasCar;
    private boolean isStudent;
    
    // Enhanced persona system
    private String persona; // URBAN_TRANSPORT, SUBURBAN_CAR, STUDENT, YOUNG_PROF, FAMILY, REMOTE_WORKER
    private boolean hasKids;
    private boolean isRemoteWorker;
    private boolean hasGymMembership;
    private boolean hasCreditCard;
    
    // Seasonal and timing tracking
    private int lastSalaryDay = -1;
    private int last13thSalaryDay = -1;
    private int lastFuelDay = -1;
    private int lastGymDay = -1;
    private int lastChildcareDay = -1;
    private int lastDonationDay = -1;
    
    // Advanced financial & behavioral tracking
    private int lastPaydaySplurgeDay = -1;
    private int lastBNPLPurchaseDay = -1;
    private int lastP2PTransferDay = -1;
    private int lastSavingsTransferDay = -1;
    private int lastTravelcardPurchaseDay = -1;
    private int lastAperoDay = -1;
    private int lastGiftPurchaseDay = -1;
    private int lastPetExpenseDay = -1;
    private int lastSerafePaymentDay = -1;
    private int lastHealthInsuranceDay = -1;
    
    // Life events and state changes
    private boolean hasJobChange = false;
    private boolean hasJobLoss = false;
    private boolean hasMoved = false;
    private boolean hasNewChild = false;
    private boolean hasPet = false;
    private int jobChangeDay = -1;
    private int jobLossDay = -1;
    private int moveDay = -1;
    private int childBirthDay = -1;
    private int petAdoptionDay = -1;
    
    // Financial state tracking
    private double monthlySalary;
    private double savingsRate;
    private boolean isInPaydaySplurgeMode = false;
    private boolean isInBrokeMode = false;
    private int currentBNPLInstallment = 0;
    private double totalBNPLAmount = 0.0;
    private double monthlyBNPLPayment = 0.0;
    
    // Swiss-specific attributes
    private boolean hasHalbtax = false;
    private boolean hasGA = false;
    private String birthdayMonth;
    private String healthInsuranceProvider;
    private String primaryGroceryStore;
    private String favoriteCoffeeShop;
    private String preferredBar;
    
    // Pattern tracking for realistic behavior
    private int lastGroceryDay = -1;
    private int lastHolidayDay = -1;
    private int lastServiceBillingDay = -1;
    private int lastTransportBillingDay = -1;
    
    // Cognitive bias and psychological pattern fields
    private Map<String, Double> categoryAnchors = new HashMap<>(); // First major purchase in each category
    private Map<String, Double> sourceBasedSpending = new HashMap<>(); // Different spending patterns by money source
    private boolean isFollowingHerdBehavior = false; // Following trendy items
    private double subscriptionPriceSensitivity = 1.0; // Loss aversion for price increases
    private Set<String> forgottenSubscriptions = new HashSet<>(); // Active but unused subscriptions
    private boolean isInSavingMode = false; // Goal-oriented saving phase
    private int savingModeDuration = 0; // How long to save
    private double savingsGoal = 0.0; // Target amount to save
    private String savingsGoalType = ""; // What they're saving for
    
    // Inter-personal transaction fields
    private boolean hasSharedAccount = false; // Family joint account
    private String partnerPersonId = ""; // Partner's person ID for expense splitting
    private List<ExpenseSplit> pendingExpenseSplits = new ArrayList<>(); // Pending reimbursements
    
    // Financial product lifecycle fields
    private boolean hasNewCreditCard = false; // Credit card introductory offer period
    private int creditCardIntroPeriod = 0; // Days remaining in intro period
    private boolean hasActiveLoan = false; // Active loan repayment
    private int loanRemainingMonths = 0; // Months remaining on loan
    private double monthlyLoanPayment = 0.0; // Monthly loan payment amount
    
    // Technical transaction fields
    private Map<String, Transaction> pendingAuthorizations = new HashMap<>(); // Authorization phase transactions
    private boolean useCardNotPresent = false; // Card-not-present vs card-present patterns
    private String homeLocation = ""; // Home location for geospatial clustering
    private String workLocation = ""; // Work location for geospatial clustering
    private boolean isFrequentTraveler = false; // Multi-currency transactions
    private String preferredForeignCurrency = "EUR"; // Preferred foreign currency (EUR/USD)
    
    // Swiss-specific advanced fields
    private boolean hasPillar3a = false; // Pillar 3a pension account
    private boolean usesEBill = false; // Uses eBill system
    private boolean isCrossBorderShopper = false; // Shops in neighboring countries
    private String preferredCrossBorderCountry = "Germany"; // Germany, France, or Italy
    private boolean hasSBBEasyRide = false; // SBB EasyRide user
    private boolean hasLunchCheck = false; // Employer lunch check benefit
    private String region = "German"; // Swiss region (German, French, Italian)
    private String preferredLanguage = "German"; // Preferred language for transactions
    
    // Inner class for expense splitting
    private static class ExpenseSplit {
        private String description;
        private double amount;
        private String partnerPersonId;
        private int step;
        
        public ExpenseSplit(String description, double amount, String partnerPersonId, int step) {
            this.description = description;
            this.amount = amount;
            this.partnerPersonId = partnerPersonId;
            this.step = step;
        }
        
        // Getters
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public String getPartnerPersonId() { return partnerPersonId; }
        public int getStep() { return step; }
    }
    
    // Personal preferences (deterministic based on Person ID)
    private String preferredGroceryStore;
    private String preferredTransportService;
    private String preferredStreamingService;
    private String preferredBank;
    private String preferredRestaurant;

    public SwissClient(PaySim paySim) {
        super(paySim);
        
        // Generate unique Person ID
        this.personId = generatePersonId();
        
        // Initialize Swiss-specific attributes
        initializeSwissAttributes(paySim.random);
        
        // Initialize personal preferences based on Person ID
        initializePersonalPreferences();
        
        // Override the name to be more realistic
        setRealisticSwissName(paySim.random);
    }
    
    /**
     * Generate unique Person ID for this client
     */
    private String generatePersonId() {
        // Use a combination of timestamp and random hash for uniqueness
        long timestamp = System.currentTimeMillis();
        int hash = Math.abs(hashCode());
        return "P" + String.format("%08d", hash % 100000000) + "_" + (timestamp % 10000);
    }
    
    /**
     * Initialize personal preferences based on Person ID hash
     */
    private void initializePersonalPreferences() {
        int hash = Math.abs(personId.hashCode());
        
        // Grocery store preference
        String[] groceryStores = {"Migros", "Coop", "Aldi", "Lidl", "Denner", "Volg"};
        preferredGroceryStore = groceryStores[hash % groceryStores.length];
        
        // Transport service preference
        String[] transportServices = {"SBB", "PostAuto", "VBZ", "TPG", "BVB", "Tram"};
        preferredTransportService = transportServices[hash % transportServices.length];
        
        // Streaming service preference
        String[] streamingServices = {"Netflix", "Spotify", "Disney+", "Amazon Prime", "Apple Music", "YouTube Premium"};
        preferredStreamingService = streamingServices[hash % streamingServices.length];
        
        // Bank preference
        String[] banks = {"UBS", "Credit Suisse", "PostFinance", "Raiffeisen", "ZKB", "Local Bank"};
        preferredBank = banks[hash % banks.length];
        
        // Restaurant preference
        String[] restaurants = {"McDonald's", "Burger King", "Subway", "Pizza Hut", "KFC", "Starbucks"};
        preferredRestaurant = restaurants[hash % restaurants.length];
    }
    
    /**
     * Set a realistic Swiss name for the client
     */
    private void setRealisticSwissName(MersenneTwisterFast random) {
        String[] swissFirstNames = {
            "Hans", "Peter", "Thomas", "Michael", "Andreas", "Martin", "Christian", "Daniel", "Markus", "Stefan",
            "Anna", "Maria", "Ursula", "Monika", "Sandra", "Claudia", "Sabine", "Petra", "Elisabeth", "Barbara"
        };
        
        String[] swissLastNames = {
            "Müller", "Schmid", "Schneider", "Fischer", "Meyer", "Weber", "Huber", "Wagner", "Steiner", "Berger",
            "Frei", "Roth", "Zimmermann", "Keller", "Brunner", "Widmer", "Kuhn", "Baumann", "Lüthi", "Hofmann"
        };
        
        String firstName = swissFirstNames[random.nextInt(swissFirstNames.length)];
        String lastName = swissLastNames[random.nextInt(swissLastNames.length)];
        
        // Create a realistic Swiss name format
        String realisticName = firstName + " " + lastName;
        
        // Use reflection to set the name field (since it's private in SuperActor)
        try {
            java.lang.reflect.Field nameField = getClass().getSuperclass().getSuperclass().getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(this, realisticName);
        } catch (Exception e) {
            // If reflection fails, keep the original name
        }
    }

    private void initializeSwissAttributes(MersenneTwisterFast random) {
        // Age group distribution
        double ageRand = random.nextDouble();
        if (ageRand < 0.25) {
            ageGroup = "YOUNG"; // 18-35
        } else if (ageRand < 0.70) {
            ageGroup = "MIDDLE"; // 36-65
        } else {
            ageGroup = "SENIOR"; // 65+
        }
        
        // Income level based on age and random factor
        double incomeRand = random.nextDouble();
        if (ageGroup.equals("YOUNG")) {
            if (incomeRand < 0.40) incomeLevel = "LOW";
            else if (incomeRand < 0.80) incomeLevel = "MEDIUM";
            else incomeLevel = "HIGH";
        } else if (ageGroup.equals("MIDDLE")) {
            if (incomeRand < 0.20) incomeLevel = "LOW";
            else if (incomeRand < 0.70) incomeLevel = "MEDIUM";
            else incomeLevel = "HIGH";
        } else {
            if (incomeRand < 0.30) incomeLevel = "LOW";
            else if (incomeRand < 0.80) incomeLevel = "MEDIUM";
            else incomeLevel = "HIGH";
        }
        
        // Location preference
        double locationRand = random.nextDouble();
        if (locationRand < 0.60) location = "URBAN";
        else if (locationRand < 0.85) location = "SUBURBAN";
        else location = "RURAL";
        
        // Car ownership (higher in rural/suburban areas)
        double carRand = random.nextDouble();
        if (location.equals("RURAL")) {
            hasCar = carRand < 0.90;
        } else if (location.equals("SUBURBAN")) {
            hasCar = carRand < 0.75;
        } else {
            hasCar = carRand < 0.40;
        }
        
        // Student status (mainly young people)
        isStudent = ageGroup.equals("YOUNG") && random.nextDouble() < 0.30;
        
        // Initialize persona based on demographics
        initializePersona(random);
    }
    
    /**
     * Initialize persona based on demographics and preferences
     */
    private void initializePersona(MersenneTwisterFast random) {
        // Determine if person has kids (based on age and income)
        if (ageGroup.equals("MIDDLE") && incomeLevel.equals("MEDIUM") || incomeLevel.equals("HIGH")) {
            hasKids = random.nextDouble() < 0.60; // 60% of middle-aged with good income have kids
        } else if (ageGroup.equals("YOUNG")) {
            hasKids = random.nextDouble() < 0.10; // 10% of young people have kids
        } else {
            hasKids = random.nextDouble() < 0.40; // 40% of seniors have kids
        }
        
        // Remote worker status (mainly young and middle-aged)
        if (ageGroup.equals("YOUNG") || ageGroup.equals("MIDDLE")) {
            isRemoteWorker = random.nextDouble() < 0.35; // 35% work remotely
        } else {
            isRemoteWorker = false; // Seniors don't work remotely
        }
        
        // Gym membership (higher for young and middle-aged)
        if (ageGroup.equals("YOUNG")) {
            hasGymMembership = random.nextDouble() < 0.70; // 70% of young people have gym
        } else if (ageGroup.equals("MIDDLE")) {
            hasGymMembership = random.nextDouble() < 0.50; // 50% of middle-aged have gym
        } else {
            hasGymMembership = random.nextDouble() < 0.20; // 20% of seniors have gym
        }
        
        // Credit card ownership (based on income and age)
        if (incomeLevel.equals("HIGH")) {
            hasCreditCard = random.nextDouble() < 0.95; // 95% of high income have credit card
        } else if (incomeLevel.equals("MEDIUM")) {
            hasCreditCard = random.nextDouble() < 0.80; // 80% of medium income have credit card
        } else {
            hasCreditCard = random.nextDouble() < 0.40; // 40% of low income have credit card
        }
        
        // Determine persona based on demographics
        if (isStudent) {
            persona = "STUDENT";
        } else if (ageGroup.equals("YOUNG") && !isStudent) {
            persona = "YOUNG_PROF";
        } else if (ageGroup.equals("MIDDLE")) {
            if (hasKids) {
                persona = "FAMILY";
            } else {
                persona = "YOUNG_PROF";
            }
        } else { // SENIOR
            persona = "SENIOR";
        }
        
        // Override based on location and transport preferences
        if (location.equals("URBAN") && !hasCar) {
            persona = "URBAN_TRANSPORT";
        } else if (location.equals("SUBURBAN") && hasCar) {
            persona = "SUBURBAN_CAR";
        }
        
        // Remote worker override
        if (isRemoteWorker) {
            persona = "REMOTE_WORKER";
        }
        
        // Initialize Swiss-specific attributes
        initializeSwissSpecificAttributes(random);
        
        // Initialize financial state
        initializeFinancialState(random);
        
        // Initialize life events
        initializeLifeEvents(random);
        
        // Initialize advanced behavioral and technical features
        initializeAdvancedFeatures(random);
    }
    
    /**
     * Initialize Swiss-specific attributes
     */
    private void initializeSwissSpecificAttributes(MersenneTwisterFast random) {
        // Travel cards (Halbtax/GA)
        if (persona.equals("URBAN_TRANSPORT") || persona.equals("STUDENT")) {
            hasHalbtax = random.nextDouble() < 0.70; // 70% of urban transport users have Halbtax
            hasGA = random.nextDouble() < 0.20; // 20% have GA (more expensive)
        } else if (persona.equals("SUBURBAN_CAR")) {
            hasHalbtax = random.nextDouble() < 0.30; // 30% of car owners have Halbtax
            hasGA = random.nextDouble() < 0.10; // 10% have GA
        }
        
        // Birthday month (for gifting patterns)
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        birthdayMonth = months[random.nextInt(months.length)];
        
        // Health insurance provider
        String[] providers = {"Swica", "Helsana", "CSS", "Atupri", "KPT", "Concordia", "Sanitas"};
        healthInsuranceProvider = providers[random.nextInt(providers.length)];
        
        // Grocery store preferences (can evolve over time)
        String[] groceryStores = {"Migros", "Coop", "Aldi", "Lidl", "Denner", "Volg", "Manor"};
        primaryGroceryStore = groceryStores[random.nextInt(groceryStores.length)];
        
        // Coffee shop preferences
        String[] coffeeShops = {"Starbucks", "Café de Paris", "Café Schober", "Café Odeon", "Café Central", "Local Coffee Shop"};
        favoriteCoffeeShop = coffeeShops[random.nextInt(coffeeShops.length)];
        
        // Bar preferences (for Apéro culture)
        String[] bars = {"Bar 63", "Café Bar Odeon", "Bar Au Lac", "Café Bar", "Local Pub", "Wine Bar"};
        preferredBar = bars[random.nextInt(bars.length)];
        
        // Pet ownership (20% of people have pets)
        hasPet = random.nextDouble() < 0.20;
        if (hasPet) {
            petAdoptionDay = random.nextInt(365); // Random day in the year
        }
    }
    
    /**
     * Initialize financial state
     */
    private void initializeFinancialState(MersenneTwisterFast random) {
        // Base monthly salary based on age and income level
        if (incomeLevel.equals("LOW")) {
            monthlySalary = 3000 + random.nextDouble() * 2000; // 3000-5000 CHF
        } else if (incomeLevel.equals("MEDIUM")) {
            monthlySalary = 5000 + random.nextDouble() * 3000; // 5000-8000 CHF
        } else { // HIGH
            monthlySalary = 8000 + random.nextDouble() * 5000; // 8000-13000 CHF
        }
        
        // Adjust for age and persona
        if (ageGroup.equals("YOUNG")) {
            monthlySalary *= 0.8; // Young people earn less
        } else if (ageGroup.equals("SENIOR")) {
            monthlySalary *= 1.2; // Seniors earn more
        }
        
        if (isStudent) {
            monthlySalary *= 0.3; // Students have very low income
        }
        
        // Savings rate (5-25% of income)
        savingsRate = 0.05 + random.nextDouble() * 0.20;
        
        // Set initial balance based on salary
        double initialBalance = monthlySalary * (2 + random.nextDouble() * 3); // 2-5 months salary
        try {
            java.lang.reflect.Field balanceField = getClass().getSuperclass().getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(this, initialBalance);
        } catch (Exception e) {
            // If reflection fails, keep the original balance
        }
    }
    
    /**
     * Initialize advanced behavioral and technical features
     */
    private void initializeAdvancedFeatures(MersenneTwisterFast random) {
        // Cognitive bias initialization
        subscriptionPriceSensitivity = 0.8 + random.nextDouble() * 0.4; // 0.8 to 1.2 (loss aversion)
        isFollowingHerdBehavior = random.nextDouble() < 0.25; // 25% follow trends
        
        // Forgotten subscriptions (5-15% of people have unused subscriptions)
        if (random.nextDouble() < 0.10) {
            String[] subscriptionTypes = {"Gym", "Streaming", "Software", "Magazine", "Music"};
            int numForgotten = 1 + random.nextInt(3); // 1-3 forgotten subscriptions
            for (int i = 0; i < numForgotten; i++) {
                forgottenSubscriptions.add(subscriptionTypes[random.nextInt(subscriptionTypes.length)]);
            }
        }
        
        // Goal-oriented saving (20% of people are in saving mode)
        if (random.nextDouble() < 0.20) {
            isInSavingMode = true;
            savingModeDuration = 3 + random.nextInt(9); // 3-12 months
            String[] goalTypes = {"Vacation", "Car", "House", "Electronics", "Education"};
            savingsGoalType = goalTypes[random.nextInt(goalTypes.length)];
            savingsGoal = 1000 + random.nextDouble() * 9000; // 1000-10000 CHF
        }
        
        // Inter-personal transactions (30% have shared accounts, 20% have partners)
        hasSharedAccount = random.nextDouble() < 0.30;
        if (random.nextDouble() < 0.20) {
            // Generate a partner person ID
            partnerPersonId = "P" + String.format("%08d", random.nextInt(100000000)) + "_" + random.nextInt(10000);
        }
        
        // Financial product lifecycle
        hasNewCreditCard = random.nextDouble() < 0.15; // 15% have new credit cards
        if (hasNewCreditCard) {
            creditCardIntroPeriod = 30 + random.nextInt(90); // 30-120 days intro period
        }
        
        hasActiveLoan = random.nextDouble() < 0.25; // 25% have active loans
        if (hasActiveLoan) {
            loanRemainingMonths = 6 + random.nextInt(42); // 6-48 months remaining
            monthlyLoanPayment = 200 + random.nextDouble() * 800; // 200-1000 CHF monthly
        }
        
        // Technical transaction patterns
        useCardNotPresent = random.nextDouble() < 0.40; // 40% prefer online/CNP transactions
        
        // Geospatial clustering
        String[] locations = {"Zurich", "Geneva", "Basel", "Bern", "Lausanne", "St. Gallen", "Lucerne", "Lugano"};
        homeLocation = locations[random.nextInt(locations.length)];
        workLocation = locations[random.nextInt(locations.length)];
        
        // Multi-currency and travel
        isFrequentTraveler = random.nextDouble() < 0.20; // 20% travel frequently
        if (isFrequentTraveler) {
            preferredForeignCurrency = random.nextDouble() < 0.6 ? "EUR" : "USD";
        }
        
        // Swiss-specific advanced features
        hasPillar3a = random.nextDouble() < 0.60; // 60% have Pillar 3a accounts
        usesEBill = random.nextDouble() < 0.70; // 70% use eBill system
        isCrossBorderShopper = random.nextDouble() < 0.35; // 35% shop cross-border
        
        if (isCrossBorderShopper) {
            String[] countries = {"Germany", "France", "Italy"};
            preferredCrossBorderCountry = countries[random.nextInt(countries.length)];
        }
        
        hasSBBEasyRide = random.nextDouble() < 0.20; // 20% use SBB EasyRide
        hasLunchCheck = random.nextDouble() < 0.20; // 20% have lunch check benefits
        
        // Regional and linguistic variations
        double regionRand = random.nextDouble();
        if (regionRand < 0.70) {
            region = "German";
            preferredLanguage = "German";
        } else if (regionRand < 0.85) {
            region = "French";
            preferredLanguage = "French";
        } else {
            region = "Italian";
            preferredLanguage = "Italian";
        }
    }
    
    /**
     * Initialize life events
     */
    private void initializeLifeEvents(MersenneTwisterFast random) {
        // Job change probability (5% per year)
        if (random.nextDouble() < 0.05) {
            hasJobChange = true;
            jobChangeDay = random.nextInt(365);
        }
        
        // Job loss probability (2% per year, higher for young people)
        double jobLossProbability = ageGroup.equals("YOUNG") ? 0.03 : 0.02;
        if (random.nextDouble() < jobLossProbability) {
            hasJobLoss = true;
            jobLossDay = random.nextInt(365);
        }
        
        // Moving probability (10% per year, higher for young people)
        double moveProbability = ageGroup.equals("YOUNG") ? 0.15 : 0.10;
        if (random.nextDouble() < moveProbability) {
            hasMoved = true;
            moveDay = random.nextInt(365);
        }
        
        // New child probability (3% per year, only for middle-aged)
        if (ageGroup.equals("MIDDLE") && !hasKids) {
            if (random.nextDouble() < 0.03) {
                hasNewChild = true;
                childBirthDay = random.nextInt(365);
            }
        }
    }

    // Getters for Swiss-specific attributes
    public String getAgeGroup() { return ageGroup; }
    public String getIncomeLevel() { return incomeLevel; }
    public String getLocation() { return location; }
    public boolean hasCar() { return hasCar; }
    public boolean isStudent() { return isStudent; }
    
    // Getters for Person ID and preferences
    public String getPersonId() { return personId; }
    public String getPreferredGroceryStore() { return preferredGroceryStore; }
    public String getPreferredTransportService() { return preferredTransportService; }
    public String getPreferredStreamingService() { return preferredStreamingService; }
    public String getPreferredBank() { return preferredBank; }
    public String getPreferredRestaurant() { return preferredRestaurant; }
    
    // Getters for persona attributes
    public String getPersona() { return persona; }
    public boolean hasKids() { return hasKids; }
    public boolean isRemoteWorker() { return isRemoteWorker; }
    public boolean hasGymMembership() { return hasGymMembership; }
    public boolean hasCreditCard() { return hasCreditCard; }
    
    // Getters for Swiss-specific attributes
    public boolean hasHalbtax() { return hasHalbtax; }
    public boolean hasGA() { return hasGA; }
    public String getBirthdayMonth() { return birthdayMonth; }
    public String getHealthInsuranceProvider() { return healthInsuranceProvider; }
    public String getPrimaryGroceryStore() { return primaryGroceryStore; }
    public String getFavoriteCoffeeShop() { return favoriteCoffeeShop; }
    public String getPreferredBar() { return preferredBar; }
    
    // Getters for life events
    public boolean hasJobChange() { return hasJobChange; }
    public boolean hasJobLoss() { return hasJobLoss; }
    public boolean hasMoved() { return hasMoved; }
    public boolean hasNewChild() { return hasNewChild; }
    public boolean hasPet() { return hasPet; }
    
    // Getters for financial state
    public double getMonthlySalary() { return monthlySalary; }
    public double getSavingsRate() { return savingsRate; }
    public boolean isInPaydaySplurgeMode() { return isInPaydaySplurgeMode; }
    public boolean isInBrokeMode() { return isInBrokeMode; }
    
    // Getters for cognitive bias and psychological patterns
    public Map<String, Double> getCategoryAnchors() { return categoryAnchors; }
    public Map<String, Double> getSourceBasedSpending() { return sourceBasedSpending; }
    public boolean isFollowingHerdBehavior() { return isFollowingHerdBehavior; }
    public double getSubscriptionPriceSensitivity() { return subscriptionPriceSensitivity; }
    public Set<String> getForgottenSubscriptions() { return forgottenSubscriptions; }
    public boolean isInSavingMode() { return isInSavingMode; }
    public int getSavingModeDuration() { return savingModeDuration; }
    public double getSavingsGoal() { return savingsGoal; }
    public String getSavingsGoalType() { return savingsGoalType; }
    
    // Getters for inter-personal transactions
    public boolean hasSharedAccount() { return hasSharedAccount; }
    public String getPartnerPersonId() { return partnerPersonId; }
    public List<ExpenseSplit> getPendingExpenseSplits() { return pendingExpenseSplits; }
    
    // Getters for financial product lifecycle
    public boolean hasNewCreditCard() { return hasNewCreditCard; }
    public int getCreditCardIntroPeriod() { return creditCardIntroPeriod; }
    public boolean hasActiveLoan() { return hasActiveLoan; }
    public int getLoanRemainingMonths() { return loanRemainingMonths; }
    public double getMonthlyLoanPayment() { return monthlyLoanPayment; }
    
    // Getters for technical transaction fields
    public Map<String, Transaction> getPendingAuthorizations() { return pendingAuthorizations; }
    public boolean useCardNotPresent() { return useCardNotPresent; }
    public String getHomeLocation() { return homeLocation; }
    public String getWorkLocation() { return workLocation; }
    public boolean isFrequentTraveler() { return isFrequentTraveler; }
    public String getPreferredForeignCurrency() { return preferredForeignCurrency; }
    
    // Getters for Swiss-specific advanced fields
    public boolean hasPillar3a() { return hasPillar3a; }
    public boolean usesEBill() { return usesEBill; }
    public boolean isCrossBorderShopper() { return isCrossBorderShopper; }
    public String getPreferredCrossBorderCountry() { return preferredCrossBorderCountry; }
    public boolean hasSBBEasyRide() { return hasSBBEasyRide; }
    public boolean hasLunchCheck() { return hasLunchCheck; }
    public String getRegion() { return region; }
    public String getPreferredLanguage() { return preferredLanguage; }
    
    /**
     * Get realistic Swiss company name for transaction type
     * Creates recurring patterns for specific clients
     */
    private String getSwissCompanyName(MersenneTwisterFast random, String action) {
        // Use client ID hash to create consistent company preferences
        int clientHash = Math.abs(getName().hashCode());
        
        switch (action) {
            case "INCOME_GENERAL":
                String[] incomeSources = {"UBS Salary", "Credit Suisse Salary", "PostFinance Salary", "Raiffeisen Salary", "ZKB Salary", "Local Company Salary", "Swiss Salary", "International Company Salary", "Startup Salary", "Government Salary"};
                return incomeSources[clientHash % incomeSources.length];
                
            case "HOUSING_GENERAL":
                String[] housing = {"Swisscom", "EWZ", "GEW", "IWB", "AEW", "Local Utility", "Internet Provider", "Phone Provider", "Cable Provider", "Security System"};
                return housing[clientHash % housing.length];
                
            case "GENERAL_EXPENSES_DAILY":
                String[] dailyExpenses = {"Migros", "Coop", "Aldi", "Lidl", "Denner", "Volg", "Manor", "Globus", "Jelmoli", "Local Market", "Farmers Market", "Organic Store"};
                return dailyExpenses[clientHash % dailyExpenses.length];
                
            case "TRAVEL_GENERAL":
                String[] travel = {"Swiss", "EasyJet", "Ryanair", "Booking.com", "Airbnb", "Expedia", "Hotels.com", "TripAdvisor", "Kayak", "Skyscanner", "Local Travel Agency", "Trainline"};
                return travel[clientHash % travel.length];
                
            case "OTHER_GENERAL":
                String[] otherExpenses = {"Local Shop", "Kiosk", "Market", "Vending Machine", "Street Vendor", "Pop-up Store", "Flea Market", "Antique Shop", "Craft Store", "Gift Shop", "Stationery Store", "Hardware Store"};
                return otherExpenses[clientHash % otherExpenses.length];
                
            case "SHOPPING_ELECTRONICS":
                String[] electronicsStores = {"MediaMarkt", "Interdiscount", "Fust", "Brack", "Digitec", "Galaxus", "Apple Store", "Samsung Store", "Saturn", "Expert", "Conrad", "Online Electronics"};
                return electronicsStores[clientHash % electronicsStores.length];
                
            case "SHOPPING_BOOKS":
                String[] bookStores = {"Orell Füssli", "Buchhandlung Stauffacher", "Buchhandlung Jäggi", "Buchhandlung Bider", "Online Bookstore", "University Bookstore", "Thalia", "Buch.ch", "Amazon Books", "Local Library", "Academic Bookstore", "Children's Bookstore"};
                return bookStores[clientHash % bookStores.length];
                
            case "SHOPPING_CLOTHING":
                String[] clothingStores = {"H&M", "Zara", "C&A", "Uniqlo", "Mango", "New Yorker", "Manor Fashion", "Globus Fashion", "Jelmoli Fashion", "Local Boutique", "Designer Store", "Sportswear Store", "Shoe Store", "Jewelry Store"};
                return clothingStores[clientHash % clothingStores.length];
                
            case "HEALTHCARE_GENERAL":
                String[] healthcare = {"CSS", "Swisscare", "Helsana", "Concordia", "AXA", "Sanitas", "Swica", "KPT", "Atupri", "ProVita", "Aquilana", "Local Doctor", "Dentist", "Pharmacy", "Hospital", "Specialist Clinic"};
                return healthcare[clientHash % healthcare.length];
                
            case "TRANSPORTATION_FUEL":
                String[] fuelStations = {"Shell", "BP", "Avia", "Coop Pronto", "Migrol", "Tamoil", "Agrola", "Esso", "Total", "Local Gas Station", "Highway Service", "Car Wash"};
                return fuelStations[clientHash % fuelStations.length];
                
            case "TRANSPORTATION_PUBLIC":
                String[] publicTransport = {"SBB", "PostAuto", "VBZ", "TPG", "BVB", "Tram", "Bus", "Metro", "Cable Car", "Funicular", "Boat", "Bike Share", "Scooter Share"};
                return publicTransport[clientHash % publicTransport.length];
                
            case "FOOD_DINING_LUNCH":
                String[] lunchPlaces = {"McDonald's", "Burger King", "Subway", "Pizza Hut", "KFC", "Starbucks", "Migros Restaurant", "Coop Restaurant", "Local Café", "Food Truck", "Pizza Place", "Sushi Restaurant", "Thai Restaurant", "Italian Restaurant", "Swiss Restaurant", "Fast Food", "Cafeteria", "Bakery", "Deli", "Street Food"};
                return lunchPlaces[clientHash % lunchPlaces.length];
                
            case "TAXES_GENERAL":
                String[] taxAuthorities = {"Federal Tax Office", "Canton Tax Office", "Municipal Tax Office", "VAT Office", "Wealth Tax Office", "Income Tax Office", "Property Tax Office", "Corporate Tax Office", "Import Tax Office", "Export Tax Office"};
                return taxAuthorities[clientHash % taxAuthorities.length];
                
            case "UTILITIES_GENERAL":
                String[] utilities = {"Swisscom", "EWZ", "GEW", "IWB", "AEW", "Local Utility", "Internet Provider", "Phone Provider", "Cable Provider", "Electricity Provider", "Water Provider", "Gas Provider", "Waste Management", "Recycling Service"};
                return utilities[clientHash % utilities.length];
                
            case "ENTERTAINMENT_STREAMING":
                String[] streamingServices = {"Netflix", "Spotify", "Disney+", "Amazon Prime", "Apple Music", "YouTube Premium", "HBO Max", "Hulu", "Crunchyroll", "Twitch", "Tidal", "Deezer", "Pandora", "SoundCloud", "Podcast Platform"};
                return streamingServices[clientHash % streamingServices.length];
                
            case "ENTERTAINMENT_MOBILE":
                String[] mobileServices = {"Swisscom Mobile", "Sunrise Mobile", "Salt Mobile", "Yallo Mobile", "M-Budget Mobile", "Aldi Talk", "Lebara", "Lyca Mobile", "Gomo", "Wingo", "Mobile Virtual Network", "International Roaming"};
                return mobileServices[clientHash % mobileServices.length];
                
            default:
                return "Swiss Company";
        }
    }
    
    @Override
    public void step(SimState state) {
        PaySim paySim = (PaySim) state;
        int step = (int) state.schedule.getSteps();
        MersenneTwisterFast random = paySim.random;
        
        // Implement realistic daily patterns
        executeDailyPatterns(paySim, step, random);
    }
    
    /**
     * Execute realistic daily spending patterns
     */
    private void executeDailyPatterns(PaySim paySim, int step, MersenneTwisterFast random) {
        // 1. INCOME (monthly salary with jitter)
        if (shouldReceiveSalary(step, random)) {
            executeIncome(paySim, step, random);
        }
        
        // 2. 13TH SALARY (November/December for subset)
        if (shouldReceive13thSalary(step, random)) {
            execute13thSalary(paySim, step, random);
        }
        
        // 3. DAILY TRANSPORT (with persona-specific patterns)
        if (shouldUseTransport(step, random)) {
            executeTransport(paySim, step, random);
        }
        
        // 4. GROCERY SHOPPING (1-2 times per week, persona-specific)
        if (shouldShopForGroceries(step, random)) {
            executeGroceryShopping(paySim, step, random);
        }
        
        // 5. LUNCH (persona and weekday dependent)
        if (shouldHaveLunch(step, random)) {
            executeLunch(paySim, step, random);
        }
        
        // 6. MONTHLY SERVICES (with billing jitter)
        if (shouldPayMonthlyServices(step, random)) {
            executeMonthlyServices(paySim, step, random);
        }
        
        // 7. HOUSING & UTILITIES (monthly with jitter)
        if (shouldPayHousing(step, random)) {
            executeHousingBilling(paySim, step, random);
        }
        
        // 8. FUEL (car owners only, weekly)
        if (shouldBuyFuel(step, random)) {
            executeFuelPurchase(paySim, step, random);
        }
        
        // 9. GYM (monthly for members)
        if (shouldPayGym(step, random)) {
            executeGymPayment(paySim, step, random);
        }
        
        // 10. CHILDCARE/SCHOOL (families only)
        if (shouldPayChildcare(step, random)) {
            executeChildcarePayment(paySim, step, random);
        }
        
        // 11. SEASONAL PATTERNS
        executeSeasonalPatterns(paySim, step, random);
        
        // 12. OCCASIONAL PATTERNS
        if (shouldGoShopping(step, random)) {
            executeShopping(paySim, step, random);
        }
        
        if (shouldVisitHealthcare(step, random)) {
            executeHealthcare(paySim, step, random);
        }
        
        if (shouldMakeDonation(step, random)) {
            executeDonation(paySim, step, random);
        }
        
        // 13. CREDIT CARD PAYMENT (monthly for card holders)
        if (shouldPayCreditCard(step, random)) {
            executeCreditCardPayment(paySim, step, random);
        }
        
        // 14. ADVANCED FINANCIAL PATTERNS
        executeAdvancedFinancialPatterns(paySim, step, random);
        
        // 15. LIFE EVENTS
        executeLifeEvents(paySim, step, random);
        
        // 16. SWISS-SPECIFIC PATTERNS
        executeSwissSpecificPatterns(paySim, step, random);
        
        // 17. P2P TRANSFERS & SOCIAL SPENDING
        if (shouldMakeP2PTransfer(step, random)) {
            executeP2PTransfer(paySim, step, random);
        }
        
        // 18. SAVINGS & INVESTMENTS
        if (shouldMakeSavingsTransfer(step, random)) {
            executeSavingsTransfer(paySim, step, random);
        }
        
        // 19. PET EXPENSES
        if (hasPet && shouldPayPetExpenses(step, random)) {
            executePetExpenses(paySim, step, random);
        }
        
        // 20. SERAFE & HEALTH INSURANCE
        if (shouldPaySerafe(step, random)) {
            executeSerafePayment(paySim, step, random);
        }
        
        if (shouldPayHealthInsurance(step, random)) {
            executeHealthInsurancePayment(paySim, step, random);
        }
        
        // 21. TRAVELCARD PURCHASES
        if (shouldBuyTravelcard(step, random)) {
            executeTravelcardPurchase(paySim, step, random);
        }
        
        // 22. APÉRO CULTURE
        if (shouldGoApero(step, random)) {
            executeApero(paySim, step, random);
        }
        
        // 23. GIFT PURCHASES
        if (shouldBuyGift(step, random)) {
            executeGiftPurchase(paySim, step, random);
        }
    }
    
    /**
     * Execute seasonal patterns (ski trips, summer holidays, shopping spikes)
     */
    private void executeSeasonalPatterns(PaySim paySim, int step, MersenneTwisterFast random) {
        int dayOfYear = step % 365;
        
        // Ski trips (December-March)
        if (dayOfYear >= 335 || dayOfYear <= 90) { // Dec 1 - Mar 31
            if (shouldGoSkiing(step, random)) {
                executeSkiTrip(paySim, step, random);
            }
        }
        
        // Summer holidays (July-August)
        if (dayOfYear >= 180 && dayOfYear <= 240) { // Jul 1 - Aug 31
            if (shouldGoOnSummerHoliday(step, random)) {
                executeSummerHoliday(paySim, step, random);
            }
        }
        
        // Black Friday shopping (November)
        if (dayOfYear >= 300 && dayOfYear <= 330) { // Nov 1 - Nov 30
            if (shouldGoBlackFridayShopping(step, random)) {
                executeBlackFridayShopping(paySim, step, random);
            }
        }
        
        // Christmas shopping (December)
        if (dayOfYear >= 335 && dayOfYear <= 365) { // Dec 1 - Dec 31
            if (shouldGoChristmasShopping(step, random)) {
                executeChristmasShopping(paySim, step, random);
            }
        }
        
        // Back-to-school (August-September)
        if (dayOfYear >= 210 && dayOfYear <= 270) { // Aug 1 - Sep 30
            if (shouldGoBackToSchoolShopping(step, random)) {
                executeBackToSchoolShopping(paySim, step, random);
            }
        }
        
        // Swiss events
        if (dayOfYear >= 60 && dayOfYear <= 90) { // Feb-Mar (Fasnacht)
            if (shouldGoFasnacht(step, random)) {
                executeFasnacht(paySim, step, random);
            }
        }
        
        if (dayOfYear >= 210 && dayOfYear <= 240) { // Aug (Street Parade)
            if (shouldGoStreetParade(step, random)) {
                executeStreetParade(paySim, step, random);
            }
        }
    }
    
    /**
     * Determine if person should shop for groceries (1-2 times per week)
     */
    private boolean shouldShopForGroceries(int step, MersenneTwisterFast random) {
        int daysSinceLastGrocery = step - lastGroceryDay;
        if (daysSinceLastGrocery < 3) return false; // Minimum 3 days between shops
        
        // Age-based grocery shopping probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.20; // 20% chance per day (young people shop less frequently)
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.30; // 30% chance per day (middle-aged shop more)
        } else { // SENIOR
            baseProbability = 0.25; // 25% chance per day (seniors shop moderately)
        }
        
        // Family status adjustment
        if (hasKids) {
            baseProbability *= 1.3; // Families shop more frequently
        }
        
        // Income adjustment
        if (incomeLevel.equals("LOW")) {
            baseProbability *= 1.2; // Low income = more frequent small shops
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Determine if person should have lunch (weekdays, 70% probability)
     */
    private boolean shouldHaveLunch(int step, MersenneTwisterFast random) {
        int dayOfWeek = (step % 7); // 0-6 for days of week
        if (dayOfWeek >= 5) return false; // No lunch on weekends
        
        // Age-based lunch probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.75; // 75% chance (young people eat out more)
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.70; // 70% chance (middle-aged moderate)
        } else { // SENIOR
            baseProbability = 0.60; // 60% chance (seniors eat out less)
        }
        
        // Income adjustment
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.1; // High income = more restaurant lunches
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.8; // Low income = fewer restaurant lunches
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Determine if person should go on holiday (2 per year)
     */
    private boolean shouldGoOnHoliday(int step, MersenneTwisterFast random) {
        int daysSinceLastHoliday = step - lastHolidayDay;
        if (daysSinceLastHoliday < 60) return false; // Minimum 60 days between holidays
        
        // Age-based holiday probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.004; // Young people: ~1.5 holidays per year
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.006; // Middle-aged: ~2.2 holidays per year
        } else { // SENIOR
            baseProbability = 0.008; // Seniors: ~3 holidays per year (more time)
        }
        
        // Income adjustment
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.4; // High income = more holidays
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.6; // Low income = fewer holidays
        }
        
        // Family status adjustment
        if (hasKids) {
            baseProbability *= 1.2; // Families go on more holidays
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Determine if person should go shopping (occasional)
     */
    private boolean shouldGoShopping(int step, MersenneTwisterFast random) {
        // Age-based shopping probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.06; // 6% chance per day (young people shop more)
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.05; // 5% chance per day (middle-aged moderate)
        } else { // SENIOR
            baseProbability = 0.04; // 4% chance per day (seniors shop less)
        }
        
        // Income adjustment
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.3; // High income = more shopping
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.7; // Low income = less shopping
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Determine if person should visit healthcare (based on age and health)
     */
    private boolean shouldVisitHealthcare(int step, MersenneTwisterFast random) {
        double baseProbability = 0.008; // 0.8% base probability
        
        // Age-based healthcare probability
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.005; // 0.5% for young people
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.008; // 0.8% for middle-aged
        } else { // SENIOR
            baseProbability = 0.020; // 2% for seniors (more health issues)
        }
        
        // Income adjustment
        if (incomeLevel.equals("LOW")) {
            baseProbability *= 1.4; // Low income = more health issues
        }
        
        // Location adjustment
        if (location.equals("RURAL")) {
            baseProbability *= 0.8; // Rural areas have fewer healthcare visits
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Determine if person should receive salary (monthly with jitter)
     */
    private boolean shouldReceiveSalary(int step, MersenneTwisterFast random) {
        int daysSinceLastSalary = step - lastSalaryDay;
        if (daysSinceLastSalary < 28) return false; // Minimum 28 days
        
        // Add jitter: ±2 days around monthly cycle
        int expectedDay = 30;
        int jitter = random.nextInt(5) - 2; // -2 to +2 days
        int actualDay = expectedDay + jitter;
        
        return daysSinceLastSalary >= actualDay;
    }
    
    /**
     * Determine if person should receive 13th salary (November/December)
     */
    private boolean shouldReceive13thSalary(int step, MersenneTwisterFast random) {
        int daysSinceLast13thSalary = step - last13thSalaryDay;
        if (daysSinceLast13thSalary < 300) return false; // Minimum 300 days
        
        // Only 30% of people get 13th salary
        if (random.nextDouble() > 0.30) return false;
        
        // November (day 300-330) or December (day 335-365)
        int dayOfYear = step % 365;
        return (dayOfYear >= 300 && dayOfYear <= 330) || (dayOfYear >= 335 && dayOfYear <= 365);
    }
    
    /**
     * Determine if person should use transport (persona-specific)
     */
    private boolean shouldUseTransport(int step, MersenneTwisterFast random) {
        if (persona.equals("URBAN_TRANSPORT")) {
            // Urban transport users: daily on weekdays
            int dayOfWeek = step % 7;
            return dayOfWeek < 5; // Monday to Friday
        } else if (persona.equals("SUBURBAN_CAR")) {
            // Suburban car owners: occasional public transport
            return random.nextDouble() < 0.20; // 20% chance
        } else {
            // Others: mixed usage
            return random.nextDouble() < 0.60; // 60% chance
        }
    }
    
    /**
     * Determine if person should pay monthly services (with jitter)
     */
    private boolean shouldPayMonthlyServices(int step, MersenneTwisterFast random) {
        int daysSinceLastService = step - lastServiceBillingDay;
        if (daysSinceLastService < 28) return false;
        
        // Add jitter: ±2 days around monthly cycle
        int expectedDay = 30;
        int jitter = random.nextInt(5) - 2;
        int actualDay = expectedDay + jitter;
        
        return daysSinceLastService >= actualDay;
    }
    
    /**
     * Determine if person should pay housing (monthly with jitter)
     */
    private boolean shouldPayHousing(int step, MersenneTwisterFast random) {
        int daysSinceLastHousing = step - lastTransportBillingDay; // Reuse this field
        if (daysSinceLastHousing < 28) return false;
        
        // Housing is paid 1st-5th of month (with jitter)
        int dayOfMonth = (step % 30) + 1;
        int jitter = random.nextInt(3) - 1; // -1 to +1 days
        int actualDay = dayOfMonth + jitter;
        
        return actualDay >= 1 && actualDay <= 5;
    }
    
    /**
     * Determine if person should buy fuel (car owners only)
     */
    private boolean shouldBuyFuel(int step, MersenneTwisterFast random) {
        if (!hasCar) return false;
        
        int daysSinceLastFuel = step - lastFuelDay;
        if (daysSinceLastFuel < 7) return false; // Minimum 7 days
        
        // Fuel every 7-10 days
        return daysSinceLastFuel >= (7 + random.nextInt(4));
    }
    
    /**
     * Determine if person should pay gym (monthly for members)
     */
    private boolean shouldPayGym(int step, MersenneTwisterFast random) {
        if (!hasGymMembership) return false;
        
        int daysSinceLastGym = step - lastGymDay;
        if (daysSinceLastGym < 28) return false;
        
        // Monthly gym payment with jitter
        int expectedDay = 30;
        int jitter = random.nextInt(5) - 2;
        int actualDay = expectedDay + jitter;
        
        return daysSinceLastGym >= actualDay;
    }
    
    /**
     * Determine if person should pay childcare (families only)
     */
    private boolean shouldPayChildcare(int step, MersenneTwisterFast random) {
        if (!hasKids) return false;
        
        int daysSinceLastChildcare = step - lastChildcareDay;
        if (daysSinceLastChildcare < 28) return false;
        
        // Monthly childcare payment
        return daysSinceLastChildcare >= 30;
    }
    
    /**
     * Determine if person should make donation (December or crisis-based)
     */
    private boolean shouldMakeDonation(int step, MersenneTwisterFast random) {
        int daysSinceLastDonation = step - lastDonationDay;
        if (daysSinceLastDonation < 60) return false;
        
        // Higher probability in December (charity season)
        int dayOfYear = step % 365;
        double baseProbability = 0.005; // 0.5% base probability
        
        if (dayOfYear >= 335 && dayOfYear <= 365) { // December
            baseProbability *= 5.0; // 5x higher in December
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Determine if person should pay credit card (monthly for card holders)
     */
    private boolean shouldPayCreditCard(int step, MersenneTwisterFast random) {
        if (!hasCreditCard) return false;
        
        // Credit card payment around 15th of month
        int dayOfMonth = (step % 30) + 1;
        int jitter = random.nextInt(5) - 2; // -2 to +2 days
        int actualDay = dayOfMonth + jitter;
        
        return actualDay >= 13 && actualDay <= 17;
    }
    
    // Seasonal decision methods with age-based variations
    private boolean shouldGoSkiing(int step, MersenneTwisterFast random) {
        // Age-based skiing probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.15; // 15% of young people go skiing
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.25; // 25% of middle-aged go skiing
        } else { // SENIOR
            baseProbability = 0.10; // 10% of seniors go skiing
        }
        
        // Income adjustment
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.5; // High income = more skiing
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.6; // Low income = less skiing
        }
        
        // Location adjustment
        if (location.equals("URBAN")) {
            baseProbability *= 0.8; // Urban people ski less
        } else if (location.equals("RURAL")) {
            baseProbability *= 1.3; // Rural people ski more
        }
        
        if (random.nextDouble() > baseProbability) return false;
        
        // Only if they haven't gone recently (age-based minimum intervals)
        int daysSinceLastHoliday = step - lastHolidayDay;
        int minInterval = ageGroup.equals("YOUNG") ? 45 : (ageGroup.equals("MIDDLE") ? 30 : 60);
        return daysSinceLastHoliday >= minInterval;
    }
    
    private boolean shouldGoOnSummerHoliday(int step, MersenneTwisterFast random) {
        // Age-based summer holiday probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.35; // 35% of young people go on summer holiday
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.45; // 45% of middle-aged go on summer holiday
        } else { // SENIOR
            baseProbability = 0.50; // 50% of seniors go on summer holiday (more time)
        }
        
        // Income adjustment
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.4; // High income = more holidays
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.7; // Low income = fewer holidays
        }
        
        // Family status adjustment
        if (hasKids) {
            baseProbability *= 1.2; // Families go on more holidays
        }
        
        if (random.nextDouble() > baseProbability) return false;
        
        // Age-based minimum intervals between holidays
        int daysSinceLastHoliday = step - lastHolidayDay;
        int minInterval = ageGroup.equals("YOUNG") ? 75 : (ageGroup.equals("MIDDLE") ? 60 : 45);
        return daysSinceLastHoliday >= minInterval;
    }
    
    private boolean shouldGoBlackFridayShopping(int step, MersenneTwisterFast random) {
        // Age-based Black Friday shopping probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.40; // 40% of young people do Black Friday
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.35; // 35% of middle-aged do Black Friday
        } else { // SENIOR
            baseProbability = 0.20; // 20% of seniors do Black Friday
        }
        
        // Income adjustment
        if (incomeLevel.equals("LOW")) {
            baseProbability *= 1.3; // Low income = more Black Friday deals
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    private boolean shouldGoChristmasShopping(int step, MersenneTwisterFast random) {
        // Age-based Christmas shopping probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.55; // 55% of young people do Christmas shopping
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.70; // 70% of middle-aged do Christmas shopping
        } else { // SENIOR
            baseProbability = 0.65; // 65% of seniors do Christmas shopping
        }
        
        // Family status adjustment
        if (hasKids) {
            baseProbability *= 1.2; // Families do more Christmas shopping
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    private boolean shouldGoBackToSchoolShopping(int step, MersenneTwisterFast random) {
        // Only families with kids
        if (!hasKids) return false;
        
        // Age-based back-to-school shopping (only for middle-aged with kids)
        if (!ageGroup.equals("MIDDLE")) return false;
        
        // 85% of families with school-age kids do back-to-school shopping
        return random.nextDouble() < 0.85;
    }
    
    private boolean shouldGoFasnacht(int step, MersenneTwisterFast random) {
        // Age-based Fasnacht probability
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.35; // 35% of young people go to Fasnacht
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.25; // 25% of middle-aged go to Fasnacht
        } else { // SENIOR
            baseProbability = 0.15; // 15% of seniors go to Fasnacht
        }
        
        // Location adjustment (Fasnacht is mainly in German-speaking regions)
        if (location.equals("URBAN")) {
            baseProbability *= 1.2; // Urban people more likely to attend
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    private boolean shouldGoStreetParade(int step, MersenneTwisterFast random) {
        // Age-based Street Parade probability (mainly young people)
        if (ageGroup.equals("YOUNG")) {
            double baseProbability = 0.35; // 35% of young people
            // Income adjustment
            if (incomeLevel.equals("HIGH")) {
                baseProbability *= 1.2; // High income young people more likely
            }
            return random.nextDouble() < baseProbability;
        } else if (ageGroup.equals("MIDDLE")) {
            return random.nextDouble() < 0.08; // 8% of middle-aged
        } else { // SENIOR
            return random.nextDouble() < 0.02; // 2% of seniors
        }
    }
    
    // Advanced financial pattern decision methods
    private boolean shouldMakeP2PTransfer(int step, MersenneTwisterFast random) {
        // P2P transfers are more common for young and social personas
        if (ageGroup.equals("YOUNG")) {
            return random.nextDouble() < 0.15; // 15% chance per day
        } else if (persona.equals("URBAN_TRANSPORT") || persona.equals("YOUNG_PROF")) {
            return random.nextDouble() < 0.10; // 10% chance per day
        }
        return random.nextDouble() < 0.05; // 5% chance per day
    }
    
    private boolean shouldMakeSavingsTransfer(int step, MersenneTwisterFast random) {
        // Savings transfers on 1st and 15th of month
        int dayOfMonth = (step % 30) + 1;
        int jitter = random.nextInt(3) - 1; // -1 to +1 days
        int actualDay = dayOfMonth + jitter;
        
        return (actualDay == 1 || actualDay == 15);
    }
    
    private boolean shouldPayPetExpenses(int step, MersenneTwisterFast random) {
        int daysSinceLastPetExpense = step - lastPetExpenseDay;
        if (daysSinceLastPetExpense < 28) return false;
        
        // Monthly pet expenses
        return daysSinceLastPetExpense >= 30;
    }
    
    private boolean shouldPaySerafe(int step, MersenneTwisterFast random) {
        // Serafe is paid quarterly (every 90 days)
        int daysSinceLastSerafe = step - lastSerafePaymentDay;
        if (daysSinceLastSerafe < 80) return false;
        
        return daysSinceLastSerafe >= 90;
    }
    
    private boolean shouldPayHealthInsurance(int step, MersenneTwisterFast random) {
        // Health insurance is paid monthly
        int daysSinceLastHealthInsurance = step - lastHealthInsuranceDay;
        if (daysSinceLastHealthInsurance < 28) return false;
        
        return daysSinceLastHealthInsurance >= 30;
    }
    
    private boolean shouldBuyTravelcard(int step, MersenneTwisterFast random) {
        // Travelcards are bought annually (every 365 days)
        int daysSinceLastTravelcard = step - lastTravelcardPurchaseDay;
        if (daysSinceLastTravelcard < 350) return false;
        
        return daysSinceLastTravelcard >= 365;
    }
    
    private boolean shouldGoApero(int step, MersenneTwisterFast random) {
        // Apéro culture: 5-7 PM on weekdays, especially Thursdays and Fridays
        int dayOfWeek = step % 7;
        if (dayOfWeek >= 5) return false; // No Apéro on weekends
        
        // Higher probability on Thursdays and Fridays
        double baseProbability = 0.20; // 20% base probability
        if (dayOfWeek == 3 || dayOfWeek == 4) { // Thursday or Friday
            baseProbability *= 2.0; // 2x higher
        }
        
        // Only for social personas
        if (persona.equals("YOUNG_PROF") || persona.equals("URBAN_TRANSPORT")) {
            baseProbability *= 1.5;
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    private boolean shouldBuyGift(int step, MersenneTwisterFast random) {
        // Gift purchases around birthdays and holidays
        int dayOfYear = step % 365;
        
        // Birthday month (2 weeks before and after)
        String currentMonth = getCurrentMonth(dayOfYear);
        if (currentMonth.equals(birthdayMonth)) {
            return random.nextDouble() < 0.30; // 30% chance in birthday month
        }
        
        // Holiday season (November-December)
        if (dayOfYear >= 300 && dayOfYear <= 365) {
            return random.nextDouble() < 0.25; // 25% chance during holidays
        }
        
        // Regular gift giving (5% chance per day)
        return random.nextDouble() < 0.05;
    }
    
    /**
     * Get current month based on day of year
     */
    private String getCurrentMonth(int dayOfYear) {
        if (dayOfYear < 31) return "January";
        else if (dayOfYear < 59) return "February";
        else if (dayOfYear < 90) return "March";
        else if (dayOfYear < 120) return "April";
        else if (dayOfYear < 151) return "May";
        else if (dayOfYear < 181) return "June";
        else if (dayOfYear < 212) return "July";
        else if (dayOfYear < 243) return "August";
        else if (dayOfYear < 273) return "September";
        else if (dayOfYear < 304) return "October";
        else if (dayOfYear < 334) return "November";
        else return "December";
    }
    
    /**
     * Execute transport billing (monthly)
     */
    private void executeTransportBilling(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "TRANSPORTATION_PUBLIC", null);
        makeSwissTransaction(paySim, step, "TRANSPORTATION_PUBLIC", amount, preferredTransportService);
        lastTransportBillingDay = step;
    }
    
    /**
     * Execute grocery shopping (1-2 times per week)
     */
    private void executeGroceryShopping(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "GENERAL_EXPENSES_DAILY", null);
        makeSwissTransaction(paySim, step, "GENERAL_EXPENSES_DAILY", amount, preferredGroceryStore);
        lastGroceryDay = step;
    }
    
    /**
     * Execute lunch (weekdays)
     */
    private void executeLunch(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "FOOD_DINING_LUNCH", null);
        makeSwissTransaction(paySim, step, "FOOD_DINING_LUNCH", amount, preferredRestaurant);
    }
    
    /**
     * Execute monthly services (streaming, mobile)
     */
    private void executeMonthlyServices(PaySim paySim, int step, MersenneTwisterFast random) {
        // Streaming service
        double streamingAmount = pickSwissAmount(random, "ENTERTAINMENT_STREAMING", null);
        makeSwissTransaction(paySim, step, "ENTERTAINMENT_STREAMING", streamingAmount, preferredStreamingService);
        
        // Mobile service
        double mobileAmount = pickSwissAmount(random, "ENTERTAINMENT_MOBILE", null);
        makeSwissTransaction(paySim, step, "ENTERTAINMENT_MOBILE", mobileAmount, preferredBank + " Mobile");
        
        lastServiceBillingDay = step;
    }
    
    /**
     * Execute housing billing (monthly)
     */
    private void executeHousingBilling(PaySim paySim, int step, MersenneTwisterFast random) {
        double housingAmount = pickSwissAmount(random, "HOUSING_GENERAL", null);
        makeSwissTransaction(paySim, step, "HOUSING_GENERAL", housingAmount, "Local Utility");
        
        double utilitiesAmount = pickSwissAmount(random, "UTILITIES_GENERAL", null);
        makeSwissTransaction(paySim, step, "UTILITIES_GENERAL", utilitiesAmount, "Electricity Provider");
    }
    
    /**
     * Execute holiday (2 per year)
     */
    private void executeHoliday(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "TRAVEL_GENERAL", null);
        makeSwissTransaction(paySim, step, "TRAVEL_GENERAL", amount, "Booking.com");
        lastHolidayDay = step;
    }
    
    /**
     * Execute shopping (occasional)
     */
    private void executeShopping(PaySim paySim, int step, MersenneTwisterFast random) {
        // Random shopping category
        String[] shoppingCategories = {"SHOPPING_CLOTHING", "SHOPPING_ELECTRONICS", "SHOPPING_BOOKS"};
        String category = shoppingCategories[random.nextInt(shoppingCategories.length)];
        
        double amount = pickSwissAmount(random, category, null);
        String company = getSwissCompanyName(random, category);
        makeSwissTransaction(paySim, step, category, amount, company);
    }
    
    /**
     * Execute healthcare (occasional)
     */
    private void executeHealthcare(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "HEALTHCARE_GENERAL", null);
        String company = getSwissCompanyName(random, "HEALTHCARE_GENERAL");
        makeSwissTransaction(paySim, step, "HEALTHCARE_GENERAL", amount, company);
    }
    
    /**
     * Execute income (monthly salary)
     */
    private void executeIncome(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "INCOME_GENERAL", null);
        makeSwissTransaction(paySim, step, "INCOME_GENERAL", amount, preferredBank + " Salary");
    }
    
    private String pickSwissAction(MersenneTwisterFast random, Map<String, Double> stepActionProb) {
        // Use new Main_Categories and Sub_Categories
        String[] swissActions = {
            "INCOME_GENERAL", "HOUSING_GENERAL", "GENERAL_EXPENSES_DAILY", "TRAVEL_GENERAL",
            "OTHER_GENERAL", "SHOPPING_ELECTRONICS", "SHOPPING_BOOKS", "SHOPPING_CLOTHING",
            "HEALTHCARE_GENERAL", "TRANSPORTATION_FUEL", "TRANSPORTATION_PUBLIC", "FOOD_DINING_LUNCH",
            "TAXES_GENERAL", "UTILITIES_GENERAL", "ENTERTAINMENT_STREAMING", "ENTERTAINMENT_MOBILE"
        };
        
        // Adjust probabilities based on Swiss context
        Map<String, Double> swissProbabilities = new HashMap<>();
        
        // Base probabilities for different transaction types
        swissProbabilities.put("INCOME_GENERAL", 0.05);
        swissProbabilities.put("HOUSING_GENERAL", 0.08);
        swissProbabilities.put("GENERAL_EXPENSES_DAILY", 0.20);
        swissProbabilities.put("TRAVEL_GENERAL", 0.05);
        swissProbabilities.put("OTHER_GENERAL", 0.05);
        swissProbabilities.put("SHOPPING_ELECTRONICS", 0.08);
        swissProbabilities.put("SHOPPING_BOOKS", 0.05);
        swissProbabilities.put("SHOPPING_CLOTHING", 0.10);
        swissProbabilities.put("HEALTHCARE_GENERAL", 0.08);
        swissProbabilities.put("TRANSPORTATION_FUEL", 0.08);
        swissProbabilities.put("TRANSPORTATION_PUBLIC", 0.12);
        swissProbabilities.put("FOOD_DINING_LUNCH", 0.15);
        swissProbabilities.put("TAXES_GENERAL", 0.03);
        swissProbabilities.put("UTILITIES_GENERAL", 0.05);
        swissProbabilities.put("ENTERTAINMENT_STREAMING", 0.08);
        swissProbabilities.put("ENTERTAINMENT_MOBILE", 0.05);
        
        // Adjust based on demographic attributes
        if (ageGroup.equals("YOUNG")) {
            swissProbabilities.put("ENTERTAINMENT_STREAMING", 0.15);
            swissProbabilities.put("ENTERTAINMENT_MOBILE", 0.12);
            swissProbabilities.put("SHOPPING_CLOTHING", 0.15);
        } else if (ageGroup.equals("SENIOR")) {
            swissProbabilities.put("HEALTHCARE_GENERAL", 0.15);
            swissProbabilities.put("TRANSPORTATION_PUBLIC", 0.20);
        }
        
        if (location.equals("RURAL")) {
            swissProbabilities.put("TRANSPORTATION_FUEL", 0.20);
            swissProbabilities.put("GENERAL_EXPENSES_DAILY", 0.30);
        }
        
        if (isStudent) {
            swissProbabilities.put("SHOPPING_BOOKS", 0.20);
            swissProbabilities.put("FOOD_DINING_LUNCH", 0.25);
        }
        
        // Add recurring pattern logic based on client ID
        int clientHash = Math.abs(getName().hashCode());
        
        // Some clients prefer specific transaction types (creates recurring patterns)
        if (clientHash % 10 == 0) { // 10% of clients prefer entertainment
            swissProbabilities.put("ENTERTAINMENT_STREAMING", 0.25);
            swissProbabilities.put("ENTERTAINMENT_MOBILE", 0.20);
        } else if (clientHash % 10 == 1) { // 10% prefer shopping
            swissProbabilities.put("SHOPPING_CLOTHING", 0.25);
            swissProbabilities.put("SHOPPING_ELECTRONICS", 0.15);
        } else if (clientHash % 10 == 2) { // 10% prefer transport
            swissProbabilities.put("TRANSPORTATION_PUBLIC", 0.25);
            swissProbabilities.put("TRANSPORTATION_FUEL", 0.20);
        } else if (clientHash % 10 == 3) { // 10% prefer healthcare
            swissProbabilities.put("HEALTHCARE_GENERAL", 0.20);
            swissProbabilities.put("UTILITIES_GENERAL", 0.10);
        }
        
        // Create random collection for action selection
        RandomCollection<String> actionPicker = new RandomCollection<>(random);
        for (Map.Entry<String, Double> entry : swissProbabilities.entrySet()) {
            actionPicker.add(entry.getValue(), entry.getKey());
        }
        
        return actionPicker.next();
    }
    
    private int pickCount(MersenneTwisterFast random, int targetStepCount) {
        // B(n,p): n = targetStepCount & p = clientWeight
        // Use the clientWeight from the parent class
        double clientWeight = ((double) clientProfile.getClientTargetCount()) / Parameters.stepsProfiles.getTotalTargetCount();
        Binomial transactionNb = new Binomial(targetStepCount, clientWeight, random);
        return transactionNb.nextInt();
    }
    
    private double pickSwissAmount(MersenneTwisterFast random, String action, StepActionProfile stepAmountProfile) {
        // Swiss-specific amount ranges based on transaction type and demographics
        double baseAmount = 0;
        double stdDev = 0;
        
        switch (action) {
            case "INCOME_GENERAL":
                baseAmount = 5000.0; // CHF (monthly salary)
                stdDev = 1500.0;
                break;
            case "HOUSING_GENERAL":
                baseAmount = 1800.0; // CHF (rent/mortgage)
                stdDev = 600.0;
                break;
            case "GENERAL_EXPENSES_DAILY":
                baseAmount = 45.0; // CHF (daily groceries/small items)
                stdDev = 15.0;
                break;
            case "TRAVEL_GENERAL":
                baseAmount = 150.0; // CHF (travel expenses)
                stdDev = 100.0;
                break;
            case "OTHER_GENERAL":
                baseAmount = 25.0; // CHF (miscellaneous)
                stdDev = 15.0;
                break;
            case "SHOPPING_ELECTRONICS":
                baseAmount = 250.0; // CHF (electronics)
                stdDev = 150.0;
                break;
            case "SHOPPING_BOOKS":
                baseAmount = 35.0; // CHF (books)
                stdDev = 20.0;
                break;
            case "SHOPPING_CLOTHING":
                baseAmount = 85.0; // CHF (clothing)
                stdDev = 45.0;
                break;
            case "HEALTHCARE_GENERAL":
                baseAmount = 120.0; // CHF (healthcare)
                stdDev = 80.0;
                break;
            case "TRANSPORTATION_FUEL":
                baseAmount = 80.0; // CHF (fuel)
                stdDev = 30.0;
                break;
            case "TRANSPORTATION_PUBLIC":
                baseAmount = 8.5; // CHF (public transport)
                stdDev = 5.0;
                break;
            case "FOOD_DINING_LUNCH":
                baseAmount = 25.0; // CHF (lunch)
                stdDev = 12.0;
                break;
            case "TAXES_GENERAL":
                baseAmount = 800.0; // CHF (taxes)
                stdDev = 400.0;
                break;
            case "UTILITIES_GENERAL":
                baseAmount = 200.0; // CHF (utilities)
                stdDev = 100.0;
                break;
            case "ENTERTAINMENT_STREAMING":
                baseAmount = 25.0; // CHF (streaming subscriptions)
                stdDev = 15.0;
                break;
            case "ENTERTAINMENT_MOBILE":
                baseAmount = 60.0; // CHF (mobile phone)
                stdDev = 30.0;
                break;
            default:
                baseAmount = 50.0;
                stdDev = 25.0;
        }
        
        // Adjust based on income level
        if (incomeLevel.equals("HIGH")) {
            baseAmount *= 1.5;
            stdDev *= 1.3;
        } else if (incomeLevel.equals("LOW")) {
            baseAmount *= 0.7;
            stdDev *= 0.8;
        }
        
        // Add age-based variations
        if (ageGroup.equals("YOUNG")) {
            baseAmount *= 0.9; // Young people spend less
            stdDev *= 1.2; // More variation in spending
        } else if (ageGroup.equals("SENIOR")) {
            baseAmount *= 1.1; // Seniors spend more
            stdDev *= 0.9; // Less variation in spending
        }
        
        // Add persona-based variations
        if (persona.equals("STUDENT")) {
            baseAmount *= 0.6; // Students spend much less
            stdDev *= 1.5; // High variation
        } else if (persona.equals("FAMILY")) {
            baseAmount *= 1.3; // Families spend more
            stdDev *= 1.1; // Moderate variation
        }
        
        // Add random jitter (±10% to make amounts less predictable)
        double jitter = 0.9 + random.nextDouble() * 0.2; // 0.9 to 1.1
        baseAmount *= jitter;
        
        // Generate amount with normal distribution
        double amount = -1;
        while (amount <= 0) {
            amount = random.nextGaussian() * stdDev + baseAmount;
        }
        
        return amount;
    }
    
    private void makeSwissTransaction(PaySim paySim, int step, String action, double amount) {
        makeSwissTransaction(paySim, step, action, amount, null);
    }
    
    private void makeSwissTransaction(PaySim paySim, int step, String action, double amount, String companyName) {
        // Use provided company name or get realistic Swiss company name
        String swissCompanyName = (companyName != null) ? companyName : getSwissCompanyName(paySim.random, action);
        
        // Create transaction with Person ID in the name
        String nameOrig = this.getPersonId() + "_" + this.getName();
        String nameDest = swissCompanyName;
        double oldBalanceOrig = this.getBalance();
        double oldBalanceDest = 0.0;
        
        // Withdraw amount from client
        boolean isUnauthorizedOverdraft = this.withdraw(amount);
        
        double newBalanceOrig = this.getBalance();
        double newBalanceDest = amount;
        
        Transaction transaction = new Transaction(step, action, amount, nameOrig, oldBalanceOrig,
                                               newBalanceOrig, nameDest, oldBalanceDest, newBalanceDest);
        
        transaction.setUnauthorizedOverdraft(isUnauthorizedOverdraft);
        transaction.setFraud(false);
        
        // Add transaction to PaySim
        paySim.getTransactions().add(transaction);
    }
    
    // Seasonal execution methods
    private void executeSkiTrip(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "TRAVEL_GENERAL", null) * 2.5; // Ski trips are more expensive
        String[] skiResorts = {"Zermatt", "St. Moritz", "Davos", "Verbier", "Gstaad", "Arosa", "Lenzerheide"};
        String resort = skiResorts[random.nextInt(skiResorts.length)];
        makeSwissTransaction(paySim, step, "TRAVEL_GENERAL", amount, resort + " Ski Resort");
        lastHolidayDay = step;
    }
    
    private void executeSummerHoliday(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "TRAVEL_GENERAL", null) * 2.0; // Summer holidays are expensive
        String[] destinations = {"SWISS Airlines", "easyJet", "Booking.com", "Airbnb", "Hotel Booking"};
        String destination = destinations[random.nextInt(destinations.length)];
        makeSwissTransaction(paySim, step, "TRAVEL_GENERAL", amount, destination);
        lastHolidayDay = step;
    }
    
    private void executeBlackFridayShopping(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "SHOPPING_ELECTRONICS", null) * 1.5; // Bigger purchases on Black Friday
        String[] retailers = {"Digitec", "MediaMarkt", "Interdiscount", "Amazon", "Galaxus"};
        String retailer = retailers[random.nextInt(retailers.length)];
        makeSwissTransaction(paySim, step, "SHOPPING_ELECTRONICS", amount, retailer + " Black Friday");
    }
    
    private void executeChristmasShopping(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "SHOPPING_CLOTHING", null) * 2.0; // Christmas gifts are expensive
        String[] stores = {"Manor", "Jelmoli", "Globus", "H&M", "Zara", "Online Store"};
        String store = stores[random.nextInt(stores.length)];
        makeSwissTransaction(paySim, step, "SHOPPING_CLOTHING", amount, store + " Christmas");
    }
    
    private void executeBackToSchoolShopping(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "SHOPPING_CLOTHING", null) * 1.3; // School supplies and clothes
        String[] stores = {"Coop", "Migros", "Manor", "H&M", "C&A", "School Supply Store"};
        String store = stores[random.nextInt(stores.length)];
        makeSwissTransaction(paySim, step, "SHOPPING_CLOTHING", amount, store + " Back to School");
    }
    
    private void executeFasnacht(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "ENTERTAINMENT_GENERAL", null) * 1.5; // Costumes and drinks
        makeSwissTransaction(paySim, step, "ENTERTAINMENT_GENERAL", amount, "Fasnacht Festival");
    }
    
    private void executeStreetParade(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "ENTERTAINMENT_GENERAL", null) * 1.2; // Transport and drinks
        makeSwissTransaction(paySim, step, "ENTERTAINMENT_GENERAL", amount, "Street Parade Zurich");
    }
    
    // Additional execution methods for new features
    private void execute13thSalary(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "INCOME_GENERAL", null) * 0.8; // 13th salary is usually 80% of monthly
        makeSwissTransaction(paySim, step, "INCOME_GENERAL", amount, preferredBank + " 13th Salary");
        last13thSalaryDay = step;
    }
    
    private void executeTransport(PaySim paySim, int step, MersenneTwisterFast random) {
        if (persona.equals("URBAN_TRANSPORT")) {
            // Daily transport for urban users
            double amount = pickSwissAmount(random, "TRANSPORTATION_PUBLIC", null);
            makeSwissTransaction(paySim, step, "TRANSPORTATION_PUBLIC", amount, preferredTransportService);
        } else {
            // Monthly pass for others
            if (step % 30 == 0) {
                double amount = pickSwissAmount(random, "TRANSPORTATION_PUBLIC", null) * 25; // Monthly pass
                makeSwissTransaction(paySim, step, "TRANSPORTATION_PUBLIC", amount, preferredTransportService + " Monthly Pass");
            }
        }
    }
    
    private void executeFuelPurchase(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "TRANSPORTATION_FUEL", null);
        String[] fuelStations = {"Shell", "Esso", "BP", "Migrol", "Coop Pronto", "Avia"};
        String station = fuelStations[random.nextInt(fuelStations.length)];
        makeSwissTransaction(paySim, step, "TRANSPORTATION_FUEL", amount, station);
        lastFuelDay = step;
    }
    
    private void executeGymPayment(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "ENTERTAINMENT_GENERAL", null);
        String[] gyms = {"NonStop Gym", "Kieser", "Fitness First", "PureGym", "Migros Fitness", "Coop Fitness"};
        String gym = gyms[random.nextInt(gyms.length)];
        makeSwissTransaction(paySim, step, "ENTERTAINMENT_GENERAL", amount, gym);
        lastGymDay = step;
    }
    
    private void executeChildcarePayment(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "EDUCATION_GENERAL", null);
        String[] childcare = {"Kindergarten", "School", "Daycare Center", "After-School Program", "Tutoring"};
        String provider = childcare[random.nextInt(childcare.length)];
        makeSwissTransaction(paySim, step, "EDUCATION_GENERAL", amount, provider);
        lastChildcareDay = step;
    }
    
    private void executeDonation(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "OTHER_GENERAL", null) * 0.5; // Donations are smaller
        String[] charities = {"Red Cross", "UNICEF", "WWF", "Doctors Without Borders", "Local Charity"};
        String charity = charities[random.nextInt(charities.length)];
        makeSwissTransaction(paySim, step, "OTHER_GENERAL", amount, charity);
        lastDonationDay = step;
    }
    
    private void executeCreditCardPayment(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "GENERAL_EXPENSES_DAILY", null) * 2.0; // Credit card payments are larger
        makeSwissTransaction(paySim, step, "GENERAL_EXPENSES_DAILY", amount, preferredBank + " Credit Card Payment");
    }
    
    // Advanced financial pattern execution methods
    private void executeAdvancedFinancialPatterns(PaySim paySim, int step, MersenneTwisterFast random) {
        // Payday splurging behavior
        if (isInPaydaySplurgeMode) {
            executePaydaySplurge(paySim, step, random);
        }
        
        // BNPL installment payments
        if (currentBNPLInstallment > 0) {
            executeBNPLPayment(paySim, step, random);
        }
        
        // Economic sentiment effects
        executeEconomicSentimentEffects(paySim, step, random);
    }
    
    private void executeLifeEvents(PaySim paySim, int step, MersenneTwisterFast random) {
        // Job change effects
        if (hasJobChange && step == jobChangeDay) {
            executeJobChange(paySim, step, random);
        }
        
        // Job loss effects
        if (hasJobLoss && step == jobLossDay) {
            executeJobLoss(paySim, step, random);
        }
        
        // Moving effects
        if (hasMoved && step == moveDay) {
            executeMoving(paySim, step, random);
        }
        
        // New child effects
        if (hasNewChild && step == childBirthDay) {
            executeNewChild(paySim, step, random);
        }
    }
    
    private void executeSwissSpecificPatterns(PaySim paySim, int step, MersenneTwisterFast random) {
        // Travelcard effects on transport costs
        if (hasHalbtax || hasGA) {
            // Reduce transport costs for travelcard holders
            // This is handled in the transport execution methods
        }
        
        // Evolving preferences (occasional changes)
        if (random.nextDouble() < 0.001) { // 0.1% chance per day
            evolvePreferences(random);
        }
    }
    
    private void executeP2PTransfer(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = 15.0 + random.nextDouble() * 50.0; // 15-65 CHF for P2P transfers
        String[] memoTypes = {"Lunch", "Dinner", "Rent Share", "Tickets", "Coffee", "Drinks", "Split Bill"};
        String memo = memoTypes[random.nextInt(memoTypes.length)];
        
        makeSwissTransaction(paySim, step, "P2P_TRANSFER", amount, "Twint " + memo);
        lastP2PTransferDay = step;
    }
    
    private void executeSavingsTransfer(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = monthlySalary * savingsRate;
        makeSwissTransaction(paySim, step, "SAVINGS_TRANSFER", amount, preferredBank + " Savings");
        lastSavingsTransferDay = step;
    }
    
    private void executePetExpenses(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = 80.0 + random.nextDouble() * 120.0; // 80-200 CHF for pet expenses
        String[] petStores = {"Pet Store", "Vet Clinic", "Pet Food Store", "Pet Insurance"};
        String store = petStores[random.nextInt(petStores.length)];
        
        makeSwissTransaction(paySim, step, "PET_EXPENSES", amount, store);
        lastPetExpenseDay = step;
    }
    
    private void executeSerafePayment(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = 335.0; // Fixed Serafe amount per quarter
        makeSwissTransaction(paySim, step, "SERAFE_PAYMENT", amount, "Serafe Media Tax");
        lastSerafePaymentDay = step;
    }
    
    private void executeHealthInsurancePayment(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = 300.0 + random.nextDouble() * 200.0; // 300-500 CHF for health insurance
        makeSwissTransaction(paySim, step, "HEALTH_INSURANCE", amount, healthInsuranceProvider);
        lastHealthInsuranceDay = step;
    }
    
    private void executeTravelcardPurchase(PaySim paySim, int step, MersenneTwisterFast random) {
        if (hasGA) {
            double amount = 3860.0; // GA annual cost
            makeSwissTransaction(paySim, step, "TRAVELCARD_PURCHASE", amount, "SBB GA Travelcard");
        } else if (hasHalbtax) {
            double amount = 185.0; // Halbtax annual cost
            makeSwissTransaction(paySim, step, "TRAVELCARD_PURCHASE", amount, "SBB Halbtax");
        }
        lastTravelcardPurchaseDay = step;
    }
    
    private void executeApero(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = 25.0 + random.nextDouble() * 35.0; // 25-60 CHF for Apéro
        makeSwissTransaction(paySim, step, "APERO_EXPENSES", amount, preferredBar);
        lastAperoDay = step;
    }
    
    private void executeGiftPurchase(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = 50.0 + random.nextDouble() * 100.0; // 50-150 CHF for gifts
        String[] giftStores = {"Gift Shop", "Jewelry Store", "Book Store", "Online Gift Store"};
        String store = giftStores[random.nextInt(giftStores.length)];
        
        makeSwissTransaction(paySim, step, "GIFT_PURCHASE", amount, store);
        lastGiftPurchaseDay = step;
    }
    
    // Advanced financial behavior methods
    private void executePaydaySplurge(PaySim paySim, int step, MersenneTwisterFast random) {
        // High-end restaurants, electronics, clothing
        String[] splurgeCategories = {"HIGH_END_RESTAURANT", "LUXURY_SHOPPING", "ELECTRONICS_SPLURGE"};
        String category = splurgeCategories[random.nextInt(splurgeCategories.length)];
        
        double amount = 150.0 + random.nextDouble() * 350.0; // 150-500 CHF for splurges
        String company = getSwissCompanyName(random, category);
        makeSwissTransaction(paySim, step, category, amount, company);
    }
    
    private void executeBNPLPayment(PaySim paySim, int step, MersenneTwisterFast random) {
        // Monthly BNPL installment
        makeSwissTransaction(paySim, step, "BNPL_INSTALLMENT", monthlyBNPLPayment, "Klarna Installment");
        currentBNPLInstallment--;
        
        if (currentBNPLInstallment == 0) {
            // BNPL completed
            totalBNPLAmount = 0.0;
            monthlyBNPLPayment = 0.0;
        }
    }
    
    private void executeEconomicSentimentEffects(PaySim paySim, int step, MersenneTwisterFast random) {
        // Simulate economic sentiment (could be made dynamic)
        double economicSentiment = 0.7; // 0.0 = bad, 1.0 = good
        
        if (economicSentiment < 0.5) {
            // Bad economy: fewer luxury items, more small comforts
            if (random.nextDouble() < 0.10) { // 10% chance
                double amount = 15.0 + random.nextDouble() * 25.0; // 15-40 CHF
                String[] comfortItems = {"Gourmet Coffee", "Cosmetics", "Streaming Service", "Small Treat"};
                String item = comfortItems[random.nextInt(comfortItems.length)];
                makeSwissTransaction(paySim, step, "COMFORT_PURCHASE", amount, item);
            }
        }
    }
    
    // Life event execution methods
    private void executeJobChange(PaySim paySim, int step, MersenneTwisterFast random) {
        // Increase salary
        monthlySalary *= (1.1 + random.nextDouble() * 0.2); // 10-30% increase
        
        // Change commute patterns
        if (random.nextDouble() < 0.5) {
            // 50% chance of changing transport preferences
            hasCar = !hasCar;
            if (hasCar) {
                persona = "SUBURBAN_CAR";
            } else {
                persona = "URBAN_TRANSPORT";
            }
        }
    }
    
    private void executeJobLoss(PaySim paySim, int step, MersenneTwisterFast random) {
        // Stop salary
        monthlySalary = 0.0;
        
        // Cancel non-essential subscriptions
        hasGymMembership = false;
        hasCreditCard = false;
        
        // Switch to broke mode
        isInBrokeMode = true;
        isInPaydaySplurgeMode = false;
    }
    
    private void executeMoving(PaySim paySim, int step, MersenneTwisterFast random) {
        // Moving company fees
        double movingAmount = 800.0 + random.nextDouble() * 1200.0; // 800-2000 CHF
        makeSwissTransaction(paySim, step, "MOVING_EXPENSES", movingAmount, "Moving Company");
        
        // Furniture purchases
        double furnitureAmount = 500.0 + random.nextDouble() * 1500.0; // 500-2000 CHF
        makeSwissTransaction(paySim, step, "FURNITURE_PURCHASE", furnitureAmount, "IKEA");
        
        // Home improvement
        double improvementAmount = 200.0 + random.nextDouble() * 800.0; // 200-1000 CHF
        makeSwissTransaction(paySim, step, "HOME_IMPROVEMENT", improvementAmount, "DIY Store");
        
        // Change rent amount
        // This would affect future housing payments
    }
    
    private void executeNewChild(PaySim paySim, int step, MersenneTwisterFast random) {
        // Baby supplies
        double babyAmount = 300.0 + random.nextDouble() * 500.0; // 300-800 CHF
        makeSwissTransaction(paySim, step, "BABY_SUPPLIES", babyAmount, "Baby Store");
        
        // Pharmacy
        double pharmacyAmount = 100.0 + random.nextDouble() * 200.0; // 100-300 CHF
        makeSwissTransaction(paySim, step, "PHARMACY_EXPENSES", pharmacyAmount, "Pharmacy");
        
        // Set hasKids to true
        hasKids = true;
        persona = "FAMILY";
    }
    
    // Utility methods
    private void evolvePreferences(MersenneTwisterFast random) {
        // Occasionally change preferences
        if (random.nextDouble() < 0.3) {
            // Change primary grocery store
            String[] groceryStores = {"Migros", "Coop", "Aldi", "Lidl", "Denner", "Volg", "Manor"};
            primaryGroceryStore = groceryStores[random.nextInt(groceryStores.length)];
        }
        
        if (random.nextDouble() < 0.2) {
            // Change favorite coffee shop
            String[] coffeeShops = {"Starbucks", "Café de Paris", "Café Schober", "Café Odeon", "Café Central", "Local Coffee Shop"};
            favoriteCoffeeShop = coffeeShops[random.nextInt(coffeeShops.length)];
        }
    }
}
