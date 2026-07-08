package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.SaasService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SaasServiceRepository extends JpaRepository<SaasService, Integer> {
    List<SaasService> findByIsActiveTrue();
}
