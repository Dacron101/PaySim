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
        // Create transaction based on Swiss action type
        Merchant merchantTo = paySim.pickRandomMerchant();
        
        String nameOrig = this.getName();
        String nameDest = merchantTo.getName();
        double oldBalanceOrig = this.getBalance();
        double oldBalanceDest = merchantTo.getBalance();
        
        // Withdraw amount from client
        boolean isUnauthorizedOverdraft = this.withdraw(amount);
        
        // Deposit amount to merchant
        merchantTo.deposit(amount);
        
        double newBalanceOrig = this.getBalance();
        double newBalanceDest = merchantTo.getBalance();
        
        Transaction transaction = new Transaction(step, action, amount, nameOrig, oldBalanceOrig,
                                               newBalanceOrig, nameDest, oldBalanceDest, newBalanceDest);
        
        transaction.setUnauthorizedOverdraft(isUnauthorizedOverdraft);
        transaction.setFraud(this.isFraud());
        
        // Add transaction to PaySim
        paySim.getTransactions().add(transaction);
    }
}
