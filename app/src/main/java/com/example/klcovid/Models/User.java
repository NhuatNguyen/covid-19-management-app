package com.example.klcovid.Models;

import java.io.Serializable;

public class User implements Serializable {
    public String Id;
    public String idNumber;
    public String fullName;
    public String address;
    public String phoneNumber;

    public User(String id, String idNumber, String fullName, String address, String phoneNumber) {
        Id = id;
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public User(String idNumber, String fullName, String address, String phoneNumber) {
        this.idNumber = idNumber;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    //    public User(String fullName) {
//        this.fullName = fullName;
//    }
    public User(String id,String phoneNumber) {
        Id = id;
        this.phoneNumber = phoneNumber;
    }
    public User( String fullName, String address, String phoneNumber) {
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

