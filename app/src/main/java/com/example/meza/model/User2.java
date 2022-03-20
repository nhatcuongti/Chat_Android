package com.example.meza.model;

import java.util.ArrayList;

/**
 * Created by reiko-lhnhat on 3/20/2022.
 */
public class User2 {
    private String fullname;
    private String image;
    private String phone_number;
    private int is_active;
    private ArrayList<String> list_friend;

    public User2() {
    }

    public User2(String fullname, String img, String phoneNumber, int isActive, ArrayList<String> listFriend) {
        this.fullname = fullname;
        this.image = img;
        this.phone_number = phoneNumber;
        this.is_active = isActive;
        this.list_friend = listFriend;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public ArrayList<String> getList_friend() {
        return list_friend;
    }

    public void setList_friend(ArrayList<String> list_friend) {
        this.list_friend = list_friend;
    }

}
