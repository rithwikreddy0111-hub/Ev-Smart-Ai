package com.evsmartai.web;
import com.evsmartai.model.*;
import com.evsmartai.repo.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/vehicles")
public class VehicleController {
 public record VehicleResponse(Long id,String make,String model,int modelYear,double batteryCapacityKwh,double batteryPercent,double efficiencyKwhPer100Km) {}
 private VehicleResponse dto(Vehicle v){return new VehicleResponse(v.getId(),v.getMake(),v.getModel(),v.getModelYear(),v.getBatteryCapacityKwh(),v.getBatteryPercent(),v.getEfficiencyKwhPer100Km());}
 private final VehicleRepository vehicles;private final UserRepository users;
 public VehicleController(VehicleRepository v,UserRepository u){vehicles=v;users=u;}
 private AppUser user(Authentication a){return users.findByEmail(a.getName()).orElseThrow();}
 @GetMapping public List<VehicleResponse> all(Authentication a){return vehicles.findAllByUserIdOrderByIdDesc(user(a).getId()).stream().map(this::dto).toList();}
 @PostMapping public ResponseEntity<VehicleResponse> create(@Valid @RequestBody Vehicle v,Authentication a){v.setUser(user(a));return ResponseEntity.status(201).body(dto(vehicles.save(v)));}
 @PutMapping("/{id}") public VehicleResponse update(@PathVariable Long id,@Valid @RequestBody Vehicle input,Authentication a){Vehicle v=vehicles.findByIdAndUserId(id,user(a).getId()).orElseThrow(()->new NoSuchElementException("Vehicle not found"));v.setMake(input.getMake());v.setModel(input.getModel());v.setModelYear(input.getModelYear());v.setBatteryCapacityKwh(input.getBatteryCapacityKwh());v.setBatteryPercent(input.getBatteryPercent());v.setEfficiencyKwhPer100Km(input.getEfficiencyKwhPer100Km());return dto(vehicles.save(v));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id,Authentication a){Vehicle v=vehicles.findByIdAndUserId(id,user(a).getId()).orElseThrow(()->new NoSuchElementException("Vehicle not found"));vehicles.delete(v);return ResponseEntity.noContent().build();}
}
