package com.ibm.inventory_management.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StockItemCommand {

    public enum Type { ADD, UPDATE, DELETE }

    private final Type type;
    private final String id;
    private final String name;
    private final String manufacturer;
    private final double price;
    private final int stock;

    @JsonCreator
    public StockItemCommand(
            @JsonProperty("type") Type type,
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("manufacturer") String manufacturer,
            @JsonProperty("price") double price,
            @JsonProperty("stock") int stock) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.stock = stock;
    }

    public static StockItemCommand add(String name, String manufacturer, double price, int stock) {
        return new StockItemCommand(Type.ADD, null, name, manufacturer, price, stock);
    }

    public static StockItemCommand update(String id, String name, String manufacturer, double price, int stock) {
        return new StockItemCommand(Type.UPDATE, id, name, manufacturer, price, stock);
    }

    public static StockItemCommand delete(String id) {
        return new StockItemCommand(Type.DELETE, id, null, null, 0, 0);
    }

    public Type getType() { return type; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getManufacturer() { return manufacturer; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
}
