package org.paysim.paysim.actors;

import ec.util.MersenneTwisterFast;

import org.paysim.paysim.PaySim;

import org.paysim.paysim.parameters.BalancesClients;

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
}
