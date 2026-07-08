package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    List<AuditLog> findAllByOrderByCreatedAtDesc();
}