package com.example.salmangeforce.food_order.Model;

public class Rating {
    private String rating;
    private String comment;
    private String foodId;

    public Rating(){}

    public Rating( String rating, String comment, String foodId) {
        this.rating = rating;
        this.comment = comment;
        this.foodId = foodId;
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
}
