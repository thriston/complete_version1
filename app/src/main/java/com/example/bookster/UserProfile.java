package com.example.bookster;

import java.io.Serializable;
import java.util.Date;

public class UserProfile implements Serializable {
    private String fullName;
    private Date dob;
    private String email;
    private String age;
    private String phone;
    private String faculty;
    private String UID;
    private int studentID;


    public UserProfile(String fullName, String email, String phone, String UID) {
        this.fullName = fullName;
        this.email = email;
        this.UID = UID;
        this.phone = phone;
    }

    public UserProfile() {
    }

    //Getters and Setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }
}
