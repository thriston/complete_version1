package com.example.bookster;

import java.io.Serializable;

public class PurchaseRequest implements Serializable {
    private String ID;
    private String message;
    private String location;
    private String status;
    private String senderUID;
    private String receiverUID;
    private String senderName;
    private String receiverName;
    private Product product;
    private Long date;
    private String type;

    public PurchaseRequest(String ID, String message, String location, String status, String senderUID, String senderName, Product product, Long date) {
        this.ID = ID;
        this.message = message;
        this.location = location;
        this.status = status;
        this.senderUID = senderUID;
        this.senderName = senderName;
        this.product = product;
        this.date = date;
        this.type = "Purchase";
    }

    public PurchaseRequest()
    {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
