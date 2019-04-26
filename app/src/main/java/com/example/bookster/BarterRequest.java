package com.example.bookster;

public class BarterRequest {
    private String ID;
    private String message;
    private String location;
    private String status;
    private String senderUID;
    private String senderName;
    private Product sellerProduct;
    private Product myProduct;
    private Long date;

    public BarterRequest(String ID, String message, String location, String status, String senderUID, String senderName, Product sellerProduct, Product myProduct , Long date) {
        this.ID = ID;
        this.message = message;
        this.location = location;
        this.status = status;
        this.senderUID = senderUID;
        this.senderName = senderName;
        this.sellerProduct = sellerProduct;
        this.myProduct = myProduct;
        this.date = date;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }



    public Product getSellerProduct() {
        return sellerProduct;
    }

    public void setSellerProduct(Product product) {
        this.sellerProduct = product;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Product getMyProduct() {
        return myProduct;
    }

    public void setMyProduct(Product myProduct) {
        this.myProduct = myProduct;
    }
}
