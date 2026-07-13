package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.AuditLog;
import java.util.List;

public interface AuditLogService {
    List<AuditLog> getAllLogs();
    AuditLog log(Integer userId, String action, String entityType, Integer entityId, String description);
    List<AuditLog> getLogsByUser(Integer userId);
    AuditLog getLogById(Integer id);
    void deleteLog(Integer id);
}
