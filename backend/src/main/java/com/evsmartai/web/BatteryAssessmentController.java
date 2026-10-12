package com.evsmartai.web;

import com.evsmartai.model.AppUser;
import com.evsmartai.model.BatteryAssessment;
import com.evsmartai.repo.BatteryAssessmentRepository;
import com.evsmartai.repo.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController @RequestMapping("/api/battery-assessments")
@CrossOrigin(
    origins = "https://ev-smart-qzy9popyj-rithwik9.vercel.app/",
    allowCredentials = "true"
)
public class BatteryAssessmentController {
    public record CreateRequest(@DecimalMin("0.0") @DecimalMax("40.0") double batteryAgeYears,
        @Min(0) @Max(20000) int chargeCycles,
        @DecimalMin("0.0") @DecimalMax("100.0") double fastChargePercent,
        @DecimalMin("1.0") @DecimalMax("3000.0") double originalRangeKm,
        @DecimalMin("1.0") @DecimalMax("3000.0") double observedRangeKm,
        @DecimalMin("0.0") @DecimalMax("100.0") double estimatedSohPercent,
        @Size(max=500) String assessmentNote) {}
    public record AssessmentResponse(Long id,double batteryAgeYears,int chargeCycles,double fastChargePercent,double originalRangeKm,double observedRangeKm,double estimatedSohPercent,String assessmentNote,Instant createdAt) {}
    private final BatteryAssessmentRepository assessments; private final UserRepository users;
    public BatteryAssessmentController(BatteryAssessmentRepository a,UserRepository u){assessments=a;users=u;}
    private AppUser user(Authentication a){return users.findByEmail(a.getName()).orElseThrow();}
    private AssessmentResponse dto(BatteryAssessment a){return new AssessmentResponse(a.getId(),a.getBatteryAgeYears(),a.getChargeCycles(),a.getFastChargePercent(),a.getOriginalRangeKm(),a.getObservedRangeKm(),a.getEstimatedSohPercent(),a.getAssessmentNote(),a.getCreatedAt());}
    @GetMapping public List<AssessmentResponse> all(Authentication a){return assessments.findAllByUserIdOrderByCreatedAtDesc(user(a).getId()).stream().map(this::dto).toList();}
    @PostMapping public ResponseEntity<AssessmentResponse> create(@Valid @RequestBody CreateRequest r,Authentication a){
        if(r.observedRangeKm()>r.originalRangeKm()*1.5) throw new IllegalArgumentException("Observed range must not exceed 150% of original range");
        BatteryAssessment x=new BatteryAssessment();x.setUser(user(a));x.setBatteryAgeYears(r.batteryAgeYears());x.setChargeCycles(r.chargeCycles());x.setFastChargePercent(r.fastChargePercent());x.setOriginalRangeKm(r.originalRangeKm());x.setObservedRangeKm(r.observedRangeKm());x.setEstimatedSohPercent(r.estimatedSohPercent());x.setAssessmentNote(r.assessmentNote());
        return ResponseEntity.status(201).body(dto(assessments.save(x)));
    }
}
