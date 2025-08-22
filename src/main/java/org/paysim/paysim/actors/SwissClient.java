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

public class SwissClient extends Client {
    private static final String CLIENT_IDENTIFIER = "SC";
    
    // Swiss-specific demographic attributes
    private String ageGroup; // YOUNG, MIDDLE, SENIOR
    private String incomeLevel; // LOW, MEDIUM, HIGH
    private String location; // URBAN, SUBURBAN, RURAL
    private boolean hasCar;
    private boolean isStudent;

    public SwissClient(PaySim paySim) {
        super(paySim);
        
        // Initialize Swiss-specific attributes
        initializeSwissAttributes(paySim.random);
        
        // Override the name to be more realistic
        setRealisticSwissName(paySim.random);
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
    }

    // Getters for Swiss-specific attributes
    public String getAgeGroup() { return ageGroup; }
    public String getIncomeLevel() { return incomeLevel; }
    public String getLocation() { return location; }
    public boolean hasCar() { return hasCar; }
    public boolean isStudent() { return isStudent; }
    
    /**
     * Get realistic Swiss company name for transaction type
     * Creates recurring patterns for specific clients
     */
    private String getSwissCompanyName(MersenneTwisterFast random, String action) {
        // Use client ID hash to create consistent company preferences
        int clientHash = Math.abs(getName().hashCode());
        
        switch (action) {
            case "FOOD_GROCERIES":
                String[] groceryStores = {"Migros", "Coop", "Aldi", "Lidl", "Denner", "Volg"};
                return groceryStores[clientHash % groceryStores.length];
                
            case "FOOD_RESTAURANT":
                String[] restaurants = {"McDonald's", "Burger King", "Subway", "Pizza Hut", "KFC", "Starbucks"};
                return restaurants[clientHash % restaurants.length];
                
            case "TRANSPORT_PUBLIC":
                String[] publicTransport = {"SBB", "PostAuto", "VBZ", "TPG", "BVB", "Tram"};
                return publicTransport[clientHash % publicTransport.length];
                
            case "TRANSPORT_PRIVATE":
                String[] privateTransport = {"Shell", "BP", "Avia", "Coop Pronto", "Migrol", "Tamoil"};
                return privateTransport[clientHash % privateTransport.length];
                
            case "HEALTHCARE":
                String[] healthcare = {"CSS", "Swisscare", "Helsana", "Concordia", "AXA", "Sanitas"};
                return healthcare[clientHash % healthcare.length];
                
            case "SHOPPING_CLOTHES":
                String[] clothingStores = {"H&M", "Zara", "C&A", "Uniqlo", "Mango", "New Yorker"};
                return clothingStores[clientHash % clothingStores.length];
                
            case "SHOPPING_ELECTRONICS":
                String[] electronicsStores = {"MediaMarkt", "Interdiscount", "Fust", "Brack", "Digitec", "Galaxus"};
                return electronicsStores[clientHash % electronicsStores.length];
                
            case "ENTERTAINMENT":
                String[] entertainment = {"Netflix", "Spotify", "Disney+", "Amazon Prime", "Apple Music", "YouTube Premium"};
                return entertainment[clientHash % entertainment.length];
                
            case "HOUSING":
                String[] housing = {"Swisscom", "EWZ", "GEW", "IWB", "AEW", "Local Utility"};
                return housing[clientHash % housing.length];
                
            case "INSURANCE":
                String[] insurance = {"AXA", "Zurich", "Allianz", "Generali", "Basler", "Helvetia"};
                return insurance[clientHash % insurance.length];
                
            case "EDUCATION":
                String[] education = {"ETH Zurich", "University of Zurich", "University of Basel", "University of Bern", "Online Course", "Language School"};
                return education[clientHash % education.length];
                
            case "TRAVEL":
                String[] travel = {"Swiss", "EasyJet", "Ryanair", "Booking.com", "Airbnb", "Expedia"};
                return travel[clientHash % travel.length];
                
            case "CASH_WITHDRAWAL":
                String[] atms = {"UBS ATM", "Credit Suisse ATM", "PostFinance ATM", "Raiffeisen ATM", "ZKB ATM", "Local Bank ATM"};
                return atms[clientHash % atms.length];
                
            case "CASH_DEPOSIT":
                String[] banks = {"UBS", "Credit Suisse", "PostFinance", "Raiffeisen", "ZKB", "Local Bank"};
                return banks[clientHash % banks.length];
                
            case "BANK_TRANSFER":
                String[] transferBanks = {"UBS Transfer", "Credit Suisse Transfer", "PostFinance Transfer", "Raiffeisen Transfer", "ZKB Transfer", "Local Bank Transfer"};
                return transferBanks[clientHash % transferBanks.length];
                
            case "CREDIT_CARD_PAYMENT":
                String[] creditCards = {"UBS Credit Card", "Credit Suisse Credit Card", "PostFinance Credit Card", "Raiffeisen Credit Card", "ZKB Credit Card", "Local Bank Credit Card"};
                return creditCards[clientHash % creditCards.length];
                
            default:
                return "Swiss Company";
        }
    }
    
    @Override
    public void step(SimState state) {
        PaySim paySim = (PaySim) state;
        int stepTargetCount = paySim.getStepTargetCount();
        if (stepTargetCount > 0) {
            MersenneTwisterFast random = paySim.random;
            int step = (int) state.schedule.getSteps();
            Map<String, Double> stepActionProfile = paySim.getStepProbabilities();

            int count = pickCount(random, stepTargetCount);

            for (int t = 0; t < count; t++) {
                String action = pickSwissAction(random, stepActionProfile);
                StepActionProfile stepAmountProfile = paySim.getStepAction(action);
                double amount = pickSwissAmount(random, action, stepAmountProfile);

                makeSwissTransaction(paySim, step, action, amount);
            }
        }
    }
    
    private String pickSwissAction(MersenneTwisterFast random, Map<String, Double> stepActionProb) {
        // Use Swiss-specific transaction types and probabilities
        String[] swissActions = {
            "FOOD_GROCERIES", "FOOD_RESTAURANT", "TRANSPORT_PUBLIC", "TRANSPORT_PRIVATE",
            "HEALTHCARE", "SHOPPING_CLOTHES", "SHOPPING_ELECTRONICS", "ENTERTAINMENT",
            "HOUSING", "INSURANCE", "EDUCATION", "TRAVEL", "CASH_WITHDRAWAL", 
            "CASH_DEPOSIT", "BANK_TRANSFER", "CREDIT_CARD_PAYMENT"
        };
        
        // Adjust probabilities based on Swiss context
        Map<String, Double> swissProbabilities = new HashMap<>();
        
        // Base probabilities for different transaction types
        swissProbabilities.put("FOOD_GROCERIES", 0.25);
        swissProbabilities.put("FOOD_RESTAURANT", 0.15);
        swissProbabilities.put("TRANSPORT_PUBLIC", 0.12);
        swissProbabilities.put("TRANSPORT_PRIVATE", 0.08);
        swissProbabilities.put("HEALTHCARE", 0.08);
        swissProbabilities.put("SHOPPING_CLOTHES", 0.10);
        swissProbabilities.put("SHOPPING_ELECTRONICS", 0.05);
        swissProbabilities.put("ENTERTAINMENT", 0.08);
        swissProbabilities.put("HOUSING", 0.05);
        swissProbabilities.put("INSURANCE", 0.03);
        swissProbabilities.put("EDUCATION", 0.02);
        swissProbabilities.put("TRAVEL", 0.03);
        swissProbabilities.put("CASH_WITHDRAWAL", 0.02);
        swissProbabilities.put("CASH_DEPOSIT", 0.01);
        swissProbabilities.put("BANK_TRANSFER", 0.01);
        swissProbabilities.put("CREDIT_CARD_PAYMENT", 0.01);
        
        // Adjust based on demographic attributes
        if (ageGroup.equals("YOUNG")) {
            swissProbabilities.put("EDUCATION", 0.08);
            swissProbabilities.put("ENTERTAINMENT", 0.15);
            swissProbabilities.put("SHOPPING_CLOTHES", 0.15);
        } else if (ageGroup.equals("SENIOR")) {
            swissProbabilities.put("HEALTHCARE", 0.15);
            swissProbabilities.put("TRANSPORT_PUBLIC", 0.20);
        }
        
        if (location.equals("RURAL")) {
            swissProbabilities.put("TRANSPORT_PRIVATE", 0.20);
            swissProbabilities.put("FOOD_GROCERIES", 0.30);
        }
        
        if (isStudent) {
            swissProbabilities.put("EDUCATION", 0.20);
            swissProbabilities.put("FOOD_RESTAURANT", 0.25);
        }
        
        // Add recurring pattern logic based on client ID
        int clientHash = Math.abs(getName().hashCode());
        
        // Some clients prefer specific transaction types (creates recurring patterns)
        if (clientHash % 10 == 0) { // 10% of clients prefer entertainment
            swissProbabilities.put("ENTERTAINMENT", 0.25);
            swissProbabilities.put("FOOD_RESTAURANT", 0.20);
        } else if (clientHash % 10 == 1) { // 10% prefer shopping
            swissProbabilities.put("SHOPPING_CLOTHES", 0.25);
            swissProbabilities.put("SHOPPING_ELECTRONICS", 0.15);
        } else if (clientHash % 10 == 2) { // 10% prefer transport
            swissProbabilities.put("TRANSPORT_PUBLIC", 0.25);
            swissProbabilities.put("TRANSPORT_PRIVATE", 0.20);
        } else if (clientHash % 10 == 3) { // 10% prefer healthcare
            swissProbabilities.put("HEALTHCARE", 0.20);
            swissProbabilities.put("INSURANCE", 0.10);
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
            case "FOOD_GROCERIES":
                baseAmount = 45.0; // CHF
                stdDev = 15.0;
                break;
            case "FOOD_RESTAURANT":
                baseAmount = 25.0; // CHF
                stdDev = 12.0;
                break;
            case "TRANSPORT_PUBLIC":
                baseAmount = 8.5; // CHF
                stdDev = 5.0;
                break;
            case "TRANSPORT_PRIVATE":
                baseAmount = 15.0; // CHF
                stdDev = 8.0;
                break;
            case "HEALTHCARE":
                baseAmount = 120.0; // CHF
                stdDev = 80.0;
                break;
            case "SHOPPING_CLOTHES":
                baseAmount = 85.0; // CHF
                stdDev = 45.0;
                break;
            case "SHOPPING_ELECTRONICS":
                baseAmount = 250.0; // CHF
                stdDev = 150.0;
                break;
            case "ENTERTAINMENT":
                baseAmount = 35.0; // CHF
                stdDev = 20.0;
                break;
            case "HOUSING":
                baseAmount = 1800.0; // CHF
                stdDev = 600.0;
                break;
            case "INSURANCE":
                baseAmount = 200.0; // CHF
                stdDev = 100.0;
                break;
            case "EDUCATION":
                baseAmount = 500.0; // CHF
                stdDev = 300.0;
                break;
            case "TRAVEL":
                baseAmount = 150.0; // CHF
                stdDev = 100.0;
                break;
            case "CASH_WITHDRAWAL":
                baseAmount = 100.0; // CHF
                stdDev = 50.0;
                break;
            case "CASH_DEPOSIT":
                baseAmount = 500.0; // CHF
                stdDev = 300.0;
                break;
            case "BANK_TRANSFER":
                baseAmount = 1000.0; // CHF
                stdDev = 500.0;
                break;
            case "CREDIT_CARD_PAYMENT":
                baseAmount = 75.0; // CHF
                stdDev = 40.0;
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
        
        // Generate amount with normal distribution
        double amount = -1;
        while (amount <= 0) {
            amount = random.nextGaussian() * stdDev + baseAmount;
        }
        
        return amount;
    }
    
    private void makeSwissTransaction(PaySim paySim, int step, String action, double amount) {
        // Get realistic Swiss company name for this transaction type
        String swissCompanyName = getSwissCompanyName(paySim.random, action);
        
        String nameOrig = this.getName();
        String nameDest = swissCompanyName; // Use Swiss company name instead of generic merchant
        double oldBalanceOrig = this.getBalance();
        double oldBalanceDest = 0.0; // Swiss companies start with 0 balance
        
        // Withdraw amount from client
        boolean isUnauthorizedOverdraft = this.withdraw(amount);
        
        // For Swiss companies, we don't need to deposit to merchant (simplified)
        double newBalanceOrig = this.getBalance();
        double newBalanceDest = amount; // Company receives the payment
        
        Transaction transaction = new Transaction(step, action, amount, nameOrig, oldBalanceOrig,
                                               newBalanceOrig, nameDest, oldBalanceDest, newBalanceDest);
        
        transaction.setUnauthorizedOverdraft(isUnauthorizedOverdraft);
        transaction.setFraud(false); // No fraud in Swiss spending data
        
        // Add transaction to PaySim
        paySim.getTransactions().add(transaction);
    }
}
