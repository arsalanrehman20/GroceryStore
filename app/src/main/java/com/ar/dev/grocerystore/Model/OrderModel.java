package com.ar.dev.grocerystore.Model;

public class OrderModel {
    private String orderID;
    private String orderDate;
    private String orderStatus;
    private String orderAmount;

    public OrderModel(){}

    public OrderModel(String orderID, String orderDate, String orderStatus, String orderAmount) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderAmount = orderAmount;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderAmount() {
        return orderAmount;
    }
}
