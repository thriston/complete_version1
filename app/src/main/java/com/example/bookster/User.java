package com.example.bookster;

import java.io.Serializable;

public class User implements Serializable {
    public String fullname, email, contact, myUID;

    public User(){

    }

    public User(String fullname, String email, String contact, String myUID) {
        this.fullname = fullname;
        this.email = email;
        this.contact = contact;
        this.myUID = myUID;
    }


    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMyUID() {
        return myUID;
    }

    public void setMyUID(String myUID) {
        this.myUID = myUID;
    }
}
