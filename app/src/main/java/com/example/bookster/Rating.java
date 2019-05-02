package com.example.bookster;

import java.io.Serializable;

//This class is used as a blueprint to add product/item ratings on Firebase


public class Rating implements Serializable {

    private String UID;
    private String rating;
    private Boolean hasRated;

    public Rating()
    {

    }

    public Rating(String UID, String rating) {
        this.UID = UID;
        this.rating = rating;
        this.hasRated = false;
    }

    public Boolean getHasRated() {
        return hasRated;
    }

    public void setHasRated(Boolean hasRated) {
        this.hasRated = hasRated;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
