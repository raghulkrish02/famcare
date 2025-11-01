package com.famcare.model;

import jakarta.persistence.*;

@Entity
@Table(name = "families")
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String familyName;

    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
