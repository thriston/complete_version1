package com.example.bookster;

public class Category {
    private String name;
    private String description;
    private  String numAds;
    private String imgURL;
    private static int numCategory;

    public Category(String name, String description, String numAds, String imgURL) {
        this.name = name;
        this.description = description;
        this.numAds = numAds;
        this.imgURL = imgURL;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumAds() {
        return numAds;
    }

    public void setNumAds(String numAds) {
        this.numAds = numAds;
    }
}
