package com.example.bookster;


import java.io.Serializable;

public class Product implements Serializable {
    private String ID;
    private String name;
    private String price;
    private String category;
    private String details;
    private User seller;

    public Product(String ID, String name, String price, String details, String category, User seller) {
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.category = category;
        this.details = details;
        this.seller = seller;
    }

    public Product()
    {

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public User getSeller() {

        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }


}
