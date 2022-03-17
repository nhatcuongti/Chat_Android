package com.example.meza.model;

import java.io.Serializable;

public class User implements Serializable {
    public String username, image, phone, password, id;
    public boolean isActive;

    public User() {

    }

    public User(String username, String image, String phone, String password, String id, boolean isActive) {
        this.username = username;
        this.image = image;
        this.phone = phone;
        this.password = password;
        this.id = id;
        this.isActive = isActive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
