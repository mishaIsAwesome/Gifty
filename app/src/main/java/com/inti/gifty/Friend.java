package com.inti.gifty;

import java.io.Serializable;
import java.util.ArrayList;

class Friend implements Serializable {

    private String id;
    private String name;
    private String image;
    private String occasion;
    private String date;
    private String occurrence;
    private int reminderId;
    private String addressLine;
    private String state;
    private String city;
    private String postalCode;
    private String notes;
    private Boolean priority;
    private ArrayList<CartItem> wishlist;

    public Friend(){}

    public Friend(String id, String name, String image, String occasion, String date, String occurrence, int reminderId, String addressLine, String state, String city, String postalCode, String notes, Boolean priority, ArrayList<CartItem> wishlist) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.occasion = occasion;
        this.date = date;
        this.occurrence = occurrence;
        this.reminderId = reminderId;
        this.addressLine = addressLine;
        this.state = state;
        this.city = city;
        this.postalCode = postalCode;
        this.notes = notes;
        this.priority = priority;
        this.wishlist = wishlist;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public ArrayList<CartItem> getWishlist() {
        return wishlist;
    }

    public void setWishlist(ArrayList<CartItem> wishlist) {
        this.wishlist = wishlist;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", occasion='" + occasion + '\'' +
                ", date=" + date +
                ", occurrence='" + occurrence + '\'' +
                ", addressLine='" + addressLine + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", notes='" + notes + '\'' +
                ", priority=" + priority +
                ", wishlist=" + wishlist +
                '}';
    }
}
