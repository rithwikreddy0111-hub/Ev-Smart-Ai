package com.evsmartai.web;
import com.evsmartai.model.*;
import com.evsmartai.repo.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/vehicles")
public class VehicleController {
 // Request DTO: binding the JPA entity directly let a client send "id"/"user" and overwrite another user's row.
 public record VehicleRequest(@NotBlank @Size(max=80) String make,@NotBlank @Size(max=100) String model,
  @Min(1990) @Max(2100) int modelYear,@DecimalMin("1.0") @DecimalMax("300.0") double batteryCapacityKwh,
  @DecimalMin("0.0") @DecimalMax("100.0") Double batteryPercent,@DecimalMin("1.0") @DecimalMax("100.0") double efficiencyKwhPer100Km){}
 public record VehicleResponse(Long id,String make,String model,int modelYear,double batteryCapacityKwh,double batteryPercent,double efficiencyKwhPer100Km) {}
 private VehicleResponse dto(Vehicle v){return new VehicleResponse(v.getId(),v.getMake(),v.getModel(),v.getModelYear(),v.getBatteryCapacityKwh(),v.getBatteryPercent(),v.getEfficiencyKwhPer100Km());}
 private void apply(Vehicle v,VehicleRequest r){v.setMake(r.make().trim());v.setModel(r.model().trim());v.setModelYear(r.modelYear());v.setBatteryCapacityKwh(r.batteryCapacityKwh());v.setBatteryPercent(r.batteryPercent()==null?80:r.batteryPercent());v.setEfficiencyKwhPer100Km(r.efficiencyKwhPer100Km());}
 private final VehicleRepository vehicles;private final UserRepository users;
 public VehicleController(VehicleRepository v,UserRepository u){vehicles=v;users=u;}
 private AppUser user(Authentication a){return users.findByEmail(a.getName()).orElseThrow();}
 @GetMapping public List<VehicleResponse> all(Authentication a){return vehicles.findAllByUserIdOrderByIdDesc(user(a).getId()).stream().map(this::dto).toList();}
 @PostMapping public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest r,Authentication a){Vehicle v=new Vehicle();v.setUser(user(a));apply(v,r);return ResponseEntity.status(201).body(dto(vehicles.save(v)));}
 @PutMapping("/{id}") public VehicleResponse update(@PathVariable Long id,@Valid @RequestBody VehicleRequest r,Authentication a){Vehicle v=vehicles.findByIdAndUserId(id,user(a).getId()).orElseThrow(()->new NoSuchElementException("Vehicle not found"));apply(v,r);return dto(vehicles.save(v));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id,Authentication a){Vehicle v=vehicles.findByIdAndUserId(id,user(a).getId()).orElseThrow(()->new NoSuchElementException("Vehicle not found"));vehicles.delete(v);return ResponseEntity.noContent().build();}
}
