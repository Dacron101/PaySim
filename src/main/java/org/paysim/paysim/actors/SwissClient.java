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
