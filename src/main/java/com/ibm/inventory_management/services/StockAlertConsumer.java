package com.ibm.inventory_management.services;

import com.ibm.inventory_management.models.StockLevelLowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockAlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockAlertConsumer.class);

    // concurrency = 3 : traité en priorité avec plus de threads que le topic normal
    @KafkaListener(topics = StockItemCommandConsumer.PRIORITY_TOPIC, groupId = "inv-svc-alerts", concurrency = "3")
    public void handleCriticalEvent(StockLevelLowEvent event) {
        log.warn("[PRIORITÉ CRITIQUE] Stock bas — item: '{}' (id: {}), stock: {}/{} unités",
                event.getItemName(), event.getItemId(), event.getCurrentStock(), event.getThreshold());
    }
}
