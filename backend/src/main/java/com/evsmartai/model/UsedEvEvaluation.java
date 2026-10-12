package com.evsmartai.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "used_ev_evaluations", indexes = @Index(name = "idx_used_ev_user", columnList = "user_id"))
public class UsedEvEvaluation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private AppUser user;
    @Column(nullable = false, length = 80) private String make;
    @Column(nullable = false, length = 100) private String model;
    @Column(name = "model_year", nullable = false) private int modelYear;
    @Column(name = "asking_price", nullable = false) private double askingPrice;
    @Column(name = "mileage_km", nullable = false) private int mileageKm;
    @Column(name = "battery_health_percent", nullable = false) private double batteryHealthPercent;
    @Column(name = "service_history_available", nullable = false) private boolean serviceHistoryAvailable;
    @Column(nullable = false) private double score;
    @Column(name = "risk_summary", length = 1000) private String riskSummary;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    public Long getId(){return id;} public AppUser getUser(){return user;} public void setUser(AppUser v){user=v;}
    public String getMake(){return make;} public void setMake(String v){make=v;}
    public String getModel(){return model;} public void setModel(String v){model=v;}
    public int getModelYear(){return modelYear;} public void setModelYear(int v){modelYear=v;}
    public double getAskingPrice(){return askingPrice;} public void setAskingPrice(double v){askingPrice=v;}
    public int getMileageKm(){return mileageKm;} public void setMileageKm(int v){mileageKm=v;}
    public double getBatteryHealthPercent(){return batteryHealthPercent;} public void setBatteryHealthPercent(double v){batteryHealthPercent=v;}
    public boolean isServiceHistoryAvailable(){return serviceHistoryAvailable;} public void setServiceHistoryAvailable(boolean v){serviceHistoryAvailable=v;}
    public double getScore(){return score;} public void setScore(double v){score=v;}
    public String getRiskSummary(){return riskSummary;} public void setRiskSummary(String v){riskSummary=v;}
    public Instant getCreatedAt(){return createdAt;}
}
