package com.ibm.inventory_management.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ibm.inventory_management.models.StockItem;
import com.ibm.inventory_management.models.StockItemCommand;
import com.ibm.inventory_management.services.StockItemApi;
import com.ibm.inventory_management.services.WriteQueueService;

@RestController
public class StockItemController {

    private final StockItemApi service;
    private final WriteQueueService writeQueueService;

    public StockItemController(StockItemApi service, WriteQueueService writeQueueService) {
        this.service = service;
        this.writeQueueService = writeQueueService;
    }

    @GetMapping(path = "/stock-items", produces = "application/json")
    public List<StockItem> listStockItems() {
        return this.service.listStockItems();
    }

    @PostMapping(path = "/stock-item")
    public ResponseEntity<Void> addStockItem(@RequestParam String name, @RequestParam String manufacturer,
            @RequestParam float price, @RequestParam int stock) {
        boolean accepted = writeQueueService.enqueue(StockItemCommand.add(name, manufacturer, price, stock));
        return accepted
                ? ResponseEntity.accepted().build()
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @PutMapping(path = "/stock-item/{id}")
    public ResponseEntity<Void> updateStockItem(@PathVariable("id") String id, @RequestParam String name,
            @RequestParam String manufacturer, @RequestParam float price, @RequestParam int stock) {
        boolean accepted = writeQueueService.enqueue(StockItemCommand.update(id, name, manufacturer, price, stock));
        return accepted
                ? ResponseEntity.accepted().build()
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @DeleteMapping(path = "/stock-item/{id}")
    public ResponseEntity<Void> deleteStockItem(@PathVariable("id") String id) {
        boolean accepted = writeQueueService.enqueue(StockItemCommand.delete(id));
        return accepted
                ? ResponseEntity.accepted().build()
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
