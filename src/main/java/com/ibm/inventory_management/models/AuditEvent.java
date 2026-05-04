package com.ibm.inventory_management.models;

import java.time.Instant;

public class AuditEvent {

    public enum Status { SUCCESS, FAILURE }

    private Instant timestamp;
    private StockItemCommand.Type operation;
    private String itemId;
    private Status status;
    private String errorMessage;

    public AuditEvent() {}

    public AuditEvent(StockItemCommand.Type operation, String itemId, Status status, String errorMessage) {
        this.timestamp = Instant.now();
        this.operation = operation;
        this.itemId = itemId;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public StockItemCommand.Type getOperation() { return operation; }
    public void setOperation(StockItemCommand.Type operation) { this.operation = operation; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
