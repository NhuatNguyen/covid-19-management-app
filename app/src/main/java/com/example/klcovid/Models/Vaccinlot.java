package com.example.klcovid.Models;

public class Vaccinlot {
    public String somui;
    public String name;
    public String createdAt;

    public Vaccinlot(String somui, String name, String createdAt) {
        this.somui = somui;
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getSomui() {
        return somui;
    }

    public void setSomui(String somui) {
        this.somui = somui;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

