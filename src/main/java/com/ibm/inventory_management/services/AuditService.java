package com.ibm.inventory_management.services;

import com.ibm.inventory_management.models.AuditEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AuditService {

    private final List<AuditEvent> events = Collections.synchronizedList(new ArrayList<>());

    @KafkaListener(topics = StockItemCommandConsumer.AUDIT_TOPIC, groupId = "inv-svc-audit")
    public void consume(AuditEvent event) {
        events.add(event);
    }

    public List<AuditEvent> getEvents() {
        return List.copyOf(events);
    }
}
