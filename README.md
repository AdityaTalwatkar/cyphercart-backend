Here is the complete, comprehensive backend `README.md` file designed specifically for your Wexa AI take-home assignment. It covers everything the reviewers look for (architecture, graph database rationale, data model, queries, and setup).

---

### Backend README (`cyphercart-backend/README.md`)

Create or update the `README.md` file in your `cyphercart-backend` repository with this content:

```markdown
# CypherCart Backend

Backend service for **CypherCart**, an e-commerce catalog and recommendation engine built with **Spring Boot** and backed by **CognoDB** (managed graph database using openCypher over the Bolt protocol).

---

## 🚀 Why a Graph Database?
Traditional relational databases (SQL) rely on foreign keys and expensive `JOIN` operations to compute multi-hop relationships, such as finding collaborative filtering recommendations (*"users who bought product X also purchased product Y"*). As product catalogs and user interaction graphs grow, relational models suffer from performance degradation and rigid schema constraints.

**CognoDB / Graph Model Benefits:**
* **Native Index-Free Adjacency:** Graph databases traverse relationships directly in memory without costly table lookups, making multi-hop recommendation queries execute in milliseconds.
* **Flexible Schema:** Easily model complex, interconnected domains involving `User`, `Product`, `Category`, and `Order` nodes with typed, directional relationships.

---

## 📊 Data Model Diagram

```text
(User) --[PURCHASED]-> (Product) --[BELONGS_TO]-> (Category)
   \                                                 /
    `--------------[VIEWED]-------------------------'

```

* **Nodes:**
* `User`: Represents platform customers (e.g., Alice).
* `Product`: E-commerce catalog items (gadgets, electronics).
* `Category`: Grouping classifications (Mice, Soundbars, Keyboards).


* **Relationships:**
* `PURCHASED`: Connects users to items they have bought.
* `BELONGS_TO`: Connects products to their respective categories.
* `VIEWED`: Captures user browsing history for real-time recommendations.



---

## 🛠️ Technology Stack

* **Java 21** & **Spring Boot**
* **Spring Data Neo4j** (Official Bolt protocol driver)
* **CognoDB Cloud** (Managed graph database instance speaking openCypher)

---

## ⚙️ Setup and Configuration

1. **Clone the repository:**
```bash
git clone [https://github.com/AdityaTalwatkar/cyphercart-backend.git](https://github.com/AdityaTalwatkar/cyphercart-backend.git)
cd cyphercart-backend

```


2. **Configure CognoDB Credentials:**
Open `src/main/resources/application.properties` and add your CognoDB instance connection details:
```properties
spring.neo4j.uri=bolt+s://<your-instance-id>.databases.cognodb.cloud
spring.neo4j.authentication.username=cognodb
spring.neo4j.authentication.password=<your-generated-password>

```


3. **Run the Application:**
```bash
./mvnw spring-boot:run

```



---

## 🔍 Main Cypher Queries Explained

### 1. Multi-Hop Graph Recommendation Query

This query traverses the graph to find products bought by other users who share similar purchase history with the target user (collaborative filtering):

```cypher
MATCH (u:User {id: $userId})-[:PURCHASED]->(p:Product)<-[:PURCHASED]-(other:User)-[:PURCHASED]->(rec:Product)
WHERE NOT (u-[:PURCHASED]->rec)
RETURN DISTINCT rec, count(rec) as score
ORDER BY score DESC
LIMIT 5

```

### 2. Catalog Inventory Retrieval

Fetches all products mapped dynamically from the graph store:

```cypher
MATCH (p:Product)
RETURN p

```

```

```
