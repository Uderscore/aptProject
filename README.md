# aptProject - Web Search Engine

A full-featured web search engine built with Spring Boot, featuring web crawling, intelligent indexing, natural language processing, and ranked search results.

## 📋 Overview

This project implements a production-ready search engine that crawls websites, indexes content, processes queries using NLP techniques, and returns ranked search results. It includes REST APIs for search and query suggestions, making it easy to integrate with front-end applications.

## ✨ Features

### Core Functionality
- **Web Crawler**: Multi-threaded crawler that respects robots.txt and handles up to 2 levels of depth
- **Document Indexer**: Builds inverted indices for fast content retrieval
- **Query Processor**: Advanced query processing with lemmatization and stemming
- **Result Ranker**: Implements PageRank algorithm for result ranking
- **Search API**: RESTful endpoints for search queries with pagination support
- **Query Suggestions**: Auto-suggestion feature based on query logs and popular searches
- **Phrase Search**: Support for exact phrase matching

### Technical Features
- Multi-threaded crawling (50+ concurrent threads)
- MongoDB document storage with efficient indexing
- Redis caching for improved performance
- NLP-powered text processing using Stanford CoreNLP
- HTML parsing with JSoup
- Stemming using Lucene analyzers
- CORS support for front-end integration

## 🛠️ Tech Stack

### Backend Framework
- **Java 17**
- **Spring Boot 3.2.5**
- **Maven** for dependency management

### Databases & Caching
- **MongoDB** - Document storage and inverted index
- **Redis** - Caching layer

### Key Libraries
- **JSoup** (1.15.3) - HTML parsing
- **Stanford CoreNLP** (4.5.9) - Natural language processing and lemmatization
- **Apache OpenNLP** (1.9.2) - NLP tools
- **Apache Lucene** (8.10.0) - Text analysis and stemming
- **JGraphT** (1.5.2) - Graph algorithms for PageRank
- **Jedis** (5.1.2) - Redis client

## 🏗️ Architecture

```
aptProject/
├── src/main/java/
│   ├── crawler/
│   │   └── Crawler.java              # Web crawler implementation
│   ├── com/example/searchengine/
│   │   ├── SearchEngineApplication.java  # Spring Boot entry point
│   │   ├── controller/
│   │   │   ├── SearchController.java     # Search API endpoints
│   │   │   └── SuggestionController.java # Suggestion API endpoints
│   │   ├── service/
│   │   │   ├── SearchService.java        # Core search logic
│   │   │   ├── QueryProcessor.java       # Query analysis & processing
│   │   │   ├── Ranker.java               # Result ranking
│   │   │   ├── SuggestionService.java    # Query suggestions
│   │   │   └── StanfordLemmatizerImpl.java
│   │   ├── model/
│   │   │   ├── WebDocument.java          # Document model
│   │   │   ├── SearchResponse.java       # API response model
│   │   │   └── QueryLog.java             # Query logging
│   │   └── config/
│   │       ├── AppConfig.java
│   │       ├── CorsConfig.java
│   │       └── MongoConnectionChecker.java
│   └── ...
├── pom.xml                          # Maven dependencies
├── docker-compose.yml               # Redis container config
├── seeds.txt                        # Initial crawl URLs
├── visited.txt                      # Crawl state tracking
├── pending.txt                      # URLs pending crawl
└── blocked_urls.txt                 # URLs to skip
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MongoDB (local or remote instance)
- Docker (for Redis - optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Uderscore/aptProject.git
   cd aptProject
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Start Redis (using Docker)**
   ```bash
   docker-compose up -d
   ```

4. **Configure MongoDB connection**
   - Update MongoDB connection settings in application configuration
   - Default connection: `mongodb://localhost:27017`

5. **Prepare seed URLs**
   - Add initial URLs to crawl in `seeds.txt`
   - One URL per line

### Running the Application

1. **Start the search engine**
   ```bash
   mvn spring-boot:run
   ```

2. **The API will be available at**
   ```
   http://localhost:8080
   ```

## 📡 API Endpoints

### Search Endpoint
Perform a search query

```http
GET /api/v1/search?query={search_term}&topK={top_k}&page={page}&size={size}
```

**Parameters:**
- `query` (required): Search query string
- `topK` (optional, default: 100): Number of top results to retrieve
- `page` (optional, default: 0): Page number for pagination
- `size` (optional, default: 10): Results per page

**Example Request:**
```bash
curl "http://localhost:8080/api/v1/search?query=machine%20learning&topK=100&page=0&size=10"
```

**Response:**
```json
{
  "results": [...],
  "page": 0,
  "size": 10,
  "totalResults": 87,
  "totalPages": 9,
  "executionTime": 142
}
```

### Phrase Search Endpoint
Search for exact phrases

```http
GET /api/v1/search/phrase?query={phrase}&topK={top_k}&page={page}&size={size}
```

**Parameters:** Same as regular search

**Example Request:**
```bash
curl "http://localhost:8080/api/v1/search/phrase?query=artificial%20intelligence&page=0&size=10"
```

### Suggestion Endpoint
Get query suggestions based on prefix

```http
GET /api/v1/suggestion?prefix={prefix}&limit={limit}
```

**Parameters:**
- `prefix` (required): Query prefix for suggestions
- `limit` (optional, default: 10): Maximum number of suggestions

**Example Request:**
```bash
curl "http://localhost:8080/api/v1/suggestion?prefix=mach&limit=5"
```

**Response:**
```json
[
  "machine learning",
  "machine vision",
  "machine translation",
  "machine intelligence",
  "machine algorithms"
]
```

## 🔧 Configuration

### MongoDB Configuration
Configure your MongoDB connection in Spring Boot application properties:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/searchengine
```

### Redis Configuration
Redis is configured via Docker Compose. Default port: 6379

### Crawler Configuration
Edit crawler settings in the Crawler.java file:
- Thread pool size
- Maximum crawl depth
- Robots.txt compliance
- URL patterns to include/exclude

## 🏃 Running the Crawler

The crawler can be run independently to index new websites:

```bash
# Add seed URLs to seeds.txt
echo "https://example.com" >> seeds.txt

# Run the crawler (implemented in the application)
# Crawler respects robots.txt and follows polite crawling practices
```

## 📊 Project Status

This is an academic/learning project demonstrating:
- Full-stack search engine architecture
- Web crawling best practices
- Information retrieval algorithms
- NLP integration for text processing
- REST API design
- Spring Boot development

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

## 📝 License

This project is available for educational purposes.

## 🤝 Contributing

This is an educational project. Feel free to fork and experiment with your own implementations.

## 📧 Contact

For questions or feedback about this project, please open an issue on GitHub.

---

**Note**: This search engine is designed for educational purposes. For production use, consider additional features like security hardening, advanced ranking algorithms, distributed crawling, and comprehensive error handling.
