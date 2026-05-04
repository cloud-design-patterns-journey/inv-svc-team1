package com.ibm.inventory_management.services;

import com.ibm.inventory_management.models.AuditEvent;
import com.ibm.inventory_management.models.StockItemCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockItemCommandConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockItemCommandConsumer.class);
    static final String AUDIT_TOPIC = "stock-items-audit";

    private final StockItemApi stockItemService;
    private final KafkaTemplate<String, AuditEvent> auditTemplate;

    public StockItemCommandConsumer(StockItemApi stockItemService,
                                    KafkaTemplate<String, AuditEvent> auditTemplate) {
        this.stockItemService = stockItemService;
        this.auditTemplate = auditTemplate;
    }

    @KafkaListener(topics = WriteQueueService.TOPIC, groupId = "inv-svc-commands")
    public void consume(StockItemCommand command) {
        String itemId = command.getId();
        try {
            switch (command.getType()) {
                case ADD    -> stockItemService.addStockItem(command.getName(), command.getManufacturer(), command.getPrice(), command.getStock());
                case UPDATE -> stockItemService.updateStockItem(command.getId(), command.getName(), command.getManufacturer(), command.getPrice(), command.getStock());
                case DELETE -> stockItemService.deleteStockItem(command.getId());
            }
            auditTemplate.send(AUDIT_TOPIC, new AuditEvent(command.getType(), itemId, AuditEvent.Status.SUCCESS, null));
        } catch (Exception e) {
            log.error("Erreur traitement commande {}: {}", command.getType(), e.getMessage());
            auditTemplate.send(AUDIT_TOPIC, new AuditEvent(command.getType(), itemId, AuditEvent.Status.FAILURE, e.getMessage()));
        }
    }
}
