# AgetAPI

# 🚀 Repo API Extractor:

An intelligent backend system that scans any GitHub repository and automatically extracts API endpoints along with their request/response schemas.

---

## 🔥 Features

* 🔍 **Automatic API Detection**

  * Scans repository files to find REST endpoints
* 🧠 **AI-powered Schema Extraction**

  * Uses LLM to infer request/response schemas from code
* ⚡ **Concurrent Processing**

  * Multi-threaded schema generation for performance
* 🧹 **Duplicate Removal**

  * Ensures unique API endpoints
* 📂 **Multi-language Ready (Extensible)**

  * Designed to support Java, Node.js, Go, etc.

---

## 🏗️ Architecture

```
GitHub Repo
   ↓
Clone Service
   ↓
File Scanner
   ↓
Endpoint Extractor
   ↓
Code Snippet Extractor
   ↓
LLM (Schema Generator)
   ↓
Structured Output
```

---

## 🛠️ Tech Stack

* **Java (Spring Boot)**
* **LangChain4j / LLM Integration**
* **Multithreading (ExecutorService)**
* **File System Scanning**
* **REST API Parsing**

---

## 📦 Project Structure

```
service/
 ├── ExtractionService.java
 ├── SchemaGeneratorService.java
 ├── CodeSnippetExtractorService.java
 ├── EndpointExtractorService.java
 ├── RepoScannerService.java
 └── GitService.java

agent/
 ├── RepoAgent.java
 └── RepoTools.java

model/
 ├── ApiEndpoint.java
 └── RepositoryStructure.java
```

---

## 🚀 How It Works

1. Provide a GitHub repository URL
2. System clones the repo
3. Scans files to detect API endpoints
4. Extracts relevant code snippets
5. Uses AI to generate schema
6. Returns structured API data

---

## 📌 Example Output

```json
{
  "method": "POST",
  "path": "/jobPost",
  "requestSchema": { ... },
  "responseSchema": { ... }
}
```

---

## ⚠️ Limitations

* Schema accuracy depends on code clarity
* Dynamic languages (Node.js) may require heuristics
* LLM output may need post-processing

---
## ▶️ How to Run the Project

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/repo-name.git
cd repo-name
```

---

### 2️⃣ Build the Project

If using Maven:

```bash
mvn clean install
```

---

### 3️⃣ Run the Application

```bash
mvn spring-boot:run
```

OR run directly from your IDE (IntelliJ / Eclipse)

---

### 4️⃣ Server Starts At

```bash
http://localhost:8080
```

---

## 📬 API Testing using Postman

### 🔹 Endpoint

```bash
POST http://localhost:8080/extract
```

---

### 🔹 Request Body (JSON)

```json
{
  "repoUrl": "https://github.com/your-username/sample-repo"
}
```

---

### 🔹 Steps in Postman

1. Open Postman
2. Start the server
3.Open Postman
4.Hit /extract API
5.Provide any public GitHub repo URL
6.Get extracted APIs + schema in response
