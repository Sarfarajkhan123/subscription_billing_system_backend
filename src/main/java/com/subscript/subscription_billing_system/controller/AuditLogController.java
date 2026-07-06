package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.AuditLog;
import com.subscript.subscription_billing_system.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuditLogController {

    private final AuditLogService auditLogService;

    // POST /api/audit-logs — manually create a log entry for testing
    @PostMapping
    public ResponseEntity<AuditLog> createLog(
            @RequestParam Integer userId,
            @RequestParam String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Integer entityId,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(
                auditLogService.log(userId, action, entityType, entityId, description));
    }

    // GET /api/audit-logs
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    // GET /api/audit-logs/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(userId));
    }

    // GET /api/audit-logs/1
    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getLogById(@PathVariable Integer id) {
        return ResponseEntity.ok(auditLogService.getLogById(id));
    }

    // DELETE /api/audit-logs/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(@PathVariable Integer id) {
        auditLogService.deleteLog(id);
        return ResponseEntity.ok("Log deleted");
    }
}