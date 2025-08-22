#!/usr/bin/env python3
"""
Swiss Spending Data Analyzer
Analyzes the output from the Swiss Spending Simulator
"""

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os
import glob
from datetime import datetime
import numpy as np

def find_latest_output():
    """Find the most recent output directory"""
    output_dirs = glob.glob("outputs/SWISS_*")
    if not output_dirs:
        return None
    return max(output_dirs, key=os.path.getctime)

def load_transaction_data(output_dir):
    """Load transaction data from the output directory"""
    raw_log_file = os.path.join(output_dir, "rawLog.csv")
    if not os.path.exists(raw_log_file):
        print(f"Raw log file not found: {raw_log_file}")
        return None
    
    # Load the CSV file
    df = pd.read_csv(raw_log_file)
    print(f"Loaded {len(df)} transactions")
    return df

def analyze_spending_patterns(df):
    """Analyze spending patterns by category"""
    print("\n=== Spending Analysis by Category ===")
    
    # Group by transaction type and calculate statistics
    category_stats = df.groupby('type')['amount'].agg([
        'count', 'mean', 'std', 'min', 'max', 'sum'
    ]).round(2)
    
    # Sort by total amount spent
    category_stats = category_stats.sort_values('sum', ascending=False)
    
    print(category_stats)
    
    return category_stats

def analyze_daily_patterns(df):
    """Analyze spending patterns by day"""
    print("\n=== Daily Spending Patterns ===")
    
    # Group by step (day) and calculate daily totals
    daily_spending = df.groupby('step')['amount'].sum()
    
    print(f"Average daily spending: CHF {daily_spending.mean():.2f}")
    print(f"Daily spending std: CHF {daily_spending.std():.2f}")
    print(f"Min daily spending: CHF {daily_spending.min():.2f}")
    print(f"Max daily spending: CHF {daily_spending.max():.2f}")
    
    return daily_spending

def analyze_client_behavior(df):
    """Analyze individual client behavior"""
    print("\n=== Client Behavior Analysis ===")
    
    # Group by client and calculate statistics
    client_stats = df.groupby('nameOrig')['amount'].agg([
        'count', 'sum', 'mean', 'std'
    ]).round(2)
    
    print(f"Number of active clients: {len(client_stats)}")
    print(f"Average transactions per client: {client_stats['count'].mean():.2f}")
    print(f"Average total spending per client: CHF {client_stats['sum'].mean():.2f}")
    print(f"Average transaction amount per client: CHF {client_stats['mean'].mean():.2f}")
    
    return client_stats

def create_visualizations(df, output_dir):
    """Create visualizations of the spending data"""
    print("\n=== Creating Visualizations ===")
    
    # Set up the plotting style
    plt.style.use('seaborn-v0_8')
    sns.set_palette("husl")
    
    # Create a figure with multiple subplots
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    fig.suptitle('Swiss Spending Patterns Analysis', fontsize=16)
    
    # 1. Spending by category (pie chart)
    category_totals = df.groupby('type')['amount'].sum().sort_values(ascending=False)
    axes[0, 0].pie(category_totals.values, labels=category_totals.index, autopct='%1.1f%%')
    axes[0, 0].set_title('Total Spending by Category')
    
    # 2. Daily spending over time
    daily_spending = df.groupby('step')['amount'].sum()
    axes[0, 1].plot(daily_spending.index, daily_spending.values, linewidth=1)
    axes[0, 1].set_title('Daily Total Spending Over Time')
    axes[0, 1].set_xlabel('Day')
    axes[0, 1].set_ylabel('Total Spending (CHF)')
    axes[0, 1].grid(True, alpha=0.3)
    
    # 3. Transaction amount distribution by category
    top_categories = category_totals.head(8).index
    df_top = df[df['type'].isin(top_categories)]
    df_top.boxplot(column='amount', by='type', ax=axes[1, 0])
    axes[1, 0].set_title('Transaction Amount Distribution by Category')
    axes[1, 0].set_xlabel('Category')
    axes[1, 0].set_ylabel('Amount (CHF)')
    axes[1, 0].tick_params(axis='x', rotation=45)
    
    # 4. Transaction frequency by category
    category_counts = df.groupby('type')['amount'].count().sort_values(ascending=False)
    axes[1, 1].bar(range(len(category_counts)), category_counts.values)
    axes[1, 1].set_title('Transaction Frequency by Category')
    axes[1, 1].set_xlabel('Category')
    axes[1, 1].set_ylabel('Number of Transactions')
    axes[1, 1].set_xticks(range(len(category_counts)))
    axes[1, 1].set_xticklabels(category_counts.index, rotation=45, ha='right')
    
    plt.tight_layout()
    
    # Save the plot
    plot_file = os.path.join(output_dir, "spending_analysis.png")
    plt.savefig(plot_file, dpi=300, bbox_inches='tight')
    print(f"Visualization saved to: {plot_file}")
    
    plt.show()

def generate_summary_report(df, output_dir):
    """Generate a comprehensive summary report"""
    print("\n=== Generating Summary Report ===")
    
    report_file = os.path.join(output_dir, "analysis_summary.txt")
    
    with open(report_file, 'w') as f:
        f.write("Swiss Spending Simulator - Analysis Summary\n")
        f.write("=" * 50 + "\n\n")
        f.write(f"Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
        f.write(f"Total transactions: {len(df):,}\n")
        f.write(f"Date range: Day 1 to Day {df['step'].max()}\n")
        f.write(f"Total amount spent: CHF {df['amount'].sum():,.2f}\n\n")
        
        # Category summary
        f.write("SPENDING BY CATEGORY:\n")
        f.write("-" * 30 + "\n")
        category_summary = df.groupby('type')['amount'].agg(['count', 'sum', 'mean']).round(2)
        for category, row in category_summary.iterrows():
            f.write(f"{category:20} | Count: {row['count']:6} | Total: CHF {row['sum']:10,.2f} | Avg: CHF {row['mean']:8.2f}\n")
        
        f.write("\nDAILY PATTERNS:\n")
        f.write("-" * 30 + "\n")
        daily_spending = df.groupby('step')['amount'].sum()
        f.write(f"Average daily spending: CHF {daily_spending.mean():.2f}\n")
        f.write(f"Peak day: Day {daily_spending.idxmax()} (CHF {daily_spending.max():.2f})\n")
        f.write(f"Lowest day: Day {daily_spending.idxmin()} (CHF {daily_spending.min():.2f})\n")
        
        f.write("\nCLIENT BEHAVIOR:\n")
        f.write("-" * 30 + "\n")
        client_stats = df.groupby('nameOrig')['amount'].agg(['count', 'sum'])
        f.write(f"Active clients: {len(client_stats):,}\n")
        f.write(f"Average transactions per client: {client_stats['count'].mean():.2f}\n")
        f.write(f"Average spending per client: CHF {client_stats['sum'].mean():.2f}\n")
        f.write(f"Top spender: CHF {client_stats['sum'].max():.2f}\n")
        f.write(f"Lowest spender: CHF {client_stats['sum'].min():.2f}\n")
    
    print(f"Summary report saved to: {report_file}")

def main():
    """Main analysis function"""
    print("Swiss Spending Data Analyzer")
    print("=" * 40)
    
    # Find the latest output directory
    output_dir = find_latest_output()
    if not output_dir:
        print("No output directory found. Please run the simulator first.")
        return
    
    print(f"Analyzing data from: {output_dir}")
    
    # Load the transaction data
    df = load_transaction_data(output_dir)
    if df is None:
        return
    
    # Perform analysis
    category_stats = analyze_spending_patterns(df)
    daily_patterns = analyze_daily_patterns(df)
    client_behavior = analyze_client_behavior(df)
    
    # Create visualizations
    try:
        create_visualizations(df, output_dir)
    except Exception as e:
        print(f"Warning: Could not create visualizations: {e}")
    
    # Generate summary report
    generate_summary_report(df, output_dir)
    
    print("\nAnalysis complete!")
    print(f"Check {output_dir} for detailed results and visualizations.")

if __name__ == "__main__":
    main()
