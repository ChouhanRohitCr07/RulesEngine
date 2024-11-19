package com.example.RulesEngine.entity;


import jakarta.persistence.*;

@Entity
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessUnit;

    @Column(nullable = false, length = 500)
    private String condition;

    @Column(nullable = false, length = 500)
    private String consequence;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 50)
    private String approvalType; // New column for approval type

    // Constructors
    public Rule() {}

    public Rule(String businessUnit, String condition, String consequence, String description, String approvalType) {
        this.businessUnit = businessUnit;
        this.condition = condition;
        this.consequence = consequence;
        this.description = description;
        this.approvalType = approvalType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConsequence() {
        return consequence;
    }

    public void setConsequence(String consequence) {
        this.consequence = consequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", businessUnit='" + businessUnit + '\'' +
                ", condition='" + condition + '\'' +
                ", consequence='" + consequence + '\'' +
                ", description='" + description + '\'' +
                ", approvalType='" + approvalType + '\'' +
                '}';
    }
}