package com.evsmartai.repo;
import com.evsmartai.model.UsedEvEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface UsedEvEvaluationRepository extends JpaRepository<UsedEvEvaluation, Long> {
    List<UsedEvEvaluation> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
