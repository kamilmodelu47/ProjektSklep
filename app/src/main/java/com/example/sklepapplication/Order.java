package com.example.sklepapplication;

import java.util.Date;
import java.util.List;

import java.util.List;

public class Order {
    private String customerName;
    private List<String> products;
    private float totalPrice;
    private String orderDate;

    public Order(String customerName, List<String> products, float totalPrice, String orderDate) {
        this.customerName = customerName;
        this.products = products;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "customerName='" + customerName + '\'' +
                ", products=" + products +
                ", totalPrice=" + totalPrice +
                ", orderDate='" + orderDate + '\'' +
                '}';
    }
}

