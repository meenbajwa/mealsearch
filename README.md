# Meal Search 🔍

A powerful Java-based desktop application for searching and scraping meal information across the web. Built with modern Java practices, this application provides robust meal discovery, data crawling capabilities, and structured data management for comprehensive meal research and analytics.

## 🌟 Key Features

- **Web Scraping** - Extract meal information from multiple online sources
- **Advanced Search** - Search meals by name, cuisine, or ingredients
- **Data Crawling** - Automated crawler to aggregate meal data
- **Regex Pattern Matching** - Intelligent pattern recognition for data extraction
- **Phone Number Validation** - Extract and validate contact information
- **CSV Export** - Export search results to CSV format
- **Logging System** - Comprehensive logging for debugging and monitoring
- **Modular Architecture** - Clean, scalable Java codebase

## 🛠️ Tech Stack

- **Language**: Java
- **Build Tool**: Gradle
- **API**: TheMealDB (for meal data)
- **Data Processing**: Custom parsing and regex patterns
- **File I/O**: CSV file handling
- **Logging**: Java logging framework
- **Libraries**: Gson (JSON processing), HttpClient

## 📦 Installation & Setup

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Gradle 6.0 or higher
- Git
- Terminal/Command Prompt

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/meenbajwa/mealsearch.git
   cd mealsearch
   ```

2. **Build the project**
   ```bash
   gradle build
   ```

3. **Run the application**
   ```bash
   gradle run
   ```

   Or directly with Java:
   ```bash
   java -jar build/libs/mealsearch.jar
   ```

## 🚀 Usage Guide

### 1. **Search for Meals**
   ```bash
   # Basic search
   Search for: Biryani
   ```

### 2. **Browse Search Results**
   - View all meals matching your search query
   - See detailed information for each meal
   - Access cooking instructions and ingredients

### 3. **Export Data**
   - Export search results to CSV
   - Use data for further analysis
   - Share meal information with others

### 4. **Web Crawling**
   - Automatically crawl meal databases
   - Extract meal information
   - Store results locally

## 📁 Project Structure

```
mealsearch/
├── src/
│   ├── main/java/
│   │   ├── crawler/       # Web scraping and crawling logic
│   │   ├── search/        # Search functionality
│   │   ├── models/        # Data models and entities
│   │   ├── utils/         # Utility classes and helpers
│   │   └── Main.java      # Application entry point
│   └── resources/
│       └── config/        # Configuration files
├── gradle/                # Gradle wrapper files
├── build.gradle           # Gradle build configuration
└── settings.gradle        # Project settings
```

## 🎯 Core Components

### Crawler Module
- **WebCrawler** - Crawls web pages for meal information
- **DataParser** - Parses HTML and extracts meal data
- **URLHandler** - Manages URL requests and connections

### Search Module
- **MealSearcher** - Main search implementation
- **QueryBuilder** - Constructs search queries
- **ResultFilter** - Filters and sorts results

### Data Models
- **Meal** - Represents a meal with properties
- **Recipe** - Recipe details and instructions
- **Ingredient** - Ingredient information with quantities

### Utility Classes
- **CSVExporter** - Export data to CSV format
- **RegexValidator** - Pattern matching and validation
- **Logger** - Application logging

## 📊 Data Processing

### Regex Patterns Used
- **Phone Numbers** - Validates and extracts phone numbers
- **Email Addresses** - Pattern matching for email validation
- **URLs** - Extract and validate URLs from content
- **Meal Names** - Parse and extract meal identifiers

## 📝 CSV Export Format

```
Meal Name,Category,Cuisine,Instructions,Ingredients,Difficulty
Biryani,Rice,Indian,"Step 1..., Step 2...",Basmati Rice;Meat;Spices,Medium
```

## 🔍 API Integration

The application integrates with **TheMealDB API** for meal data:

```
Base URL: https://www.themealdb.com/api/json/v1/1/
Endpoints:
- Search: /search.php?s={mealName}
- Lookup: /lookup.php?i={mealId}
- Categories: /categories.php
- Filter: /filter.php?c={category}
```

## 🐛 Logging

The application provides comprehensive logging:

```
Logging Levels:
- INFO: General information about application flow
- WARNING: Potential issues and warnings
- SEVERE: Errors and exceptions
- FINE: Detailed debugging information
```

Log files are stored in: `logs/mealsearch.log`

## 🚧 Future Enhancements

- **GUI Interface** - JavaFX-based graphical user interface
- **Database Integration** - Persistent storage with MySQL/PostgreSQL
- **Advanced Filtering** - Filter by cuisine, difficulty, prep time
- **Recipe Rating** - User ratings and reviews
- **Nutrition Information** - Calorie and nutrient data extraction
- **Multi-language Support** - Support for multiple languages
- **Batch Processing** - Process multiple searches simultaneously
- **Cloud Sync** - Sync data across devices
- **Mobile App** - Android/iOS companion app

## 🔒 Security Features

- **Input Validation** - Sanitize search queries
- **Error Handling** - Graceful error management
- **Exception Handling** - Comprehensive exception catching
- **Secure HTTP** - HTTPS for API calls when available

## 📱 Minimum Requirements

- **OS**: Windows, macOS, Linux
- **RAM**: 512 MB minimum
- **Disk Space**: 100 MB
- **Internet**: Required for API calls

## 🧪 Testing

```bash
# Run tests
gradle test

# Run tests with coverage
gradle testCodeCoverageReport
```

## 🤝 Contributing

We welcome contributions! Please:
- Report bugs with detailed information
- Suggest new features
- Submit pull requests with improvements
- Write tests for new features

## 📖 Documentation

- **API Docs**: See inline code comments
- **Build Guide**: See BUILDING.md
- **Contributing**: See CONTRIBUTING.md

## 👨‍💻 Author

**Jasmeen Kaur**
- GitHub: [@meenbajwa](https://github.com/meenbajwa)
- LinkedIn: [Jasmeen Kaur](https://www.linkedin.com/in/jasmeen-kaur-bb8b86409/)

## 📄 License

This project is open source and available under the MIT License.

## 🙏 Credits

- **API**: [TheMealDB](https://www.themealdb.com/) - Free meal database
- **Build System**: [Gradle](https://gradle.org/) - Build automation
- **Java Community**: For excellent tools and libraries

---

**Search, Discover, and Organize Meal Data! 🍜**
