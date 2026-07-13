package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.Meter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MeterRepository extends JpaRepository<Meter, Integer> {
    List<Meter> findByService_ServiceId(Integer serviceId);
}
