// 1. Clear existing data safely
MATCH (n) DETACH DELETE n;

// 2. Create Brands
CREATE (b1:Brand {name: 'boAt'})
CREATE (b2:Brand {name: 'Zebronics'})

// 3. Create Products
CREATE (p1:Product {id: 'prod-1', name: 'boAt Aavante Bar 1500 Wireless Soundbar', price: 7999.0, category: 'Soundbars', stock: 45, status: 'Active'})-[:MANUFACTURED_BY]->(b1)
CREATE (p2:Product {id: 'prod-2', name: 'Zebronics Zeb-Transformer Gaming Mouse', price: 1299.0, category: 'Mice', stock: 78, status: 'Active'})-[:MANUFACTURED_BY]->(b2)
CREATE (p3:Product {id: 'prod-3', name: 'Zebronics Zeb-Companion Keyboard & Mouse Combo', price: 1799.0, category: 'Keyboards', stock: 32, status: 'Active'})-[:MANUFACTURED_BY]->(b2)
CREATE (p4:Product {id: 'prod-4', name: 'boAt Rockerz 450 On-Ear Headphones', price: 2499.0, category: 'Headphones', stock: 25, status: 'Active'})-[:MANUFACTURED_BY]->(b1)

// 4. Create Users
CREATE (u1:User {id: 'user-1', name: 'Alice', role: 'CUSTOMER'})
CREATE (u2:User {id: 'user-2', name: 'Bob', role: 'CUSTOMER'})
CREATE (u3:User {id: 'user-3', name: 'Charlie', role: 'CUSTOMER'})
CREATE (u4:User {id: 'user-admin', name: 'Admin User', role: 'ADMIN'})

// 5. Create Multi-Hop Graph Relationships
// Alice, Bob, and Charlie buy the soundbar
CREATE (u1)-[:PURCHASED {date: '2026-08-10'}]->(p1)
CREATE (u2)-[:PURCHASED {date: '2026-08-11'}]->(p1)
CREATE (u3)-[:PURCHASED {date: '2026-08-11'}]->(p1)

// Bob and Charlie also buy other products (enabling collaborative filtering cross-sell)
CREATE (u2)-[:PURCHASED {date: '2026-08-12'}]->(p2)
CREATE (u2)-[:PURCHASED {date: '2026-08-12'}]->(p3)
CREATE (u3)-[:PURCHASED {date: '2026-08-13'}]->(p2)