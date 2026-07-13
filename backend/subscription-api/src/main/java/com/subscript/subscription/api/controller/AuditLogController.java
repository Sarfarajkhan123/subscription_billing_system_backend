package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.AuditLog;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuditLogController {

    ResponseEntity<AuditLog> createLog(
            Integer userId,
            String action,
            String entityType,
            Integer entityId,
            String description);

    ResponseEntity<List<AuditLog>> getAllLogs();

    ResponseEntity<List<AuditLog>> getLogsByUser(Integer userId);

    ResponseEntity<AuditLog> getLogById(Integer id);

    ResponseEntity<String> deleteLog(Integer id);
}