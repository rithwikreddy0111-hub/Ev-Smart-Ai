package com.evsmartai.web;
import com.evsmartai.model.*;
import com.evsmartai.repo.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;
@RestController @RequestMapping("/api/bookings")
public class BookingController {
 // Instants (UTC) avoid comparing the client's local time with the server's clock, which is UTC on most hosts.
 public record CreateRequest(@NotBlank @Size(max=200) String stationName,@DecimalMin("-90.0") @DecimalMax("90.0") Double stationLat,@DecimalMin("-180.0") @DecimalMax("180.0") Double stationLon,@NotNull Instant bookingStart,@Min(15) @Max(480) int durationMinutes){}
 public record BookingResponse(Long id,String stationName,Double stationLat,Double stationLon,Instant bookingStart,int durationMinutes,String status,boolean simulation) {}
 private BookingResponse dto(ChargingBooking b){return new BookingResponse(b.getId(),b.getStationName(),b.getStationLat(),b.getStationLon(),b.getBookingStart(),b.getDurationMinutes(),b.getStatus(),b.isSimulation());}
 private final BookingRepository bookings;private final UserRepository users;
 public BookingController(BookingRepository b,UserRepository u){bookings=b;users=u;}
 private AppUser user(Authentication a){return users.findByEmail(a.getName()).orElseThrow();}
 @GetMapping public List<BookingResponse> all(Authentication a){return bookings.findAllByUserIdOrderByBookingStartDesc(user(a).getId()).stream().map(this::dto).toList();}
 @PostMapping public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateRequest r,Authentication a){
  if(!r.bookingStart().isAfter(Instant.now()))throw new IllegalArgumentException("Booking start must be in the future");
  AppUser u=user(a);
  Instant start=r.bookingStart();
  Instant end=start.plusSeconds(r.durationMinutes()*60L);
  boolean conflict=bookings.findAllByUserIdOrderByBookingStartDesc(u.getId()).stream().anyMatch(x->"BOOKED".equals(x.getStatus())&&x.getBookingStart().isBefore(end)&&x.getBookingStart().plusSeconds(x.getDurationMinutes()*60L).isAfter(start));
  if(conflict)throw new IllegalArgumentException("This booking overlaps another booking in your account");
  ChargingBooking b=new ChargingBooking();
  b.setUser(u);b.setStationName(r.stationName().trim());b.setStationLat(r.stationLat());b.setStationLon(r.stationLon());b.setBookingStart(start);b.setDurationMinutes(r.durationMinutes());b.setStatus("BOOKED");
  return ResponseEntity.status(201).body(dto(bookings.save(b)));
 }
 @DeleteMapping("/{id}") public ResponseEntity<?> cancel(@PathVariable Long id,Authentication a){ChargingBooking b=bookings.findByIdAndUserId(id,user(a).getId()).orElseThrow(()->new NoSuchElementException("Booking not found"));b.setStatus("CANCELLED");bookings.save(b);return ResponseEntity.noContent().build();}
}
