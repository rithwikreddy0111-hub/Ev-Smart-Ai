package com.evsmartai.web;

import com.evsmartai.model.AppUser;
import com.evsmartai.model.UsedEvEvaluation;
import com.evsmartai.repo.UsedEvEvaluationRepository;
import com.evsmartai.repo.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController @RequestMapping("/api/used-ev-evaluations")
@CrossOrigin(
    origins = "https://ev-smart-qzy9popyj-rithwik9.vercel.app/",
    allowCredentials = "true"
)
public class UsedEvEvaluationController {
    public record CreateRequest(@NotBlank @Size(max=80) String make,@NotBlank @Size(max=100) String model,
        @Min(1990) @Max(2100) int modelYear,@DecimalMin("0.0") @DecimalMax("100000000.0") double askingPrice,
        @Min(0) @Max(2000000) int mileageKm,@DecimalMin("0.0") @DecimalMax("100.0") double batteryHealthPercent,
        boolean serviceHistoryAvailable,@DecimalMin("0.0") @DecimalMax("100.0") double score,@Size(max=1000) String riskSummary) {}
    public record EvaluationResponse(Long id,String make,String model,int modelYear,double askingPrice,int mileageKm,double batteryHealthPercent,boolean serviceHistoryAvailable,double score,String riskSummary,Instant createdAt) {}
    private final UsedEvEvaluationRepository evaluations;private final UserRepository users;
    public UsedEvEvaluationController(UsedEvEvaluationRepository e,UserRepository u){evaluations=e;users=u;}
    private AppUser user(Authentication a){return users.findByEmail(a.getName()).orElseThrow();}
    private EvaluationResponse dto(UsedEvEvaluation e){return new EvaluationResponse(e.getId(),e.getMake(),e.getModel(),e.getModelYear(),e.getAskingPrice(),e.getMileageKm(),e.getBatteryHealthPercent(),e.isServiceHistoryAvailable(),e.getScore(),e.getRiskSummary(),e.getCreatedAt());}
    @GetMapping public List<EvaluationResponse> all(Authentication a){return evaluations.findAllByUserIdOrderByCreatedAtDesc(user(a).getId()).stream().map(this::dto).toList();}
    @PostMapping public ResponseEntity<EvaluationResponse> create(@Valid @RequestBody CreateRequest r,Authentication a){
        UsedEvEvaluation e=new UsedEvEvaluation();e.setUser(user(a));e.setMake(r.make().trim());e.setModel(r.model().trim());e.setModelYear(r.modelYear());e.setAskingPrice(r.askingPrice());e.setMileageKm(r.mileageKm());e.setBatteryHealthPercent(r.batteryHealthPercent());e.setServiceHistoryAvailable(r.serviceHistoryAvailable());e.setScore(r.score());e.setRiskSummary(r.riskSummary());
        return ResponseEntity.status(201).body(dto(evaluations.save(e)));
    }
}
