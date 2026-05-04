package com.ibm.inventory_management.models;

import java.time.Instant;

public class StockLevelLowEvent {

    private Instant timestamp;
    private String itemId;
    private String itemName;
    private int currentStock;
    private int threshold;

    public StockLevelLowEvent() {}

    public StockLevelLowEvent(String itemId, String itemName, int currentStock, int threshold) {
        this.timestamp = Instant.now();
        this.itemId = itemId;
        this.itemName = itemName;
        this.currentStock = currentStock;
        this.threshold = threshold;
    }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
    public int getThreshold() { return threshold; }
    public void setThreshold(int threshold) { this.threshold = threshold; }
}
