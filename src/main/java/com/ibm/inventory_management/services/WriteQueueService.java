package com.ibm.inventory_management.services;

import com.ibm.inventory_management.models.StockItemCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WriteQueueService {

    private static final Logger log = LoggerFactory.getLogger(WriteQueueService.class);
    static final String TOPIC = "stock-items-commands";

    private final KafkaTemplate<String, StockItemCommand> kafkaTemplate;

    public WriteQueueService(KafkaTemplate<String, StockItemCommand> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public boolean enqueue(StockItemCommand command) {
        try {
            kafkaTemplate.send(TOPIC, command);
            return true;
        } catch (Exception e) {
            log.error("Impossible d'envoyer la commande sur Kafka: {}", e.getMessage());
            return false;
        }
    }
}
