package com.wexa.cyphercart.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("User")
public class User {
    @Id @GeneratedValue private String id;
    private String name;
    private String role; 

    @Relationship(type = "PURCHASED", direction = Relationship.Direction.OUTGOING)
    private Set<Product> purchasedProducts = new HashSet<>();

    public User() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Set<Product> getPurchasedProducts() { return purchasedProducts; }
    public void setPurchasedProducts(Set<Product> purchasedProducts) { this.purchasedProducts = purchasedProducts; }
}