package com.ibm.inventory_management.services;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaStockItemListener {
    @Autowired
    private StockItemApi stockItemService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "stock-items-requests", groupId = "stock-items-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            Map<String, Object> message = objectMapper.readValue(record.value(), Map.class);
            String correlationId = (String) message.get("correlationId");
            Map<String, Object> payload = (Map<String, Object>) message.get("payload");
            String action = (String) payload.get("action");
            Object responsePayload = null;
            switch (action) {
                case "list":
                    responsePayload = stockItemService.listStockItems();
                    break;
                case "add":
                    stockItemService.addStockItem(
                        (String) payload.get("name"),
                        (String) payload.get("manufacturer"),
                        Double.parseDouble(payload.get("price").toString()),
                        Integer.parseInt(payload.get("stock").toString())
                    );
                    responsePayload = "OK";
                    break;
                case "update":
                    stockItemService.updateStockItem(
                        (String) payload.get("id"),
                        (String) payload.get("name"),
                        (String) payload.get("manufacturer"),
                        Double.parseDouble(payload.get("price").toString()),
                        Integer.parseInt(payload.get("stock").toString())
                    );
                    responsePayload = "OK";
                    break;
                case "delete":
                    stockItemService.deleteStockItem((String) payload.get("id"));
                    responsePayload = "OK";
                    break;
                default:
                    responsePayload = "Unknown action";
            }
            String response = objectMapper.writeValueAsString(Map.of(
                "correlationId", correlationId,
                "payload", responsePayload
            ));
            kafkaTemplate.send("stock-items-responses", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
