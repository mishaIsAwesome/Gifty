package com.inti.gifty;

import java.io.Serializable;

class Item implements Serializable {
    private String id;
    private String name;
    private String shop;
    private String image;
    private double price;
    private String desc;
    private double rating;

    public Item(String id, String name, String shop, String image, double price, String desc, double rating) {
        this.id = id;
        this.name = name;
        this.shop = shop;
        this.image = image;
        this.price = price;
        this.desc = desc;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
