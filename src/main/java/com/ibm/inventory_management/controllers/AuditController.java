package com.ibm.inventory_management.controllers;

import com.ibm.inventory_management.models.AuditEvent;
import com.ibm.inventory_management.services.AuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping(path = "/audit", produces = "application/json")
    public List<AuditEvent> getAuditLog() {
        return auditService.getEvents();
    }
}
