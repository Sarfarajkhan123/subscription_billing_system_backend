package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    List<AuditLog> findAllByOrderByCreatedAtDesc();
}