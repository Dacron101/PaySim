## Project Leader

Dr. Edgar Lopez-Rojas
http://edgarlopez.net 

Dataset sample: https://www.kaggle.com/ealaxi/paysim1

## Description

PaySim, a Mobile Money Payment Simulator The Mobile Money Payment Simulation case study is based on a real company that has developed a mobile money implementation that provides mobile phone users with the ability to transfer money between themselves using the phone as a sort of electronic wallet. The task at hand is to develop an approach that detects suspicious activities that are indicative of fraud. Unfortunately, during the initial part of our research this service was only been running in a demo mode. This prevented us from collecting any data that could had been used for analysis of possible detection methods. The development of PaySim covers two phases. During the first phase, we modelled and implemented a MABS that used the schema of the real mobile money service and generated synthetic data following scenarios that were based on predictions of what could be possible when the real system starts operating. During the second phase we got access to transactional financial logs of the system and developed a new version of the simulator which uses aggregated transactional data to generate financial information more alike the original source. Kaggle has featured PaySim1 as dataset of the week of april 2018. See the full article: http://blog.kaggle.com/2017/05/01/datasets-of-the-week-april-2017/ 

## PaySim first paper of the simulator:

Please refer to this dataset using the following citations:

E. A. Lopez-Rojas , A. Elmir, and S. Axelsson. "PaySim: A financial mobile money simulator for fraud detection". In: The 28th European Modeling and Simulation Symposium-EMSS, Larnaca, Cyprus. 2016


## Acknowledgements
This work is part of the research project ”Scalable resource-efficient systems for big data analytics” funded by the Knowledge Foundation (grant: 20140032) in Sweden.

Master's thesis: Elmir A. PaySim Financial Simulator : PaySim Financial Simulator [Internet] [Dissertation]. 2016. Available from: http://urn.kb.se/resolve?urn=urn:nbn:se:bth-14061

2016 PhD Thesis Dr. Edgar Lopez-Rojas
http://bth.diva-portal.org/smash/record.jsf?pid=diva2%3A955852&dswid=-1552

2019 Contribution by Camille Barneaud (https://github.com/gadcam) and the company Flaminem (https://www.flaminem.com/) implementation of Money Laundering cases

---

## Swiss Spending Simulator

### Overview
The Swiss Spending Simulator is an enhanced version of PaySim specifically designed to generate realistic Swiss financial transaction data for AI/ML training purposes. It simulates realistic spending patterns, personal preferences, and Swiss-specific behaviors without fraud data.

### Key Features

#### 1. Person ID System
- **Unique Identifier**: Each person gets a unique ID (e.g., `P66275862_675_Markus Widmer`)
- **Pattern Tracking**: Individual spending patterns are tracked and maintained
- **Personal Preferences**: Deterministic preferences based on Person ID hash

#### 2. Realistic Spending Patterns

##### Income & Pay Cycles
- **Monthly Salary**: Around end-of-month (25th-30th)
- **13th Salary**: Optional bonus in November/December for subset of people
- **Income Variation**: Based on age group, location, and profession

##### Weekly Rhythms
- **Groceries**: 1-2 times per week (Coop, Migros, Denner, Aldi, Lidl)
- **Restaurants**: 1-3 times per week (lunch/dinner peaks)
- **Public Transport**: Daily weekdays OR monthly pass
- **Fuel**: Weekly or every ~10 days (car owners only)

##### Monthly Bills
- **Rent/Mortgage**: Once per month (1st-5th of month)
- **Utilities**: Once per month or quarterly
- **Phone/Internet**: Monthly billing
- **Subscriptions**: Spotify, Netflix, Disney+, Gym, etc.

##### Seasonal Patterns
- **Ski Trips**: December-March
- **Summer Holidays**: July-August
- **Shopping Spikes**: November (Black Friday) & December (Christmas)
- **Back-to-School**: August/September clothing purchases
- **Swiss Events**: Fasnacht (Feb/Mar), Street Parade Zurich (August)

#### 3. Personas & Demographics

##### Urban Public Transport User
- Daily public transport usage
- Higher restaurant spending
- Lower car-related expenses

##### Suburban Car Owner
- Weekly fuel purchases
- Monthly parking/road tolls
- Higher transport costs

##### Student
- Low income, small frequent spends
- High public transport usage
- Limited restaurant spending

##### Young Professional
- High restaurant and e-commerce spending
- Gym memberships
- Travel and entertainment

##### Family with Kids
- Large grocery baskets
- Pharmacy expenses
- Family travel packages

##### Remote Worker
- Fewer transport expenses
- Higher utility costs
- Home office expenses

#### 4. Category Patterns

##### Groceries
- **Frequency**: 1-2 times per week
- **Pattern**: Mixture of small + occasional large baskets
- **Merchants**: Coop, Migros, Denner, Aldi, Lidl, Volg, Manor

##### Restaurants
- **Pattern**: Lunch/dinner peaks, coffee micro-payments
- **Frequency**: 1-3 times per week
- **Types**: Fast food, casual dining, coffee shops

##### Transport
- **Options**: Daily tickets OR monthly pass
- **Providers**: SBB, PostAuto, VBZ, TPG, BVB, Tram
- **Variations**: Public transport vs. car ownership

##### E-commerce
- **Timing**: Clusters around November, January, evenings
- **Categories**: Electronics, clothing, books, home goods
- **Merchants**: Digitec, MediaMarkt, Orell Füssli, online retailers

##### Travel
- **Frequency**: 2 times per year
- **Bundles**: Flights, hotels, restaurants, attractions
- **Seasonal**: Ski trips (winter), beach holidays (summer)

#### 5. Swiss-Specific Features

##### Merchants & Services
- **Groceries**: Coop, Migros, Denner, Aldi, Lidl, Manor, Volg
- **Electronics**: Interdiscount, MediaMarkt, Digitec
- **Transport**: SBB, VBZ, TPG, BVB, PostAuto
- **Telecom**: Swisscom, Sunrise, Salt
- **Banks**: UBS, Credit Suisse, PostFinance, Raiffeisen, ZKB
- **Airlines**: SWISS, easyJet
- **Ski Resorts**: Zermatt, St. Moritz, Davos

##### Subscriptions
- **Streaming**: Swisscom TV, Blue+, Netflix, Spotify
- **Fitness**: NonStop Gym, Kieser, Fitness First
- **News**: NZZ, Tages-Anzeiger, Blick

#### 6. Data Quality Features

##### Billing Jitter
- **Variation**: ±1-2 days for monthly bills
- **Realistic**: Accounts for processing delays

##### Churn & Changes
- **Occasional**: Service cancellations and re-subscriptions
- **Life Events**: Moving, job changes, family changes

##### Noise & Anomalies
- **Refunds**: ~1-2% negative transactions
- **Double Charges**: Followed by reversals
- **Outlier Purchases**: High-value items
- **Merchant Aliases**: "Migros Sihlcity", "Migros Schlieren"
- **FX Transactions**: Rare international purchases

##### Balance Dynamics
- **Initial Balance**: Income-based starting amounts
- **Monthly Credits**: Salary deposits
- **Overdrafts**: Occasional negative balances flagged
- **Credit Card**: Monthly payoff cycles

#### 7. Statistical Distributions

##### Event Timing
- **Inhomogeneous Poisson**: Weekday/hour bias
- **Jitter**: Delays, weekend shifts

##### Category Mix
- **Dirichlet Distribution**: Per-person category preferences
- **Random α**: Different α per person (not constant)

##### Amount Distributions
- **Lognormal/Gamma**: Base amounts
- **Mixtures**: Big vs. small basket distributions
- **Merchant Preferences**: Zipf/Dirichlet-multinomial (few favorites + long tail)

### Output Format
- **Person ID**: Unique identifier for each person
- **Transaction Categories**: Main_Category_Sub_Category format
- **Realistic Amounts**: Swiss Franc amounts with proper distributions
- **Company Names**: Real Swiss company names
- **Timing**: Realistic daily/weekly/monthly patterns
- **No Fraud**: Clean spending data for ML training

### Use Cases
- **AI/ML Training**: Pattern recognition, anomaly detection
- **Customer Segmentation**: Behavioral analysis
- **Recommendation Systems**: Service suggestions
- **Risk Assessment**: Credit scoring, overdraft prediction
- **Market Research**: Spending behavior analysis
- **Financial Planning**: Budget optimization tools

### Data Volume
- **Current Output**: ~20 million lines
- **Clients**: 20,000 unique Swiss people
- **Time Period**: 365 days (1 year)
- **File Size**: ~1.9GB
- **Categories**: 30+ transaction categories
- **Merchants**: 150+ Swiss companies

---

## Advanced Features (Latest Update)

### 🚀 **Advanced Financial & Behavioral Patterns**

#### **1. Payday Splurging & "Broke" Behavior**
- **Payday Splurge Mode**: Higher discretionary spending (high-end restaurants, electronics, clothing) in first few days after salary
- **Broke Mode**: Belt-tightening behavior (cheaper groceries, fewer non-essential purchases) before next salary
- **Dynamic Spending**: Automatic switching between modes based on salary cycle

#### **2. Buy Now, Pay Later (BNPL) Services**
- **Klarna/Afterpay Simulation**: Large initial purchases followed by monthly installments
- **Installment Tracking**: Automatic monthly payments until completion
- **Realistic Patterns**: 3-12 month payment plans with varying amounts

#### **3. Peer-to-Peer (P2P) Payments**
- **Twint Integration**: Frequent small-value transfers between individuals
- **Memo Types**: "Lunch", "Dinner", "Rent Share", "Tickets", "Coffee", "Drinks", "Split Bill"
- **Social Patterns**: Higher frequency for young and social personas

#### **4. Savings & Investment Transfers**
- **Regular Transfers**: Fixed-amount transfers on 1st and 15th of month
- **Dynamic Amounts**: Based on individual savings rate (5-25% of income)
- **Ad-hoc Transfers**: Bonus savings and conscious saving decisions

#### **5. Economic Sentiment Effects**
- **Global Sentiment Variable**: Affects spending patterns across all users
- **Lipstick Effect**: During low sentiment, fewer luxury items but more small comforts
- **Comfort Purchases**: Gourmet coffee, cosmetics, streaming services, small treats

### 🔄 **Life Events & State Changes**

#### **1. Dynamic Personas (Not Static)**
- **Job Change**: Sudden salary increase (10-30%), potential commute pattern changes
- **Job Loss**: Abrupt salary stop, subscription cancellations, drastic spending reduction
- **Moving**: Cluster of high-value transactions (moving company, furniture, home improvement)
- **New Child**: Fundamental spending shift (baby supplies, pharmacy, childcare fees)

#### **2. Life Event Probabilities**
- **Job Change**: 5% per year
- **Job Loss**: 2-3% per year (higher for young people)
- **Moving**: 10-15% per year (higher for young people)
- **New Child**: 3% per year (middle-aged only)

### 🇨🇭 **Granular & Swiss-Specific Details**

#### **1. Travelcard Lifecycle**
- **Halbtax**: 185 CHF annually (70% of urban users, 30% of car owners)
- **GA (General Travelcard)**: 3,860 CHF annually (20% of urban users, 10% of car owners)
- **Cost Reduction**: Individual ticket costs decrease after travelcard purchase

#### **2. Apéro Culture**
- **Timing**: 5-7 PM on weekdays, especially Thursdays and Fridays
- **Venues**: Bar 63, Café Bar Odeon, Bar Au Lac, Local Pubs
- **Personas**: Higher probability for Young Professional and Urban Transport users

#### **3. Gifting Occasions**
- **Birthday Month**: 30% chance of gift purchases in birthday month
- **Holiday Season**: 25% chance during November-December
- **Regular Gifting**: 5% daily probability for social occasions

#### **4. Pet Ownership Persona**
- **Pet Expenses**: Monthly costs (80-200 CHF) for food, vet, insurance
- **Ownership Rate**: 20% of population
- **Stores**: Pet Store, Vet Clinic, Pet Food Store, Pet Insurance

#### **5. Swiss Billers**
- **Serafe Media Tax**: 335 CHF quarterly (mandatory)
- **Health Insurance**: 300-500 CHF monthly (Swica, Helsana, CSS, Atupri, KPT, Concordia, Sanitas)
- **Universal Coverage**: Nearly all Swiss residents have these payments

### 📊 **Data Quality & Anomaly Refinements**

#### **1. Delayed Transactions**
- **Real-world Delays**: 1-3 days between authorization and posting
- **Weekend Effects**: Longer delays over weekends and holidays
- **Processing Variations**: Realistic timing inconsistencies

#### **2. Split Payments & Reimbursements**
- **Group Expenses**: One person pays full amount, others reimburse via P2P
- **Shared Costs**: Rent, utilities, travel, entertainment splits
- **Social Dynamics**: Realistic group spending patterns

#### **3. Category Ambiguity**
- **Department Stores**: Manor purchases could be groceries, clothing, or electronics
- **Mixed Categories**: Single transactions with multiple category possibilities
- **AI Training**: Helps train models to handle categorization ambiguity

#### **4. Evolution of Preferences**
- **Dynamic Changes**: 0.1% daily chance of preference evolution
- **Grocery Store Switches**: Changes between Migros, Coop, Aldi, Lidl, etc.
- **Coffee Shop Discovery**: New favorite coffee shops over time
- **Realistic Behavior**: People do change preferences in real life

### 🎯 **Enhanced AI Training Benefits**

#### **1. Behavioral Psychology**
- **Payday Psychology**: Learn to predict spending spikes after income
- **Economic Sentiment**: Understand how external factors affect spending
- **Life Event Detection**: Identify major life changes through spending patterns

#### **2. Financial Planning**
- **Savings Patterns**: Predict when people will save or splurge
- **BNPL Risk**: Assess credit risk through installment payment history
- **Budget Optimization**: Understand spending cycles and optimization opportunities

#### **3. Customer Segmentation**
- **Dynamic Personas**: Track how personas evolve over time
- **Life Stage Analysis**: Identify transitions between life stages
- **Preference Evolution**: Understand how customer preferences change

#### **4. Anomaly Detection**
- **Life Event Anomalies**: Detect unusual spending patterns indicating life changes
- **Financial Stress**: Identify patterns suggesting financial difficulties
- **Behavioral Changes**: Track significant shifts in spending behavior

### 📈 **Expected Output Improvements**
- **File Size**: 2.5-3.0GB (vs. previous 1.9GB)
- **Line Count**: 25-30 million lines (vs. previous 20 million)
- **Categories**: 40+ transaction categories (vs. previous 30+)
- **Companies**: 200+ Swiss companies (vs. previous 150+)
- **Patterns**: 50+ distinct behavioral patterns (vs. previous 25+)

## 🧠 **Deeper Behavioral & Psychological Patterns**

### **Cognitive Biases in Spending**
Incorporate principles from behavioral economics to make personas more realistic:

#### **1. Anchoring Effect**
- **First Purchase Anchor**: Initial significant purchase in a category sets spending expectations
- **Example**: CHF 800 smartphone purchase makes future purchases in that range seem reasonable
- **Implementation**: Track first major purchase per category and adjust future spending limits

#### **2. Mental Accounting**
- **Source-Based Spending**: Money treated differently based on source
- **Bonus Spending**: CHF 500 bonus spent frivolously on luxury items
- **Salary Spending**: Same amount from salary spent on essentials
- **Implementation**: Different spending patterns for different income sources

#### **3. Herd Behavior**
- **Trendy Items**: Spending spikes on popular new products or services
- **Social Influence**: Restaurant choices driven by popularity rather than individual preference
- **Implementation**: Global popularity variables affecting spending probabilities

#### **4. Loss Aversion**
- **Subscription Cancellations**: Higher likelihood of canceling after price increases
- **Price Sensitivity**: Even nominal increases trigger behavioral changes
- **Implementation**: Price change events with increased cancellation probability

### **"Forgotten" Subscriptions**
- **Active but Unused**: Gym memberships, streaming services, software subscriptions
- **Common Financial Drain**: Real-world pattern of unused recurring payments
- **Implementation**: 5-15% of subscriptions show no related activity

### **Goal-Oriented Saving & Spending**
- **Saving Phases**: Reduced spending across multiple categories for several months
- **Planned Purchases**: Large purchases after saving periods (vacations, down payments, cars)
- **Implementation**: Multi-month saving cycles followed by spending spikes

### **Inter-Personal Transactions (Family/Couples)**
- **Shared Accounts**: Joint accounts with two salary credits and mixed family spending
- **Expense Splitting**: One person pays for large items, receives P2P transfers from partners
- **Implementation**: Family personas with coordinated spending and reimbursement patterns

### **Lifecycle of Financial Products**
- **Credit Card Introductory Offers**: Concentrated spending on new cards to meet bonus requirements
- **Loan Repayments**: Fixed monthly payments for cars/electronics over specific durations (e.g., 36 months)

## 🔧 **Advanced Technical & Data Generation Nuances**

### **Transaction Status Lifecycle**
Instead of just generating "posted" transactions, model the complete lifecycle:

#### **1. Authorization Phase**
- **Initial Hold**: Funds placed on hold (e.g., gas station pump)
- **Pending Status**: Transaction visible but not final
- **Implementation**: Generate authorization transactions with pending status

#### **2. Settlement/Posting Phase**
- **Final Amount**: Confirmed amount debited (may differ from authorization)
- **Timing**: 1-3 days after authorization
- **Implementation**: Settlement transactions replacing or updating authorizations

#### **3. Description Enrichment**
- **Raw Names**: "SP * MERCHANT NAME" at authorization
- **Clean Names**: "Merchant Name" upon settlement
- **Implementation**: Two-phase merchant name generation

### **Correlated Spending**
Create realistic dependencies between certain purchases:

#### **1. Event-Based Correlations**
- **Ticket Purchases**: Followed by transport and restaurant spending in event city
- **Example**: Ticketcorner purchase → increased SBB, restaurant, and bar spending
- **Implementation**: Event date clustering of related transactions

#### **2. Project-Based Correlations**
- **DIY Projects**: Clustered purchases at hardware stores over weekends
- **Example**: Jumbo, Hornbach purchases clustered together
- **Implementation**: Multi-day project spending patterns

### **Card-Present vs. Card-Not-Present (CNP)**
- **Physical POS**: In-store transactions with different patterns
- **Online/CNP**: Higher likelihood of subscriptions, food delivery, e-commerce
- **Implementation**: Different transaction types with distinct behavioral patterns

### **Multi-Currency Simulation**
- **Foreign Transactions**: EUR, USD transactions for travelers and cross-border shoppers
- **FX Fees**: Separate foreign exchange fee transactions
- **Conversion Rates**: Embedded conversion rates in final CHF amounts
- **Implementation**: Currency-specific transaction generation with realistic rates

### **Geospatial Clustering**
- **Home Location**: Evening and weekend transactions cluster around home
- **Work Location**: Weekday transactions (lunch, coffee) cluster around work
- **Implementation**: Location-based transaction clustering for realistic patterns

## 🇨🇭 **Richer Swiss Context & Specifics**

### **Pillar 3a Pension Payments**
- **Annual Lump-Sum**: December transfers to dedicated pension accounts
- **Monthly Standing Orders**: Regular contributions throughout the year
- **Common Pattern**: Very typical Swiss financial behavior
- **Implementation**: Pension-related transaction patterns

### **The "eBill" System**
- **Batch Payments**: Multiple bill payments initiated on same day each month
- **E-banking Integration**: When users log into online banking
- **Billers**: Insurance, utilities, tax authorities, subscriptions
- **Implementation**: Monthly batch payment patterns

### **Cross-Border Shopping**
- **Price Differences**: Switzerland's high domestic prices drive cross-border shopping
- **Target Countries**: Germany, France, Italy
- **Merchants**: Edeka, DM, Carrefour, Fnac
- **Implementation**: Euro transactions with foreign merchant patterns

### **Specific Merchant & Payment Behaviors**

#### **1. SBB EasyRide**
- **Tap-and-Go**: Small, variable SBB charges aggregated and billed later
- **Daily/Monthly Billing**: Instead of daily tickets
- **Implementation**: Variable transport charges with delayed billing

#### **2. Lunch Checks**
- **Employer Benefits**: Monthly "top-up" from salary
- **Restaurant Network**: Spending at specific employer-approved restaurants
- **Implementation**: Employer benefit transaction patterns

### **Regional & Linguistic Variations**

#### **1. French-Speaking Regions**
- **Retailers**: More transactions with French retailers (Carrefour, Fnac)
- **Cultural Patterns**: Different spending preferences and timing

#### **2. Italian-Speaking Ticino**
- **Cash Preference**: Slightly higher preference for cash transactions
- **Regional Merchants**: Local Italian-Swiss business patterns

#### **3. Mobile Payment Variations**
- **Regional Preferences**: Different mobile payment app adoption by region
- **Banking Affiliation**: Payment method preferences vary by bank
