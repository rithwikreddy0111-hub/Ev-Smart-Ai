package com.evsmartai.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "battery_assessments", indexes = @Index(name = "idx_assessment_user", columnList = "user_id"))
public class BatteryAssessment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false) private AppUser user;
    @Column(name = "battery_age_years", nullable = false) private double batteryAgeYears;
    @Column(name = "charge_cycles", nullable = false) private int chargeCycles;
    @Column(name = "fast_charge_percent", nullable = false) private double fastChargePercent;
    @Column(name = "original_range_km", nullable = false) private double originalRangeKm;
    @Column(name = "observed_range_km", nullable = false) private double observedRangeKm;
    @Column(name = "estimated_soh_percent", nullable = false) private double estimatedSohPercent;
    @Column(name = "assessment_note", length = 500) private String assessmentNote;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    public Long getId(){return id;} public AppUser getUser(){return user;} public void setUser(AppUser v){user=v;}
    public double getBatteryAgeYears(){return batteryAgeYears;} public void setBatteryAgeYears(double v){batteryAgeYears=v;}
    public int getChargeCycles(){return chargeCycles;} public void setChargeCycles(int v){chargeCycles=v;}
    public double getFastChargePercent(){return fastChargePercent;} public void setFastChargePercent(double v){fastChargePercent=v;}
    public double getOriginalRangeKm(){return originalRangeKm;} public void setOriginalRangeKm(double v){originalRangeKm=v;}
    public double getObservedRangeKm(){return observedRangeKm;} public void setObservedRangeKm(double v){observedRangeKm=v;}
    public double getEstimatedSohPercent(){return estimatedSohPercent;} public void setEstimatedSohPercent(double v){estimatedSohPercent=v;}
    public String getAssessmentNote(){return assessmentNote;} public void setAssessmentNote(String v){assessmentNote=v;}
    public Instant getCreatedAt(){return createdAt;}
}
