package com.example.bookster;


import java.io.Serializable;

public class Product implements Serializable {
    private String ID;
    private String name;
    private String price;
    private String category;
    private int views;
    private int bids;
    private String details;
    private String quantity;
    private User seller;

    public Product(String ID, String name, String price, String quantity, String details, String category, User seller) {
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.details = details;
        this.seller = seller;
        this.bids = 0;
        this.views = 0;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getBids() {
        return bids;
    }

    public void setBids(int bids) {
        this.bids = bids;
    }

    public void addView()
    {
        this.views = this.views + 1;
    }

    public void addBid()
    {
        this.bids = this.bids + 1;
    }
}
