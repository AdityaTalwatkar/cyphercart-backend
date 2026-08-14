# CypherCart Backend

Backend service for **CypherCart**, an e-commerce catalog and recommendation engine built with **Spring Boot** and backed by **CognoDB** (managed graph database using openCypher over the Bolt protocol).

---

## 🚀 Why a Graph Database?

Traditional relational databases rely on foreign keys and expensive `JOIN` operations for multi-hop recommendations such as *"users who bought X also bought Y"*.

**CognoDB / Graph Model Benefits:**

* **Fast Relationship Traversal:** Directly traverses connected nodes for multi-hop recommendations.
* **Flexible Schema:** Easily models `User`, `Product`, `Category`, and purchase/view relationships.

---

## 📊 Data Model Diagram

```text
(User) --[PURCHASED]-> (Product) --[BELONGS_TO]-> (Category)
   |
   `--[VIEWED]-------> (Product)
```

* **Nodes:**

  * `User`: Platform customers.
  * `Product`: E-commerce products.
  * `Category`: Product classifications.

* **Relationships:**

  * `PURCHASED`: User purchased a product.
  * `BELONGS_TO`: Product belongs to a category.
  * `VIEWED`: User viewed a product.

---

## 🛠️ Technology Stack

* **Java 21** & **Spring Boot**
* **Spring Data Neo4j**
* **CognoDB Cloud** (openCypher over Bolt)

---

## ⚙️ Setup and Configuration

1. **Clone the repository:**

```bash
git clone https://github.com/AdityaTalwatkar/cyphercart-backend.git
cd cyphercart-backend
```

2. **Configure CognoDB Credentials:**

Open `src/main/resources/application.properties`:

```properties
spring.neo4j.uri=bolt+s://<your-instance-id>.databases.cognodb.cloud
spring.neo4j.authentication.username=cognodb
spring.neo4j.authentication.password=<your-generated-password>
```

3. **Run the Application:**

```bash
./mvnw spring-boot:run
```

## 🔍 Main Cypher Queries

### 1. Multi-Hop Recommendation

```cypher
MATCH (u:User {id: $userId})-[:PURCHASED]->(p:Product)<-[:PURCHASED]-(other:User)-[:PURCHASED]->(rec:Product)
WHERE NOT (u)-[:PURCHASED]->(rec)
RETURN DISTINCT rec, count(rec) AS score
ORDER BY score DESC
LIMIT 5
```

### 2. Catalog Retrieval

```cypher
MATCH (p:Product)
RETURN p
```
