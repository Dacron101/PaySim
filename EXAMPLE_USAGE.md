# Swiss Spending Simulator - Example Usage

This guide shows you how to use the Swiss Spending Simulator to generate realistic spending data for machine learning training.

## Quick Start

### 1. Build and Run the Simulator

```bash
# Make the run script executable (if not already done)
chmod +x run_swiss_simulator.sh

# Run the simulator
./run_swiss_simulator.sh
```

This will:
- Compile the Java project
- Run the simulation for 1000 Swiss people over 365 days
- Generate output files in the `outputs/` directory

### 2. Analyze the Generated Data

```bash
# Install Python dependencies
pip install -r requirements.txt

# Run the analysis script
python analyze_swiss_data.py
```

## What You'll Get

### Transaction Data
The simulator generates realistic Swiss spending data including:

- **Daily transactions** for 1000 people over a full year
- **16 spending categories** (groceries, restaurants, transport, etc.)
- **Realistic amounts** in Swiss Francs (CHF)
- **Demographic variations** based on age, income, location

### Sample Output
```
=== Spending Analysis by Category ===
                    count      mean       std       min       max         sum
FOOD_GROCERIES      45678     48.50     18.20     15.00    180.00  2217411.00
HOUSING             12000   1250.40    450.60    800.00   5000.00 15004800.00
TRANSPORT_PUBLIC    34200      9.20      2.80      3.00     50.00   314640.00
FOOD_RESTAURANT     28900     35.20     18.90     12.00    120.00  1017680.00
...
```

### Visualizations
The analysis script creates:
- Pie chart of spending by category
- Daily spending trends over time
- Transaction amount distributions
- Category frequency analysis

## Customization Examples

### 1. Adjust Spending Patterns

Edit `paramFiles/swissClientsProfiles.csv` to change spending behavior:

```csv
# Increase restaurant spending for young people
FOOD_RESTAURANT,1,1,35.50,15.30,0.50  # Higher frequency (0.50)
FOOD_RESTAURANT,2,2,42.80,18.60,0.30  # Higher amounts
```

### 2. Add Seasonal Variations

Edit `paramFiles/swissAggregatedTransactions.csv` to add seasonal patterns:

```csv
# Summer months (June-August) - more travel and entertainment
180,0.50,0.40,0.55,0.45,0.25,0.35,0.20,0.55,0.05,0.05,0.08,0.15,0.45,0.40,0.25,0.50
210,0.45,0.35,0.50,0.40,0.20,0.30,0.18,0.40,0.05,0.05,0.08,0.15,0.40,0.35,0.20,0.45
240,0.40,0.30,0.45,0.35,0.18,0.25,0.15,0.35,0.05,0.05,0.08,0.15,0.35,0.30,0.18,0.40
```

### 3. Modify Demographics

Edit `src/main/java/org/paysim/paysim/actors/SwissClient.java` to change population characteristics:

```java
// Increase percentage of young people
if (ageRand < 0.35) {  // Changed from 0.25
    ageGroup = "YOUNG";
} else if (ageRand < 0.75) {  // Changed from 0.70
    ageGroup = "MIDDLE";
} else {
    ageGroup = "SENIOR";
}
```

## Machine Learning Use Cases

### 1. Spending Pattern Classification

```python
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split

# Load the generated data
df = pd.read_csv('outputs/SWISS_*/rawLog.csv')

# Create features
features = df.groupby('nameOrig').agg({
    'amount': ['mean', 'std', 'count'],
    'type': lambda x: x.value_counts().to_dict()
}).reset_index()

# Train a classifier to predict income level
# (You would need to add income labels to the data)
```

### 2. Anomaly Detection

```python
from sklearn.ensemble import IsolationForest

# Detect unusual spending patterns
iso_forest = IsolationForest(contamination=0.1)
anomalies = iso_forest.fit_predict(features[['amount_mean', 'amount_std', 'amount_count']])

# Find clients with unusual behavior
anomalous_clients = features[anomalies == -1]
```

### 3. Customer Segmentation

```python
from sklearn.cluster import KMeans

# Cluster clients by spending behavior
kmeans = KMeans(n_clusters=5, random_state=42)
clusters = kmeans.fit_predict(features[['amount_mean', 'amount_std', 'amount_count']])

# Analyze each cluster
for i in range(5):
    cluster_data = features[clusters == i]
    print(f"Cluster {i}: {len(cluster_data)} clients")
    print(f"  Avg spending: CHF {cluster_data['amount']['mean'].mean():.2f}")
```

## Advanced Configuration

### 1. Multiple Simulation Runs

```bash
# Run multiple simulations with different seeds
for seed in 123 456 789; do
    java -cp target/classes org.paysim.paysim.SwissSpendingSimulator \
        -file SwissSpending.properties 1
done
```

### 2. Different Population Sizes

Edit `SwissSpending.properties`:
```properties
nbClients=5000    # Simulate 5000 people instead of 1000
nbSteps=730      # Simulate 2 years instead of 1
```

### 3. Export to Database

Edit `SwissSpending.properties`:
```properties
saveToDB=1
dbUrl=jdbc:postgresql://localhost:5432/swiss_spending
dbUser=your_username
dbPassword=your_password
```

## Troubleshooting

### Common Issues

1. **Build fails**: Ensure Java 8+ and Maven are installed
2. **No output files**: Check that the `outputs/` directory exists and is writable
3. **Analysis script errors**: Install required Python packages with `pip install -r requirements.txt`

### Performance Tuning

- Reduce `nbClients` for faster simulation
- Increase `nbSteps` for longer time periods
- Adjust `multiplier` to scale the simulation

## Next Steps

1. **Run the simulator** with default settings
2. **Analyze the output** using the Python script
3. **Customize parameters** for your specific use case
4. **Train ML models** on the generated data
5. **Extend the simulator** with additional features

The generated data provides a realistic foundation for training machine learning models on Swiss spending behavior, with enough variety and patterns to create robust models for financial analysis and fraud detection.
