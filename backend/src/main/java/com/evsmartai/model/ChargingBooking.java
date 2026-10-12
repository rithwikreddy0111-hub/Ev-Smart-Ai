package com.evsmartai.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.*;
@Entity @Table(name="charging_bookings",indexes=@Index(name="idx_booking_user_start",columnList="user_id,booking_start"))
public class ChargingBooking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private AppUser user;
 @Size(max=200) @NotBlank @Column(nullable=false,length=200) private String stationName;
 @Column(name="station_lat") private Double stationLat;
 @Column(name="station_lon") private Double stationLon;
 // No @Future here: entity validation also runs on update, which would block cancelling a booking whose start has passed.
 @Column(name="booking_start",nullable=false) private Instant bookingStart;
 @Min(15) @Max(480) @Column(name="duration_minutes",nullable=false) private int durationMinutes;
 @Column(nullable=false,length=20) private String status="BOOKED";
 @Column(name="is_simulation",nullable=false) private boolean simulation=true;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 public Long getId(){return id;} public AppUser getUser(){return user;} public void setUser(AppUser v){user=v;}
 public String getStationName(){return stationName;} public void setStationName(String v){stationName=v;}
 public Double getStationLat(){return stationLat;} public void setStationLat(Double v){stationLat=v;}
 public Double getStationLon(){return stationLon;} public void setStationLon(Double v){stationLon=v;}
 public Instant getBookingStart(){return bookingStart;} public void setBookingStart(Instant v){bookingStart=v;}
 public int getDurationMinutes(){return durationMinutes;} public void setDurationMinutes(int v){durationMinutes=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public boolean isSimulation(){return simulation;} public Instant getCreatedAt(){return createdAt;}
}
