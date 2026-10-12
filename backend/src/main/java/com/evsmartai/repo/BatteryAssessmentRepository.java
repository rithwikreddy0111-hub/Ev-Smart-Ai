package com.evsmartai.repo;
import com.evsmartai.model.BatteryAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BatteryAssessmentRepository extends JpaRepository<BatteryAssessment, Long> {
    List<BatteryAssessment> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
