#!/bin/bash

echo "Swiss Spending Simulator"
echo "========================"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 8+ first."
    exit 1
fi

echo "Building project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Error: Build failed. Please check the error messages above."
    exit 1
fi

echo "Build successful!"
echo ""
echo "Running Swiss Spending Simulator..."
echo "This will simulate 1000 Swiss people over 365 days."
echo "Output will be saved in the outputs/ directory."
echo ""

# Run the simulator
java -cp target/classes org.paysim.paysim.SwissSpendingSimulator -file SwissSpending.properties 1

echo ""
echo "Simulation complete!"
echo "Check the outputs/ directory for results."
