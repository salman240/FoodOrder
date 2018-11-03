package com.example.salmangeforce.food_order.Model;

public class Food {
    private String name;
    private String image;
    private String description;
    private String price;
    private String discount;
    private String menuId;

    public Food(){}

    public Food(String name, String description, String price, String discount, String menuId, String image) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.menuId = menuId;
    }

    // Getter Methods

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getMenuId() {
        return menuId;
    }

    // Setter Methods

    public void setName(String Name) {
        this.name = Name;
    }

    public void setImage(String Image) {
        this.image = Image;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }

    public void setPrice(String Price) {
        this.price = Price;
    }

    public void setDiscount(String Discount) {
        this.discount = Discount;
    }

    public void setMenuId(String MenuId) {
        this.menuId = MenuId;
    }
}
