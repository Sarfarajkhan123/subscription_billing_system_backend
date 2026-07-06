package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.AuditLog;
import com.subscript.subscription_billing_system.entity.User;
import com.subscript.subscription_billing_system.repository.AuditLogRepository;
import com.subscript.subscription_billing_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    // Log any action — called from other services when important things happen
    public AuditLog log(Integer userId, String action, String entityType,
            Integer entityId, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        return auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getLogsByUser(Integer userId) {
        return auditLogRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    public AuditLog getLogById(Integer id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log not found: " + id));
    }

    // Audit logs should never be deleted in a real app
    // But we add it for completeness during testing
    public void deleteLog(Integer id) {
        getLogById(id);
        auditLogRepository.deleteById(id);
    }
}