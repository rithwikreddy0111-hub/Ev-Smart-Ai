package com.evsmartai.web;
import com.evsmartai.model.*;
import com.evsmartai.repo.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
@RestController @RequestMapping("/api/bookings")
public class BookingController {
 public record BookingResponse(Long id,String stationName,Double stationLat,Double stationLon,java.time.LocalDateTime bookingStart,int durationMinutes,String status,boolean simulation) {}
 private BookingResponse dto(ChargingBooking b){return new BookingResponse(b.getId(),b.getStationName(),b.getStationLat(),b.getStationLon(),b.getBookingStart(),b.getDurationMinutes(),b.getStatus(),b.isSimulation());}
 private final BookingRepository bookings;private final UserRepository users;
 public BookingController(BookingRepository b,UserRepository u){bookings=b;users=u;}
 private AppUser user(Authentication a){return users.findByEmail(a.getName()).orElseThrow();}
 @GetMapping public List<BookingResponse> all(Authentication a){return bookings.findAllByUserIdOrderByBookingStartDesc(user(a).getId()).stream().map(this::dto).toList();}
 @PostMapping public ResponseEntity<BookingResponse> create(@Valid @RequestBody ChargingBooking b,Authentication a){
  if(b.getBookingStart()==null||!b.getBookingStart().isAfter(LocalDateTime.now()))throw new IllegalArgumentException("Booking start must be in the future");
  LocalDateTime end=b.getBookingStart().plusMinutes(b.getDurationMinutes());
  boolean conflict=bookings.findAllByUserIdOrderByBookingStartDesc(user(a).getId()).stream().anyMatch(x->"BOOKED".equals(x.getStatus())&&x.getBookingStart().isBefore(end)&&x.getBookingStart().plusMinutes(x.getDurationMinutes()).isAfter(b.getBookingStart()));
  if(conflict)throw new IllegalArgumentException("This booking overlaps another booking in your account");
  b.setUser(user(a));b.setStatus("BOOKED");return ResponseEntity.status(201).body(dto(bookings.save(b)));
 }
 @DeleteMapping("/{id}") public ResponseEntity<?> cancel(@PathVariable Long id,Authentication a){ChargingBooking b=bookings.findByIdAndUserId(id,user(a).getId()).orElseThrow(()->new NoSuchElementException("Booking not found"));b.setStatus("CANCELLED");bookings.save(b);return ResponseEntity.noContent().build();}
}
