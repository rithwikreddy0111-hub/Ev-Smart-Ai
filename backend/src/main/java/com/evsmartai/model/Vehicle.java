package com.evsmartai.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
@Entity @Table(name="vehicles",indexes=@Index(name="idx_vehicles_user",columnList="user_id"))
public class Vehicle {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private AppUser user;
 @NotBlank @Size(max=80) @Column(nullable=false,length=80) private String make;
 @NotBlank @Size(max=100) @Column(nullable=false,length=100) private String model;
 @Min(1990) @Max(2100) @Column(name="model_year",nullable=false) private int modelYear;
 @DecimalMin("1.0") @DecimalMax("300.0") @Column(name="battery_capacity_kwh",nullable=false) private double batteryCapacityKwh;
 @DecimalMin("0.0") @DecimalMax("100.0") @Column(name="battery_percent",nullable=false) private double batteryPercent=80;
 @DecimalMin("1.0") @DecimalMax("100.0") @Column(name="efficiency_kwh_per_100km",nullable=false) private double efficiencyKwhPer100Km;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 @PreUpdate void touch(){updatedAt=Instant.now();}
 public Long getId(){return id;} public AppUser getUser(){return user;} public void setUser(AppUser v){user=v;}
 public String getMake(){return make;} public void setMake(String v){make=v;}
 public String getModel(){return model;} public void setModel(String v){model=v;}
 public int getModelYear(){return modelYear;} public void setModelYear(int v){modelYear=v;}
 public double getBatteryCapacityKwh(){return batteryCapacityKwh;} public void setBatteryCapacityKwh(double v){batteryCapacityKwh=v;}
 public double getBatteryPercent(){return batteryPercent;} public void setBatteryPercent(double v){batteryPercent=v;}
 public double getEfficiencyKwhPer100Km(){return efficiencyKwhPer100Km;} public void setEfficiencyKwhPer100Km(double v){efficiencyKwhPer100Km=v;}
 public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
