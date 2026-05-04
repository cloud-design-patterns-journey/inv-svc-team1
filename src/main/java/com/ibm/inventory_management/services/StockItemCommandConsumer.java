package com.ibm.inventory_management.services;

import com.ibm.inventory_management.models.AuditEvent;
import com.ibm.inventory_management.models.StockItemCommand;
import com.ibm.inventory_management.models.StockLevelLowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockItemCommandConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockItemCommandConsumer.class);
    static final String AUDIT_TOPIC = "stock-items-audit";
    static final String PRIORITY_TOPIC = "stock-items-priority";

    @Value("${stock.low-threshold:20}")
    private int lowThreshold;

    private final StockItemApi stockItemService;
    private final KafkaTemplate<String, AuditEvent> auditTemplate;
    private final KafkaTemplate<String, StockLevelLowEvent> priorityTemplate;

    public StockItemCommandConsumer(StockItemApi stockItemService,
                                    KafkaTemplate<String, AuditEvent> auditTemplate,
                                    KafkaTemplate<String, StockLevelLowEvent> priorityTemplate) {
        this.stockItemService = stockItemService;
        this.auditTemplate = auditTemplate;
        this.priorityTemplate = priorityTemplate;
    }

    @KafkaListener(topics = WriteQueueService.TOPIC, groupId = "inv-svc-commands", concurrency = "1")
    public void consume(StockItemCommand command) {
        String itemId = command.getId();
        try {
            switch (command.getType()) {
                case ADD -> {
                    stockItemService.addStockItem(command.getName(), command.getManufacturer(), command.getPrice(), command.getStock());
                    emitStockAlertIfNeeded(itemId, command.getName(), command.getStock());
                }
                case UPDATE -> {
                    stockItemService.updateStockItem(command.getId(), command.getName(), command.getManufacturer(), command.getPrice(), command.getStock());
                    emitStockAlertIfNeeded(itemId, command.getName(), command.getStock());
                }
                case DELETE -> stockItemService.deleteStockItem(command.getId());
            }
            auditTemplate.send(AUDIT_TOPIC, new AuditEvent(command.getType(), itemId, AuditEvent.Status.SUCCESS, null));
        } catch (Exception e) {
            log.error("Erreur traitement commande {}: {}", command.getType(), e.getMessage());
            auditTemplate.send(AUDIT_TOPIC, new AuditEvent(command.getType(), itemId, AuditEvent.Status.FAILURE, e.getMessage()));
        }
    }

    private void emitStockAlertIfNeeded(String itemId, String itemName, int stock) {
        if (stock < lowThreshold) {
            log.warn("Stock critique détecté pour '{}' : {} unités restantes", itemName, stock);
            priorityTemplate.send(PRIORITY_TOPIC, new StockLevelLowEvent(itemId, itemName, stock, lowThreshold));
        }
    }
}
