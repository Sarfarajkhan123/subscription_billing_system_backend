package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.AuditLogController;
import com.subscript.subscription.api.model.AuditLog;
import com.subscript.subscription.service.service.interfaces.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuditLogControllerImpl implements AuditLogController {

    private final AuditLogService auditLogService;

    @Override
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

    @Override
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(
            @PathVariable Integer userId) {

        return ResponseEntity.ok(auditLogService.getLogsByUser(userId));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getLogById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(auditLogService.getLogById(id));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(
            @PathVariable Integer id) {

        auditLogService.deleteLog(id);
        return ResponseEntity.ok("Log deleted");
    }
}