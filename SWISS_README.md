# Swiss Spending Simulator

This is a modified version of PaySim that generates realistic spending behavior data for Swiss people. It's designed to create synthetic financial transaction data that can be used to train machine learning models for spending pattern analysis, fraud detection, or financial behavior modeling.

## Features

### Swiss-Specific Transaction Types
- **FOOD_GROCERIES**: Migros, Coop, Aldi, Lidl purchases
- **FOOD_RESTAURANT**: Restaurant and café expenses
- **TRANSPORT_PUBLIC**: SBB, local transport costs
- **TRANSPORT_PRIVATE**: Gas, parking, car maintenance
- **HEALTHCARE**: Medical expenses and insurance
- **SHOPPING_CLOTHES**: Clothing store purchases
- **SHOPPING_ELECTRONICS**: Electronics and appliances
- **ENTERTAINMENT**: Movies, events, hobbies
- **HOUSING**: Rent, utilities, maintenance
- **INSURANCE**: Various insurance payments
- **EDUCATION**: Courses, books, training
- **TRAVEL**: Vacation and business trips
- **CASH_WITHDRAWAL**: ATM withdrawals
- **CASH_DEPOSIT**: Cash deposits
- **BANK_TRANSFER**: Internal transfers
- **CREDIT_CARD_PAYMENT**: Credit card payments

### Demographic Modeling
The simulator creates realistic Swiss clients with:
- **Age Groups**: Young (18-35), Middle (36-65), Senior (65+)
- **Income Levels**: Low, Medium, High
- **Locations**: Urban, Suburban, Rural
- **Car Ownership**: Based on location and demographics
- **Student Status**: Affects spending patterns

### Realistic Spending Patterns
- Amounts are in Swiss Francs (CHF)
- Seasonal variations in spending
- Location-based adjustments (urban vs rural)
- Income-based spending behavior
- Age-appropriate transaction patterns

## Configuration

### Properties File
The main configuration is in `SwissSpending.properties`:
- `nbSteps`: 365 (simulates one year)
- `nbClients`: 1000 (number of simulated people)
- `nbMerchants`: 500 (businesses to transact with)
- `nbBanks`: 5 (Swiss banks)

### Parameter Files
All configuration data is in the `paramFiles/` directory:
- `swissTransactionsTypes.csv`: Transaction categories
- `swissClientsProfiles.csv`: Spending behavior profiles
- `swissAggregatedTransactions.csv`: Daily spending patterns
- `swissInitialBalancesDistribution.csv`: Initial bank balances
- `swissOverdraftLimits.csv`: Overdraft limits
- `swissMaxOccurrencesPerClient.csv`: Transaction frequency limits

## Usage

### Running the Simulator
```bash
# Compile the project
mvn compile

# Run the Swiss spending simulator
java -cp target/classes org.paysim.paysim.SwissSpendingSimulator -file SwissSpending.properties 1
```

### Output
The simulator generates:
- Raw transaction logs
- Aggregated spending data
- Client profiles and statistics
- Summary reports

Output files are saved in the `outputs/` directory with timestamps.

## Data Format

### Transaction Records
Each transaction includes:
- Timestamp (step number)
- Client ID
- Merchant ID
- Bank ID
- Transaction type
- Amount (CHF)
- Fraud flag (always false for this simulator)

### Client Profiles
Each client has:
- Unique identifier
- Age group
- Income level
- Location
- Car ownership status
- Student status
- Initial balance
- Overdraft limit

## Customization

### Adding New Transaction Types
1. Add the new type to `swissTransactionsTypes.csv`
2. Update `swissClientsProfiles.csv` with spending patterns
3. Modify `swissAggregatedTransactions.csv` for daily patterns
4. Update `swissMaxOccurrencesPerClient.csv` for frequency limits

### Adjusting Spending Patterns
- Modify amounts in `swissClientsProfiles.csv`
- Adjust daily probabilities in `swissAggregatedTransactions.csv`
- Change demographic distributions in `SwissClient.java`

### Seasonal Variations
The current implementation includes basic seasonal patterns. You can enhance this by:
- Adding more detailed daily patterns
- Including holiday effects
- Adding weather-related spending adjustments

## Use Cases

### Machine Learning Training
- **Spending Pattern Classification**: Categorize users by spending behavior
- **Anomaly Detection**: Identify unusual spending patterns
- **Fraud Detection**: Train models to detect suspicious transactions
- **Customer Segmentation**: Group customers by financial behavior

### Financial Analysis
- **Budget Planning**: Understand typical spending patterns
- **Risk Assessment**: Analyze spending volatility
- **Market Research**: Study consumer behavior in Switzerland

### Research and Development
- **Algorithm Testing**: Test financial algorithms with realistic data
- **System Validation**: Validate financial systems with synthetic data
- **Performance Testing**: Test system scalability with large datasets

## Technical Details

### Architecture
- Built on MASON simulation framework
- Extends the original PaySim architecture
- Modular design for easy customization
- Configurable parameters via CSV files

### Performance
- Simulates 1000 clients over 365 days
- Generates realistic transaction volumes (~300,000+ transactions per year)
- Configurable simulation parameters
- Efficient memory usage
- Fast execution with MASON framework

### Dependencies
- Java 8+
- MASON simulation library
- Maven for build management
- Python 3.7+ (for analysis scripts)
- pandas, matplotlib, seaborn, numpy (for data analysis)

## License
This project is based on PaySim and maintains the same license terms.

## Contributing
Feel free to enhance the simulator by:
- Adding more realistic Swiss spending patterns
- Including additional demographic factors
- Improving seasonal variations
- Adding more transaction types
- Enhancing the output formats
- Adding more sophisticated seasonal patterns
- Including holiday effects on spending
- Adding weather-related spending adjustments
- Implementing more detailed demographic modeling
