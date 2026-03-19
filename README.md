# FraudGraph

Social network graph analysis for organised motor insurance fraud detection.

Traditional fraud detection evaluates claims individually. FraudGraph maps the relationships between them — exposing fraud rings that are completely invisible to per-claim analysis.

---

## The Problem

A professional fraud ring submits multiple claims through different claimants, at different locations, on different dates. Each claim looks legitimate in isolation. But they all share the same witness, the same repair garage, and the same lawyer.

Per-claim systems never see this. FraudGraph does.

---

## How It Works

Every named entity in a claim — witness, garage, lawyer, doctor — becomes a node in a network. When a new claim arrives, the system queries the entire claims database for shared entities and maps the connections. Fraud rings that look like unrelated individuals reveal themselves as tightly clustered networks the moment the relationships are visualised.

The risk engine scores each claim based on how many shared entities it has and how frequently those entities appear across the database. Clean claims are auto-approved. Suspicious claims are routed to investigators. Confirmed rings are frozen and escalated — with a full evidence pack already built.

---

## Tech Stack

- **Backend** — Java 17, Spring Boot 3.2, Spring Data JPA, Hibernate, Lombok
- **Database** — MySQL
- **API** — REST via Spring MVC
- **Frontend** — HTML, CSS, Vanilla JavaScript
- **Graph** — Vis.js 9.1.9

---

## Features

- Real-time fraud ring detection on every claim submission
- Interactive network graph — nodes coloured by risk level, shaped by entity type
- Investigator dashboard with claim detail, flagged entities, and connected claim references
- One-click escalation and status management
- Dark mode with localStorage persistence
- Resizable dashboard panels
- Demo fill buttons for instant testing

---

## Running the Project

**Requirements:** Java 17, Maven, MySQL

```bash
# Create the database
CREATE DATABASE fraudgraph;

# Configure credentials in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/fraudgraph
spring.datasource.username=root
spring.datasource.password=yourpassword

# Run
mvn spring-boot:run

# Open
http://localhost:8080
```
