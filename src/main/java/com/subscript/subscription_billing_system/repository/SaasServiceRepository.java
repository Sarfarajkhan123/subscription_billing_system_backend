package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.SaasService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SaasServiceRepository extends JpaRepository<SaasService, Integer> {
    List<SaasService> findByIsActiveTrue();
}
