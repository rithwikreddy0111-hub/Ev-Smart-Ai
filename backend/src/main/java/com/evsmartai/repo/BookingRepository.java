package com.evsmartai.repo;
import com.evsmartai.model.ChargingBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface BookingRepository extends JpaRepository<ChargingBooking,Long>{List<ChargingBooking> findAllByUserIdOrderByBookingStartDesc(Long userId); Optional<ChargingBooking> findByIdAndUserId(Long id,Long userId);}
