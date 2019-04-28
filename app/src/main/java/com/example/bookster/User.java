package com.example.bookster;

import java.io.Serializable;

public class User implements Serializable {
    public String fullname, email, contact, myUID, profilePicURL;

    public User(){

    }

    public User(String fullname, String email, String contact, String myUID, String profilePicURL) {
        this.fullname = fullname;
        this.email = email;
        this.contact = contact;
        this.myUID = myUID;
        this.profilePicURL = profilePicURL;
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

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }
}
