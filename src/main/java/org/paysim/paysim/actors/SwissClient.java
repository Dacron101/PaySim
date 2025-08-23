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
    private int lastStreetParadeDay = -1;
    private int lastFasnachtDay = -1;
    private int lastChristmasShoppingDay = -1;
    private int lastBlackFridayShoppingDay = -1;
    private int lastBackToSchoolShoppingDay = -1;
    private int lastMealDay = -1;
    private int lastCreditCardPaymentDay = -1;
    
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
    private int lastHousingBillingDay = -1;
    private int lastUtilitiesBillingDay = -1;
    private int lastCashWithdrawalDay = -1;
    private int lastBankFeeDay = -1;
    private int lastInsuranceDay = -1;
    private int lastShoppingDay = -1;
    
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
    
    // Habit flags - very few people have these habits, but if they do, they're consistent
    private boolean hasMorningCoffeeHabit = false;
    private boolean hasLunchSnackHabit = false;
    private boolean hasCigaretteHabit = false;
    private boolean hasSingleTicketHabit = false;
    private boolean hasAperoHabit = false;
    
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
        // Cognitive biases
        isFollowingHerdBehavior = random.nextDouble() < 0.15; // 15% follow herd behavior
        
        // Forgotten subscriptions (realistic for Swiss)
        if (random.nextDouble() < 0.08) { // 8% have forgotten subscriptions
            String[] possibleSubscriptions = {"Gym Membership", "Magazine", "App Subscription", "Insurance Add-on"};
            int numSubscriptions = 1 + random.nextInt(2); // 1-2 forgotten subscriptions
            for (int i = 0; i < numSubscriptions; i++) {
                forgottenSubscriptions.add(possibleSubscriptions[random.nextInt(possibleSubscriptions.length)]);
            }
        }
        
        // Saving mode (goal-oriented behavior)
        isInSavingMode = random.nextDouble() < 0.12; // 12% are actively saving
        
        // Active loans
        hasActiveLoan = random.nextDouble() < 0.25; // 25% have active loans
        
        // Habit flags - very rare, but consistent if present
        hasMorningCoffeeHabit = random.nextDouble() < 0.005; // 0.5% have morning coffee habit
        hasLunchSnackHabit = random.nextDouble() < 0.004; // 0.4% have lunch snack habit
        hasCigaretteHabit = random.nextDouble() < 0.003; // 0.3% have cigarette habit
        hasSingleTicketHabit = random.nextDouble() < 0.002; // 0.2% have single ticket habit
        hasAperoHabit = random.nextDouble() < 0.006; // 0.6% have apéro habit
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
                
            case "FOOD_DINING_DINNER":
                String[] dinnerPlaces = {"Swiss Restaurant", "Italian Restaurant", "French Restaurant", "Asian Restaurant", "Steakhouse", "Seafood Restaurant", "Pizza Place", "Sushi Restaurant", "Thai Restaurant", "Indian Restaurant", "Mexican Restaurant", "Greek Restaurant", "Turkish Restaurant", "Local Bistro", "Fine Dining", "Wine Bar", "Beer Garden", "Traditional Swiss", "Fondue Restaurant", "Raclette Restaurant"};
                return dinnerPlaces[clientHash % dinnerPlaces.length];
                
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
        
        // 1b. PAYDAY SPLURGE (1-3 days after salary)
        if (shouldHavePaydaySplurge(step, random)) {
            executePaydaySplurge(paySim, step, random);
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
        
        // 5. MEALS (persona and weekday dependent, max 1 lunch + 1 dinner per day)
        if (shouldHaveLunch(step, random)) {
            executeLunch(paySim, step, random);
        }
        
        if (shouldHaveDinner(step, random)) {
            executeDinner(paySim, step, random);
        }
        
        // 5b. MORNING COFFEE (weekdays only)
        if (shouldBuyMorningCoffee(step, random)) {
            executeMorningCoffee(paySim, step, random);
        }
        
        // 5c. LUNCH BREAK SNACK (weekdays only)
        if (shouldBuyLunchSnack(step, random)) {
            executeLunchSnack(paySim, step, random);
        }
        
        // 5d. SINGLE TRANSPORT TICKET (occasional use)
        if (shouldBuySingleTicket(step, random)) {
            executeSingleTicket(paySim, step, random);
        }
        
        // 5e. FRIDAY CIGARETTES (for smokers)
        if (shouldBuyCigarettes(step, random)) {
            executeCigarettePurchase(paySim, step, random);
        }
        
        // 6. MONTHLY SERVICES (with billing jitter)
        if (shouldPayMonthlyServices(step, random)) {
            executeMonthlyServices(paySim, step, random);
        }
        
        // 7. HOUSING (monthly with jitter)
        if (shouldPayHousing(step, random)) {
            executeHousingBilling(paySim, step, random);
        }
        
        // 7b. UTILITIES (monthly with jitter, separate from housing)
        if (shouldPayUtilities(step, random)) {
            executeUtilitiesBilling(paySim, step, random);
        }
        
        // 7c. CASH WITHDRAWAL (monthly)
        if (shouldMakeCashWithdrawal(step, random)) {
            executeCashWithdrawal(paySim, step, random);
        }
        
        // 7d. BANK FEES (monthly)
        if (shouldPayBankFees(step, random)) {
            executeBankFee(paySim, step, random);
        }
        
        // 7e. INSURANCE (monthly, same company)
        if (shouldPayInsurance(step, random)) {
            executeInsurance(paySim, step, random);
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
     * Determine if person should shop for groceries (1-2 times per week, persona-specific)
     */
    private boolean shouldShopForGroceries(int step, MersenneTwisterFast random) {
        int daysSinceLastGrocery = step - lastGroceryDay;
        if (daysSinceLastGrocery < 3) return false; // Minimum 3 days
        
        // Weekend bias for large grocery shopping
        int dayOfWeek = step % 7; // 0-6 for days of week
        boolean isWeekend = dayOfWeek >= 5; // Friday (5) and Saturday (6)
        
        double baseProbability;
        if (persona.equals("FAMILY")) {
            baseProbability = 0.25; // Families shop more frequently
        } else if (persona.equals("STUDENT")) {
            baseProbability = 0.20; // Students shop moderately
        } else {
            baseProbability = 0.22; // Others shop moderately
        }
        
        // Weekend boost for large shopping
        if (isWeekend) {
            baseProbability *= 1.5; // 50% more likely on weekends
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Determine if person should have lunch (weekdays, max 1 per day)
     */
    private boolean shouldHaveLunch(int step, MersenneTwisterFast random) {
        // Only one meal per day (each step = 1 day)
        if (step == lastMealDay) return false;
        
        int dayOfWeek = (step % 7); // 0-6 for days of week
        if (dayOfWeek >= 5) return false; // No lunch on weekends
        
        // Swiss people eat out much less frequently - realistic probabilities
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.08; // ~0.4 times per week (young people are frugal in Switzerland)
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.12; // ~0.6 times per week (middle-aged moderate)
        } else { // SENIOR
            baseProbability = 0.10; // ~0.5 times per week (seniors moderate)
        }
        
        // Income adjustment
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.3; // High income = slightly more restaurant lunches
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.6; // Low income = much fewer restaurant lunches
        }
        
        // Student adjustment (students are very frugal in Switzerland)
        if (isStudent) {
            baseProbability *= 0.4; // Students eat out much less
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute lunch (persona and weekday dependent)
     */
    private void executeLunch(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "FOOD_DINING_LUNCH", null);
        String merchant = getCategorySpecificMerchant(random, "FOOD_DINING_LUNCH", persona);
        makeSwissTransaction(paySim, step, "FOOD_DINING_LUNCH", amount, merchant);
        lastMealDay = step;
    }
    
    /**
     * Determine if person should have dinner (weekdays, max 1 per day)
     */
    private boolean shouldHaveDinner(int step, MersenneTwisterFast random) {
        // Only one meal per day (each step = 1 day)
        if (step == lastMealDay) return false;
        
        int dayOfWeek = (step % 7); // 0-6 for days of week
        if (dayOfWeek >= 5) return false; // No dinner out on weekends
        
        // Swiss people eat out for dinner even less frequently
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.06; // ~0.3 times per week (young people are very frugal)
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.08; // ~0.4 times per week (middle-aged moderate)
        } else { // SENIOR
            baseProbability = 0.06; // ~0.3 times per week (seniors moderate)
        }
        
        // Income adjustment
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.4; // High income = more restaurant dinners
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.5; // Low income = much fewer restaurant dinners
        }
        
        // Student adjustment (students rarely eat out for dinner)
        if (isStudent) {
            baseProbability *= 0.3; // Students eat out for dinner very rarely
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute dinner (persona and weekday dependent)
     */
    private void executeDinner(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "FOOD_DINING_DINNER", null);
        String merchant = getCategorySpecificMerchant(random, "FOOD_DINING_DINNER", persona);
        makeSwissTransaction(paySim, step, "FOOD_DINING_DINNER", amount, merchant);
        lastMealDay = step;
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
     * Determine if person should go shopping (occasional, weekend-biased)
     */
    private boolean shouldGoShopping(int step, MersenneTwisterFast random) {
        int daysSinceLastShopping = step - lastShoppingDay;
        if (daysSinceLastShopping < 7) return false; // Minimum 7 days
        
        // Weekend bias for shopping
        int dayOfWeek = step % 7; // 0-6 for days of week
        boolean isWeekend = dayOfWeek >= 5; // Friday (5) and Saturday (6)
        
        double baseProbability = 0.15; // Base 15% chance per week
        
        // Weekend boost for shopping
        if (isWeekend) {
            baseProbability *= 2.0; // 2x more likely on weekends
        }
        
        // Persona adjustments
        if (persona.equals("YOUNG_PROF")) {
            baseProbability *= 1.3; // Young professionals shop more
        } else if (persona.equals("STUDENT")) {
            baseProbability *= 0.7; // Students shop less
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
        if (daysSinceLastSalary < 30) return false; // Exactly 30 days minimum
        
        // Calculate which month we're in (step 0 = January 1st)
        int month = (step / 30) % 12; // 0-11 for Jan-Dec
        
        // Pay date: exactly on the 25th of each month (no jitter)
        int dayOfMonth = (step % 30) + 1;
        int actualPayDay = 25;
        
        // Only pay once per month, exactly on the 25th, and ensure we haven't paid this month
        boolean isPayDay = dayOfMonth == actualPayDay;
        boolean hasNotPaidThisMonth = (step / 30) != (lastSalaryDay / 30); // Different month
        
        return isPayDay && hasNotPaidThisMonth && daysSinceLastSalary >= 30;
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
        int daysSinceLastHousing = step - lastHousingBillingDay; // Use correct field
        if (daysSinceLastHousing < 28) return false;
        
        // Housing is paid 1st-5th of month (with jitter)
        int dayOfMonth = (step % 30) + 1;
        int jitter = random.nextInt(3) - 1; // -1 to +1 days
        int actualDay = dayOfMonth + jitter;
        
        return actualDay >= 1 && actualDay <= 5;
    }
    
    /**
     * Determine if person should pay utilities (monthly with jitter)
     */
    private boolean shouldPayUtilities(int step, MersenneTwisterFast random) {
        int daysSinceLastUtilities = step - lastUtilitiesBillingDay;
        if (daysSinceLastUtilities < 28) return false;
        
        // Utilities are paid 15th-20th of month (with jitter) - different from housing
        int dayOfMonth = (step % 30) + 1;
        int jitter = random.nextInt(3) - 1; // -1 to +1 days
        int actualDay = dayOfMonth + jitter;
        
        return actualDay >= 15 && actualDay <= 20;
    }
    
    /**
     * Determine if person should buy fuel (car owners only, persona-dependent)
     */
    private boolean shouldBuyFuel(int step, MersenneTwisterFast random) {
        if (!hasCar) return false;
        
        // Reduce fuel frequency for public transport users
        if (persona.equals("URBAN_TRANSPORT")) {
            // Urban transport users rarely use their car - fuel every 3-4 weeks
            int daysSinceLastFuel = step - lastFuelDay;
            if (daysSinceLastFuel < 21) return false; // Minimum 21 days
            return daysSinceLastFuel >= (21 + random.nextInt(7)); // 21-28 days
        } else if (persona.equals("SUBURBAN_CAR")) {
            // Suburban car owners use car more frequently - fuel every 1-2 weeks
            int daysSinceLastFuel = step - lastFuelDay;
            if (daysSinceLastFuel < 7) return false; // Minimum 7 days
            return daysSinceLastFuel >= (7 + random.nextInt(7)); // 7-14 days
        } else {
            // Others: moderate usage - fuel every 2-3 weeks
            int daysSinceLastFuel = step - lastFuelDay;
            if (daysSinceLastFuel < 14) return false; // Minimum 14 days
            return daysSinceLastFuel >= (14 + random.nextInt(7)); // 14-21 days
        }
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
        
        int daysSinceLastCreditCardPayment = step - lastCreditCardPaymentDay;
        if (daysSinceLastCreditCardPayment < 28) return false; // Minimum 28 days
        
        // Not everyone pays credit card every month - make it less frequent
        if (random.nextDouble() > 0.60) return false; // Only 60% chance of paying this month
        
        // Credit card payment around 15th of month
        int dayOfMonth = (step % 30) + 1;
        int jitter = random.nextInt(5) - 2; // -2 to +2 days
        int actualPayDay = 15 + jitter;
        
        // Only pay once per month, around the 15th, and ensure we haven't paid this month
        boolean isPayDay = dayOfMonth >= actualPayDay && dayOfMonth <= actualPayDay + 2;
        boolean hasNotPaidThisMonth = (step / 30) != (lastCreditCardPaymentDay / 30); // Different month
        
        return isPayDay && hasNotPaidThisMonth && daysSinceLastCreditCardPayment >= 28;
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
        // Black Friday happens only once per year
        int daysSinceLastBlackFriday = step - lastBlackFridayShoppingDay;
        if (daysSinceLastBlackFriday < 300) return false; // Minimum 300 days between Black Fridays
        
        // Only allow Black Friday shopping on one specific day in November
        int dayOfYear = step % 365;
        if (dayOfYear < 300 || dayOfYear > 330) return false; // November only
        
        // Black Friday is typically the last Friday of November (around day 320-325)
        int dayOfWeek = step % 7;
        if (dayOfWeek != 5) return false; // Friday only (day 5)
        
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
        // Christmas shopping happens only a few times in December, not every day
        int daysSinceLastChristmasShopping = step - lastChristmasShoppingDay;
        if (daysSinceLastChristmasShopping < 7) return false; // Minimum 7 days between shopping trips
        
        // Age-based Christmas shopping probability (reduced from daily to occasional)
        double baseProbability;
        if (ageGroup.equals("YOUNG")) {
            baseProbability = 0.15; // 15% chance per week (was 55% per day)
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability = 0.20; // 20% chance per week (was 70% per day)
        } else { // SENIOR
            baseProbability = 0.18; // 18% chance per week (was 65% per day)
        }
        
        // Family status adjustment
        if (hasKids) {
            baseProbability *= 1.3; // Families do more Christmas shopping
        }
        
        // Only allow Christmas shopping in December (steps 335-365)
        int dayOfYear = step % 365;
        if (dayOfYear < 335 || dayOfYear > 365) return false;
        
        return random.nextDouble() < baseProbability;
    }
    
    private boolean shouldGoBackToSchoolShopping(int step, MersenneTwisterFast random) {
        // Only families with kids
        if (!hasKids) return false;
        
        // Back-to-school shopping happens only a few times in August-September, not throughout the period
        int daysSinceLastBackToSchool = step - lastBackToSchoolShoppingDay;
        if (daysSinceLastBackToSchool < 7) return false; // Minimum 7 days between shopping trips
        
        // Only allow back-to-school shopping in August-September (steps 210-270)
        int dayOfYear = step % 365;
        if (dayOfYear < 210 || dayOfYear > 270) return false;
        
        // Much lower probability - back-to-school is occasional, not guaranteed
        double baseProbability = 0.08; // 8% chance per week (was 85% per day)
        
        // Age-based adjustments (all ages with kids can do back-to-school shopping)
        if (ageGroup.equals("YOUNG")) {
            baseProbability *= 0.8; // Young parents shop less
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability *= 1.0; // Middle-aged parents (normal)
        } else { // SENIOR
            baseProbability *= 0.7; // Senior parents shop less
        }
        
        // Income adjustment
        if (incomeLevel.equals("LOW")) {
            baseProbability *= 1.2; // Low income families shop more (need deals)
        } else if (incomeLevel.equals("HIGH")) {
            baseProbability *= 0.8; // High income families shop less (can afford year-round)
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    private boolean shouldGoFasnacht(int step, MersenneTwisterFast random) {
        // Fasnacht happens only once per year
        int daysSinceLastFasnacht = step - lastFasnachtDay;
        if (daysSinceLastFasnacht < 300) return false; // Minimum 300 days between Fasnachts
        
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
        // Street Parade happens only once per year
        int daysSinceLastStreetParade = step - lastStreetParadeDay;
        if (daysSinceLastStreetParade < 300) return false; // Minimum 300 days between Street Parades
        
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
        // Savings transfers on 1st and 15th of month, but not everyone saves every time
        int dayOfMonth = (step % 30) + 1;
        int jitter = random.nextInt(3) - 1; // -1 to +1 days
        int actualDay = dayOfMonth + jitter;
        
        // Check if it's a savings day
        boolean isSavingsDay = (actualDay == 1 || actualDay == 15);
        if (!isSavingsDay) return false;
        
        // Not everyone saves every month - make it less frequent
        double baseProbability = 0.40; // Only 40% chance of saving each month
        
        // Age-based savings probability
        if (ageGroup.equals("YOUNG")) {
            baseProbability *= 0.6; // Young people save less (24% chance)
        } else if (ageGroup.equals("MIDDLE")) {
            baseProbability *= 1.0; // Middle-aged save normally (40% chance)
        } else { // SENIOR
            baseProbability *= 1.3; // Seniors save more (52% chance)
        }
        
        // Income-based savings probability
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.2; // High income save more
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.7; // Low income save less
        }
        
        // Persona-based savings probability
        if (persona.equals("STUDENT")) {
            baseProbability *= 0.3; // Students save very little
        } else if (persona.equals("YOUNG_PROF")) {
            baseProbability *= 0.8; // Young professionals save moderately
        } else if (persona.equals("FAMILY_WITH_KIDS")) {
            baseProbability *= 0.6; // Families save less due to expenses
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    private boolean shouldPayPetExpenses(int step, MersenneTwisterFast random) {
        int daysSinceLastPetExpense = step - lastPetExpenseDay;
        if (daysSinceLastPetExpense < 28) return false;
        
        // Monthly pet expenses
        return daysSinceLastPetExpense >= 30;
    }
    
    private boolean shouldPaySerafe(int step, MersenneTwisterFast random) {
        // Serafe is paid annually (every 365 days)
        int daysSinceLastSerafe = step - lastSerafePaymentDay;
        if (daysSinceLastSerafe < 350) return false;
        
        return daysSinceLastSerafe >= 365;
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
    
    /**
     * Determine if person should go for apéro (Thursday/Friday only)
     */
    private boolean shouldGoApero(int step, MersenneTwisterFast random) {
        // Only on Thursday (4) and Friday (5)
        int dayOfWeek = step % 7; // 0-6 for days of week
        if (dayOfWeek != 4 && dayOfWeek != 5) return false; // Thursday = 4, Friday = 5
        
        // Prevent multiple apéros on the same day
        if (step == lastAperoDay) return false;
        
        // If person has the habit, they do it 95% of the time (realistic consistency)
        if (hasAperoHabit) {
            return random.nextDouble() < 0.95; // 95% consistency
        }
        
        // Otherwise, very rare random occurrence (0.1% chance)
        double baseProbability = 0.001;
        
        // Persona adjustments
        if (persona.equals("YOUNG_PROF")) {
            baseProbability *= 1.5; // Young professionals love apéro
        } else if (persona.equals("STUDENT")) {
            baseProbability *= 0.8; // Students go occasionally
        } else if (persona.equals("FAMILY")) {
            baseProbability *= 0.6; // Families go less often
        }
        
        // Age adjustments
        if (ageGroup.equals("YOUNG")) {
            baseProbability *= 1.4; // Young people go more
        } else if (ageGroup.equals("SENIOR")) {
            baseProbability *= 0.7; // Seniors go less
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    private boolean shouldBuyGift(int step, MersenneTwisterFast random) {
        // Gift purchases around birthdays and holidays (reduced frequency)
        int dayOfYear = step % 365;
        
        // Birthday month (2 weeks before and after) - reduced from 30% to 15%
        String currentMonth = getCurrentMonth(dayOfYear);
        if (currentMonth.equals(birthdayMonth)) {
            return random.nextDouble() < 0.15; // 15% chance in birthday month (was 30%)
        }
        
        // Holiday season (November-December) - reduced from 25% to 12%
        if (dayOfYear >= 300 && dayOfYear <= 365) {
            return random.nextDouble() < 0.12; // 12% chance during holidays (was 25%)
        }
        
        // Regular gift giving - reduced from 5% to 2% per day
        return random.nextDouble() < 0.02;
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
        // Determine if this is a small convenience purchase or large weekly shop
        boolean isLargeShop = random.nextDouble() < 0.30; // 30% chance of large shop
        
        String action = "GENERAL_EXPENSES_DAILY";
        double amount;
        String merchant;
        
        if (isLargeShop) {
            // Large weekly shop at major supermarkets
            amount = 80.0 + random.nextDouble() * 120.0; // CHF 80-200 for large shop
            if (random.nextDouble() < 0.60) {
                merchant = "Coop"; // 60% at Coop
            } else {
                merchant = "Migros"; // 40% at Migros
            }
        } else {
            // Small convenience purchase
            amount = 15.0 + random.nextDouble() * 35.0; // CHF 15-50 for small purchase
            if (random.nextDouble() < 0.70) {
                merchant = "Coop Pronto"; // 70% at convenience stores
            } else {
                merchant = "Volg"; // 30% at Volg
            }
        }
        
        makeSwissTransaction(paySim, step, action, amount, merchant);
        lastGroceryDay = step;
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
        String merchant = getCategorySpecificMerchant(random, "HOUSING_GENERAL", persona);
        makeSwissTransaction(paySim, step, "HOUSING_GENERAL", housingAmount, merchant);
        
        lastHousingBillingDay = step;
    }
    
    /**
     * Execute utilities billing (monthly)
     */
    private void executeUtilitiesBilling(PaySim paySim, int step, MersenneTwisterFast random) {
        double utilitiesAmount = pickSwissAmount(random, "UTILITIES_GENERAL", null);
        String merchant = getCategorySpecificMerchant(random, "UTILITIES_GENERAL", persona);
        makeSwissTransaction(paySim, step, "UTILITIES_GENERAL", utilitiesAmount, merchant);
        
        lastUtilitiesBillingDay = step;
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
        String[] shoppingCategories = {"SHOPPING_CLOTHING", "SHOPPING_ELECTRONICS", "SHOPPING_BOOKS", "SHOPPING_JEWELRY"};
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
        lastSalaryDay = step;
    }
    
    private String pickSwissAction(MersenneTwisterFast random, Map<String, Double> stepActionProb) {
        // Use new Main_Categories and Sub_Categories
        String[] swissActions = {
            "INCOME_GENERAL", "HOUSING_GENERAL", "GENERAL_EXPENSES_DAILY", "TRAVEL_GENERAL",
            "OTHER_GENERAL", "SHOPPING_ELECTRONICS", "SHOPPING_BOOKS", "SHOPPING_CLOTHING",
            "HEALTHCARE_GENERAL", "TRANSPORTATION_FUEL", "TRANSPORTATION_PUBLIC", "FOOD_DINING_LUNCH",
            "FOOD_DINING_DINNER", "TAXES_GENERAL", "UTILITIES_GENERAL", "ENTERTAINMENT_STREAMING", "ENTERTAINMENT_MOBILE",
            "MORNING_COFFEE", "LUNCH_SNACK", "CIGARETTE_PURCHASE"
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
        swissProbabilities.put("SHOPPING_JEWELRY", 0.03);
        swissProbabilities.put("HEALTHCARE_GENERAL", 0.08);
        swissProbabilities.put("TRANSPORTATION_FUEL", 0.08);
        swissProbabilities.put("TRANSPORTATION_PUBLIC", 0.12);
        swissProbabilities.put("FOOD_DINING_LUNCH", 0.15);
        swissProbabilities.put("FOOD_DINING_DINNER", 0.12);
        swissProbabilities.put("TAXES_GENERAL", 0.03);
        swissProbabilities.put("UTILITIES_GENERAL", 0.05);
        swissProbabilities.put("ENTERTAINMENT_STREAMING", 0.08);
        swissProbabilities.put("ENTERTAINMENT_MOBILE", 0.05);
        swissProbabilities.put("MORNING_COFFEE", 0.12);
        swissProbabilities.put("LUNCH_SNACK", 0.08);
        swissProbabilities.put("CIGARETTE_PURCHASE", 0.03);
        
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
            swissProbabilities.put("SHOPPING_JEWELRY", 0.08);
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
            case "SHOPPING_JEWELRY":
                baseAmount = 120.0; // CHF (jewelry - luxury items)
                stdDev = 80.0;
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
            case "FOOD_DINING_DINNER":
                baseAmount = 45.0; // CHF (dinner - more expensive than lunch)
                stdDev = 20.0;
                break;
            case "TAXES_GENERAL":
                baseAmount = 800.0; // CHF (taxes)
                stdDev = 400.0;
                break;
            case "UTILITIES_GENERAL":
                baseAmount = 360.0; // CHF (utilities - ~20% of typical rent of 1800 CHF)
                stdDev = 80.0;
                break;
            case "ENTERTAINMENT_STREAMING":
                baseAmount = 25.0; // CHF (streaming subscriptions)
                stdDev = 15.0;
                break;
            case "ENTERTAINMENT_MOBILE":
                baseAmount = 60.0; // CHF (mobile phone)
                stdDev = 30.0;
                break;
            case "MORNING_COFFEE":
                baseAmount = 5.0; // CHF (coffee in Switzerland)
                stdDev = 1.5;
                break;
            case "LUNCH_SNACK":
                baseAmount = 5.5; // CHF (snack in Switzerland)
                stdDev = 2.0;
                break;
            case "CIGARETTE_PURCHASE":
                baseAmount = 15.0; // CHF (cigarette pack in Switzerland)
                stdDev = 3.0;
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
        while (amount <= 0.01) { // Ensure minimum amount of 0.01 CHF
            amount = random.nextGaussian() * stdDev + baseAmount;
        }
        
        // Apply Swiss psychological pricing and category adjustments
        return applySwissPricing(amount, action, random);
    }
    
    private double applySwissPricing(double rawAmount, String action, MersenneTwisterFast random) {
        // Apply Swiss psychological pricing and category-specific adjustments
        double adjustedAmount = rawAmount;
        
        // Category-specific price adjustments
        switch (action) {
            case "SHOPPING_ELECTRONICS":
                // Electronics are expensive in Switzerland, especially Apple
                adjustedAmount *= 1.8; // 80% more expensive than base
                break;
            case "SHOPPING_JEWELRY":
                // Jewelry is luxury, but not as expensive as electronics
                adjustedAmount *= 1.4; // 40% more expensive than base
                break;
            case "SHOPPING_CLOTHING":
                // Clothing is moderately expensive
                adjustedAmount *= 1.2; // 20% more expensive than base
                break;
            case "FOOD_DINING_DINNER":
                // Restaurants are expensive in Switzerland
                adjustedAmount *= 1.3; // 30% more expensive than base
                break;
            case "TRAVEL_GENERAL":
                // Travel is expensive
                adjustedAmount *= 1.5; // 50% more expensive than base
                break;
            case "HEALTHCARE_GENERAL":
                // Healthcare is expensive
                adjustedAmount *= 1.6; // 60% more expensive than base
                break;
            default:
                // Other categories keep base pricing
                break;
        }
        
        // Apply psychological pricing (Swiss love round numbers and .99 endings)
        double psychologicalPricing = random.nextDouble();
        
        if (psychologicalPricing < 0.4) {
            // 40% chance: Round to nearest 10 (e.g., 199.90, 299.00)
            adjustedAmount = Math.round(adjustedAmount / 10.0) * 10.0;
        } else if (psychologicalPricing < 0.7) {
            // 30% chance: .99 ending (e.g., 199.99, 299.99)
            adjustedAmount = Math.floor(adjustedAmount) + 0.99;
        } else if (psychologicalPricing < 0.85) {
            // 15% chance: .90 ending (e.g., 199.90, 299.90)
            adjustedAmount = Math.floor(adjustedAmount) + 0.90;
        } else if (psychologicalPricing < 0.95) {
            // 10% chance: .50 ending (e.g., 199.50, 299.50)
            adjustedAmount = Math.floor(adjustedAmount) + 0.50;
        }
        // 5% chance: Keep original amount (natural pricing)
        
        double finalAmount = Math.round(adjustedAmount * 100.0) / 100.0; // Round to 2 decimal places
        
        // Safety check: ensure amount is always positive
        if (finalAmount <= 0.01) {
            finalAmount = 0.01; // Minimum amount
        }
        
        return finalAmount;
    }
    
    private double applyAppleStorePricing(double rawAmount, MersenneTwisterFast random) {
        // Apple Store has premium pricing in Switzerland
        double appleMultiplier = 2.2; // 120% more expensive than regular electronics
        
        // Apple products are typically more expensive
        double adjustedAmount = rawAmount * appleMultiplier;
        
        // Apple loves psychological pricing even more
        double psychologicalPricing = random.nextDouble();
        
        if (psychologicalPricing < 0.5) {
            // 50% chance: .99 ending (Apple's favorite)
            adjustedAmount = Math.floor(adjustedAmount) + 0.99;
        } else if (psychologicalPricing < 0.8) {
            // 30% chance: Round to nearest 10
            adjustedAmount = Math.round(adjustedAmount / 10.0) * 10.0;
        } else if (psychologicalPricing < 0.9) {
            // 10% chance: .90 ending
            adjustedAmount = Math.floor(adjustedAmount) + 0.90;
        }
        // 10% chance: Keep natural pricing
        
        double finalAmount = Math.round(adjustedAmount * 100.0) / 100.0;
        
        // Safety check: ensure amount is always positive
        if (finalAmount <= 0.01) {
            finalAmount = 0.01; // Minimum amount
        }
        
        return finalAmount;
    }
    
    private void makeSwissTransaction(PaySim paySim, int step, String action, double amount) {
        makeSwissTransaction(paySim, step, action, amount, null);
    }
    
    private void makeSwissTransaction(PaySim paySim, int step, String action, double amount, String companyName) {
        // Use provided company name or get realistic Swiss company name
        String swissCompanyName = (companyName != null) ? companyName : getSwissCompanyName(paySim.random, action);
        
        // Apply Apple Store premium pricing if applicable
        double finalAmount = amount;
        if (swissCompanyName.contains("Apple Store") || swissCompanyName.contains("Apple")) {
            finalAmount = applyAppleStorePricing(amount, paySim.random);
        }
        
        // Final safety check: ensure amount is always positive
        if (finalAmount <= 0.00) {
            finalAmount = 0.01; // Minimum amount
        }
        
        // Create transaction with Person ID in the name
        String nameOrig = this.getPersonId() + "_" + this.getName();
        String nameDest = swissCompanyName;
        double oldBalanceOrig = this.getBalance();
        double oldBalanceDest = 0.0;
        
        // Determine if this is income or expense
        boolean isIncome = action.startsWith("INCOME_") || action.equals("BANK_TRANSFER");
        boolean isUnauthorizedOverdraft = false;
        
        if (isIncome) {
            // For income: add amount to balance
            this.deposit(finalAmount);
        } else {
            // For expenses: withdraw amount from balance
            isUnauthorizedOverdraft = this.withdraw(finalAmount);
        }
        
        double newBalanceOrig = this.getBalance();
        double newBalanceDest = finalAmount;
        
        Transaction transaction = new Transaction(step, action, finalAmount, nameOrig, oldBalanceOrig,
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
        lastBlackFridayShoppingDay = step;
    }
    
    private void executeChristmasShopping(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "SHOPPING_CLOTHING", null) * 2.0; // Christmas gifts are expensive
        String[] stores = {"Manor", "Jelmoli", "Globus", "H&M", "Zara", "Online Store"};
        String store = stores[random.nextInt(stores.length)];
        makeSwissTransaction(paySim, step, "SHOPPING_CLOTHING", amount, store + " Christmas");
        lastChristmasShoppingDay = step;
    }
    
    private void executeBackToSchoolShopping(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "SHOPPING_CLOTHING", null) * 1.3; // School supplies and clothes
        String[] stores = {"Coop", "Migros", "Manor", "H&M", "C&A", "School Supply Store"};
        String store = stores[random.nextInt(stores.length)];
        makeSwissTransaction(paySim, step, "SHOPPING_CLOTHING", amount, store + " Back to School");
        lastBackToSchoolShoppingDay = step;
    }
    
    private void executeFasnacht(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "ENTERTAINMENT_GENERAL", null) * 1.5; // Costumes and drinks
        makeSwissTransaction(paySim, step, "ENTERTAINMENT_GENERAL", amount, "Fasnacht Festival");
        lastFasnachtDay = step;
    }
    
    private void executeStreetParade(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "ENTERTAINMENT_GENERAL", null) * 1.2; // Transport and drinks
        makeSwissTransaction(paySim, step, "ENTERTAINMENT_GENERAL", amount, "Street Parade Zurich");
        lastStreetParadeDay = step;
    }
    
    // Additional execution methods for new features
    private void execute13thSalary(PaySim paySim, int step, MersenneTwisterFast random) {
        double amount = pickSwissAmount(random, "INCOME_GENERAL", null) * 0.8; // 13th salary is usually 80% of monthly
        makeSwissTransaction(paySim, step, "INCOME_GENERAL", amount, preferredBank + " 13th Salary");
        last13thSalaryDay = step;
    }
    
    private void executeTransport(PaySim paySim, int step, MersenneTwisterFast random) {
        if (persona.equals("URBAN_TRANSPORT")) {
            // Urban transport users: prefer monthly passes (80% chance) over single tickets (20% chance)
            if (random.nextDouble() < 0.80) {
                // Monthly pass (every 30 days)
                if (step % 30 == 0) {
                    double amount = pickSwissAmount(random, "TRANSPORTATION_PUBLIC", null) * 25; // Monthly pass
                    makeSwissTransaction(paySim, step, "TRANSPORTATION_PUBLIC", amount, preferredTransportService + " Monthly Pass");
                    lastTransportBillingDay = step;
                }
            } else {
                // Single ticket (reduced frequency)
                double amount = pickSwissAmount(random, "TRANSPORTATION_PUBLIC", null);
                makeSwissTransaction(paySim, step, "TRANSPORTATION_PUBLIC", amount, preferredTransportService);
            }
        } else {
            // Monthly pass for others
            if (step % 30 == 0) {
                double amount = pickSwissAmount(random, "TRANSPORTATION_PUBLIC", null) * 25; // Monthly pass
                makeSwissTransaction(paySim, step, "TRANSPORTATION_PUBLIC", amount, preferredTransportService + " Monthly Pass");
                lastTransportBillingDay = step;
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
        lastCreditCardPaymentDay = step;
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
    
    /**
     * Get category-specific merchant based on persona and realistic Swiss preferences
     */
    private String getCategorySpecificMerchant(MersenneTwisterFast random, String action, String persona) {
        double randomValue = random.nextDouble();
        
        switch (action) {
            case "FOOD_DINING_LUNCH":
                if (persona.equals("STUDENT")) {
                    // Students prefer budget options
                    if (randomValue < 0.50) return "Migros Restaurant";
                    else if (randomValue < 0.80) return "Coop Restaurant";
                    else if (randomValue < 0.90) return "Local Café";
                    else return "Food Truck";
                } else if (persona.equals("YOUNG_PROF")) {
                    // Young professionals mix of casual and quality
                    if (randomValue < 0.30) return "Starbucks";
                    else if (randomValue < 0.60) return "Local Café";
                    else if (randomValue < 0.80) return "Migros Restaurant";
                    else return "Coop Restaurant";
                } else {
                    // Others prefer local options
                    if (randomValue < 0.40) return "Local Café";
                    else if (randomValue < 0.70) return "Migros Restaurant";
                    else if (randomValue < 0.85) return "Coop Restaurant";
                    else return "Starbucks";
                }
                
            case "FOOD_DINING_DINNER":
                if (persona.equals("YOUNG_PROF") || persona.equals("FAMILY")) {
                    // Higher-end dining for young professionals and families
                    if (randomValue < 0.25) return "Restaurant zum Kropf";
                    else if (randomValue < 0.45) return "Italian Restaurant";
                    else if (randomValue < 0.65) return "Swiss Restaurant";
                    else if (randomValue < 0.80) return "Asian Restaurant";
                    else return "Steakhouse";
                } else {
                    // More casual for others
                    if (randomValue < 0.40) return "Italian Restaurant";
                    else if (randomValue < 0.65) return "Swiss Restaurant";
                    else if (randomValue < 0.80) return "Pizza Place";
                    else return "Local Bistro";
                }
                
            case "GENERAL_EXPENSES_DAILY":
                // Grocery shopping with realistic store preferences
                if (randomValue < 0.05) return "Volg"; // Small convenience purchases
                else if (randomValue < 0.35) return "Coop Pronto"; // Medium convenience
                else if (randomValue < 0.65) return "Coop"; // Major supermarket
                else if (randomValue < 0.85) return "Migros"; // Major supermarket
                else return "Aldi"; // Budget supermarket
                
            case "SHOPPING_ELECTRONICS":
                if (randomValue < 0.30) return "Digitec";
                else if (randomValue < 0.55) return "MediaMarkt";
                else if (randomValue < 0.75) return "Interdiscount";
                else if (randomValue < 0.90) return "Apple Store";
                else return "Conrad";
                
            case "SHOPPING_CLOTHING":
                if (randomValue < 0.25) return "H&M";
                else if (randomValue < 0.45) return "Zalando";
                else if (randomValue < 0.65) return "C&A";
                else if (randomValue < 0.80) return "Manor";
                else return "Orell Füssli";
                
            case "SHOPPING_JEWELRY":
                if (randomValue < 0.40) return "Christ";
                else if (randomValue < 0.70) return "Bucherer";
                else if (randomValue < 0.85) return "Gübelin";
                else return "Swiss Company";
                
            case "UTILITIES_GENERAL":
                if (randomValue < 0.25) return "Swisscom";
                else if (randomValue < 0.45) return "EWZ";
                else if (randomValue < 0.65) return "BKW";
                else if (randomValue < 0.80) return "IWB";
                else return "Local Utility";
                
            case "HOUSING_GENERAL":
                if (randomValue < 0.30) return "Livit AG";
                else if (randomValue < 0.55) return "Wincasa";
                else if (randomValue < 0.75) return "Mobimo";
                else if (randomValue < 0.90) return "Swiss Prime Site";
                else return "Local Property Management";
                
            case "GIFT_PURCHASE":
                if (randomValue < 0.30) return "Galaxus";
                else if (randomValue < 0.55) return "Zalando";
                else if (randomValue < 0.75) return "Orell Füssli";
                else if (randomValue < 0.90) return "Manor";
                else return "Online Gift Store";
                
            default:
                return getSwissCompanyName(random, action);
        }
    }
    
    /**
     * Determine if person should have a payday splurge (1-3 days after salary)
     */
    private boolean shouldHavePaydaySplurge(int step, MersenneTwisterFast random) {
        int daysSinceSalary = step - lastSalaryDay;
        if (daysSinceSalary < 1 || daysSinceSalary > 3) return false; // Only 1-3 days after salary
        
        // 40% chance of payday splurge
        double baseProbability = 0.40;
        
        // Persona adjustments
        if (persona.equals("YOUNG_PROF")) {
            baseProbability *= 1.5; // Young professionals splurge more
        } else if (persona.equals("STUDENT")) {
            baseProbability *= 0.3; // Students rarely splurge
        } else if (persona.equals("FAMILY")) {
            baseProbability *= 0.8; // Families splurge less
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Determine if person should make a cash withdrawal (monthly)
     */
    private boolean shouldMakeCashWithdrawal(int step, MersenneTwisterFast random) {
        int daysSinceLastWithdrawal = step - lastCashWithdrawalDay;
        if (daysSinceLastWithdrawal < 20) return false; // Minimum 20 days
        
        // 60% chance of monthly withdrawal
        double baseProbability = 0.60;
        
        // Persona adjustments
        if (persona.equals("YOUNG_PROF")) {
            baseProbability *= 0.8; // Young professionals use less cash
        } else if (persona.equals("SENIOR")) {
            baseProbability *= 1.3; // Seniors use more cash
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute cash withdrawal (monthly)
     */
    private void executeCashWithdrawal(PaySim paySim, int step, MersenneTwisterFast random) {
        // Standard withdrawal amounts: CHF 100 or 200
        double amount = (random.nextDouble() < 0.7) ? 100.0 : 200.0;
        
        // Use Swiss bank names for ATMs
        String[] swissBanks = {"UBS ATM", "Credit Suisse ATM", "PostFinance ATM", "Raiffeisen ATM", "ZKB ATM"};
        String atm = swissBanks[random.nextInt(swissBanks.length)];
        
        makeSwissTransaction(paySim, step, "CASH_WITHDRAWAL", amount, atm);
        lastCashWithdrawalDay = step;
    }
    
    /**
     * Determine if person should pay bank fees (monthly)
     */
    private boolean shouldPayBankFees(int step, MersenneTwisterFast random) {
        int daysSinceLastBankFee = step - lastBankFeeDay;
        if (daysSinceLastBankFee < 28) return false; // Minimum 28 days
        
        // 90% chance of monthly bank fee
        double baseProbability = 0.90;
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute bank fee payment (monthly)
     */
    private void executeBankFee(PaySim paySim, int step, MersenneTwisterFast random) {
        // Standard bank fee amounts: CHF 5-15
        double amount = 5.0 + random.nextDouble() * 10.0; // CHF 5-15
        
        // Use the person's preferred bank
        String bankName = preferredBank + " Bank Fee";
        
        makeSwissTransaction(paySim, step, "BANK_FEE", amount, bankName);
        lastBankFeeDay = step;
    }
    
    /**
     * Determine if person should pay insurance (monthly, same company)
     */
    private boolean shouldPayInsurance(int step, MersenneTwisterFast random) {
        int daysSinceLastInsurance = step - lastInsuranceDay;
        if (daysSinceLastInsurance < 28) return false; // Minimum 28 days
        
        // 95% chance of monthly insurance payment
        double baseProbability = 0.95;
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute insurance payment (monthly, same company)
     */
    private void executeInsurance(PaySim paySim, int step, MersenneTwisterFast random) {
        // Insurance amount based on persona and age
        double baseAmount = 150.0; // Base CHF 150
        
        if (persona.equals("YOUNG_PROF")) {
            baseAmount *= 0.8; // Young professionals pay less
        } else if (persona.equals("FAMILY")) {
            baseAmount *= 1.3; // Families pay more
        } else if (persona.equals("SENIOR")) {
            baseAmount *= 1.2; // Seniors pay more
        }
        
        // Add some variation
        double amount = baseAmount + random.nextDouble() * 50.0;
        
        // Use the person's health insurance provider consistently
        String insuranceCompany = healthInsuranceProvider + " Insurance";
        
        makeSwissTransaction(paySim, step, "INSURANCE", amount, insuranceCompany);
        lastInsuranceDay = step;
    }
    
    /**
     * Determine if person should buy morning coffee (weekdays only)
     */
    private boolean shouldBuyMorningCoffee(int step, MersenneTwisterFast random) {
        // Only on weekdays (Monday to Friday)
        int dayOfWeek = step % 7; // 0-6 for days of week
        if (dayOfWeek >= 5) return false; // No coffee on weekends
        
        // If person has the habit, they do it 95% of the time (realistic consistency)
        if (hasMorningCoffeeHabit) {
            return random.nextDouble() < 0.95; // 95% consistency
        }
        
        // Otherwise, very rare random occurrence (0.1% chance)
        double baseProbability = 0.001;
        
        // Persona adjustments
        if (persona.equals("YOUNG_PROF")) {
            baseProbability *= 1.3; // Young professionals drink more coffee
        } else if (persona.equals("STUDENT")) {
            baseProbability *= 0.7; // Students are more frugal
        } else if (persona.equals("SENIOR")) {
            baseProbability *= 0.8; // Seniors drink less coffee
        }
        
        // Income adjustments
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.2; // High income = more coffee
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.6; // Low income = less coffee
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute morning coffee purchase (weekdays only)
     */
    private void executeMorningCoffee(PaySim paySim, int step, MersenneTwisterFast random) {
        // Small coffee amounts: CHF 3.50 - 6.50
        double amount = 3.50 + random.nextDouble() * 3.0; // CHF 3.50-6.50
        
        // Swiss coffee chains and local cafes
        String[] coffeePlaces = {"Starbucks", "Migros Coffee", "Coop Coffee", "Local Café", "Bakery", "Kiosk"};
        String coffeePlace = coffeePlaces[random.nextInt(coffeePlaces.length)];
        
        makeSwissTransaction(paySim, step, "MORNING_COFFEE", amount, coffeePlace);
    }
    
    /**
     * Determine if person should buy cigarettes (Fridays only, for smokers)
     */
    private boolean shouldBuyCigarettes(int step, MersenneTwisterFast random) {
        // Only on Fridays
        int dayOfWeek = step % 7; // 0-6 for days of week
        if (dayOfWeek != 4) return false; // Friday = 4
        
        // If person has the habit, they do it 95% of the time (realistic consistency)
        if (hasCigaretteHabit) {
            return random.nextDouble() < 0.95; // 95% consistency
        }
        
        // Otherwise, very rare random occurrence (0.05% chance)
        double baseProbability = 0.0005;
        
        // Persona adjustments
        if (persona.equals("YOUNG_PROF")) {
            baseProbability *= 0.8; // Young professionals smoke less
        } else if (persona.equals("STUDENT")) {
            baseProbability *= 1.2; // Students smoke more
        } else if (persona.equals("SENIOR")) {
            baseProbability *= 1.1; // Seniors smoke moderately
        }
        
        // Age adjustments
        if (ageGroup.equals("YOUNG")) {
            baseProbability *= 1.3; // Young people smoke more
        } else if (ageGroup.equals("SENIOR")) {
            baseProbability *= 0.7; // Seniors smoke less
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute cigarette purchase (Fridays only)
     */
    private void executeCigarettePurchase(PaySim paySim, int step, MersenneTwisterFast random) {
        // Cigarette pack prices in Switzerland: CHF 12-18
        double amount = 12.0 + random.nextDouble() * 6.0; // CHF 12-18
        
        // Swiss gas stations and convenience stores
        String[] cigarettePlaces = {"Shell", "Esso", "BP", "Migrol", "Coop Pronto", "Avia", "Kiosk"};
        String cigarettePlace = cigarettePlaces[random.nextInt(cigarettePlaces.length)];
        
        makeSwissTransaction(paySim, step, "CIGARETTE_PURCHASE", amount, cigarettePlace);
    }
    
    /**
     * Determine if person should buy lunch break snack (weekdays only)
     */
    private boolean shouldBuyLunchSnack(int step, MersenneTwisterFast random) {
        // Only on weekdays (Monday to Friday)
        int dayOfWeek = step % 7; // 0-6 for days of week
        if (dayOfWeek >= 5) return false; // No snacks on weekends
        
        // If person has the habit, they do it 95% of the time (realistic consistency)
        if (hasLunchSnackHabit) {
            return random.nextDouble() < 0.95; // 95% consistency
        }
        
        // Otherwise, very rare random occurrence (0.08% chance)
        double baseProbability = 0.0008;
        
        // Persona adjustments
        if (persona.equals("YOUNG_PROF")) {
            baseProbability *= 1.2; // Young professionals buy more snacks
        } else if (persona.equals("STUDENT")) {
            baseProbability *= 0.6; // Students are more frugal
        } else if (persona.equals("FAMILY")) {
            baseProbability *= 0.8; // Families bring lunch more often
        }
        
        // Income adjustments
        if (incomeLevel.equals("HIGH")) {
            baseProbability *= 1.3; // High income = more snacks
        } else if (incomeLevel.equals("LOW")) {
            baseProbability *= 0.5; // Low income = fewer snacks
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute lunch break snack purchase (weekdays only)
     */
    private void executeLunchSnack(PaySim paySim, int step, MersenneTwisterFast random) {
        // Small snack amounts: CHF 2.50 - 8.50
        double amount = 2.50 + random.nextDouble() * 6.0; // CHF 2.50-8.50
        
        // Swiss convenience stores and bakeries
        String[] snackPlaces = {"Migros", "Coop", "Bakery", "Kiosk", "Local Café", "Convenience Store"};
        String snackPlace = snackPlaces[random.nextInt(snackPlaces.length)];
        
        makeSwissTransaction(paySim, step, "LUNCH_SNACK", amount, snackPlace);
    }
    
    /**
     * Determine if person should buy single public transport ticket (occasional use)
     */
    private boolean shouldBuySingleTicket(int step, MersenneTwisterFast random) {
        // Only on weekdays (Monday to Friday)
        int dayOfWeek = step % 7; // 0-6 for days of week
        if (dayOfWeek >= 5) return false; // No single tickets on weekends
        
        // If person has the habit, they do it 95% of the time (realistic consistency)
        if (hasSingleTicketHabit) {
            return random.nextDouble() < 0.95; // 95% consistency
        }
        
        // Otherwise, very rare random occurrence (0.06% chance)
        double baseProbability = 0.0006;
        
        // Persona adjustments
        if (persona.equals("URBAN_TRANSPORT")) {
            baseProbability *= 0.3; // Urban transport users have monthly passes
        } else if (persona.equals("SUBURBAN_CAR")) {
            baseProbability *= 1.5; // Suburban car owners buy more single tickets
        } else if (persona.equals("STUDENT")) {
            baseProbability *= 1.2; // Students buy single tickets occasionally
        }
        
        // Income adjustments
        if (incomeLevel.equals("LOW")) {
            baseProbability *= 1.3; // Low income = more single tickets
        } else if (incomeLevel.equals("HIGH")) {
            baseProbability *= 0.7; // High income = fewer single tickets
        }
        
        return random.nextDouble() < baseProbability;
    }
    
    /**
     * Execute single public transport ticket purchase (occasional use)
     */
    private void executeSingleTicket(PaySim paySim, int step, MersenneTwisterFast random) {
        // Single ticket prices in Switzerland: CHF 2.50 - 8.50
        double amount = 2.50 + random.nextDouble() * 6.0; // CHF 2.50-8.50
        
        // Swiss public transport providers
        String[] transportProviders = {"SBB", "VBZ", "TPG", "BVB", "PostAuto", "Tram", "Bus"};
        String provider = transportProviders[random.nextInt(transportProviders.length)];
        
        makeSwissTransaction(paySim, step, "TRANSPORTATION_PUBLIC", amount, provider);
    }
}
