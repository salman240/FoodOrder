package com.example.salmangeforce.food_order.Model;

public class Order {

    private String ProductId;
    private String ProductName;
    private String Price;
    private String Quantity;
    private String Discount;

    public Order(){}

    public Order(String productId, String productName, String quantity, String price, String discount) {
        ProductId = productId;
        ProductName = productName;
        Price = price;
        Quantity = quantity;
        Discount = discount;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
