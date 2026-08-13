package com.wexa.cyphercart.repository;

import com.wexa.cyphercart.entity.Product;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends Neo4jRepository<Product, String> {

    @Query("MATCH (p:Product) RETURN p")
    List<Product> findAll();

    
    @Query("CREATE (p:Product {id: $id, name: $name, price: $price, category: $category, brand: $brand, stock: $stock, status: $status}) RETURN p")
    Product createProductCypher(@Param("id") String id, 
                                @Param("name") String name, 
                                @Param("price") Double price, 
                                @Param("category") String category, 
                                @Param("brand") String brand, 
                                @Param("stock") Integer stock, 
                                @Param("status") String status);

    @Query("MATCH (p:Product {id: $id}) DETACH DELETE p")
    void deleteProductSafely(@Param("id") String id);

    // General user recommendation query
    @Query("MATCH (me:User {id: $userId})-[:PURCHASED]->(p:Product)<-[:PURCHASED]-(other:User)-[:PURCHASED]->(rec:Product) " +
           "WHERE me <> other AND NOT (me)-[:PURCHASED]->(rec) " +
           "RETURN rec, count(rec) AS frequency " +
           "ORDER BY frequency DESC LIMIT 5")
    List<Product> getGadgetRecommendations(@Param("userId") String userId);

    
    @Query("MATCH (p:Product {id: $productId})<-[:PURCHASED]-(other:User)-[:PURCHASED]->(rec:Product) " +
           "WHERE rec.id <> $productId " +
           "RETURN rec")
    List<Product> getProductRecommendations(@Param("userId") String userId, @Param("productId") String productId);
}