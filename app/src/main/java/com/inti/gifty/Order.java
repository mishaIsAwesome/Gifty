package com.inti.gifty;

import java.io.Serializable;

class Order implements Serializable {

    private String id;
    private Friend friend;
    private String dateOrdered;
    private String dateETA;
    private String dateSent;
    private String dateReceived;
    private double totalAmount;
    private String status;

    public Order(String id, Friend friend, String dateOrdered, String dateETA, String dateSent, String dateReceived, double totalAmount, String status) {
        this.id = id;
        this.friend = friend;
        this.dateOrdered = dateOrdered;
        this.dateETA = dateETA;
        this.dateSent = dateSent;
        this.dateReceived = dateReceived;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(String dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public String getDateETA() {
        return dateETA;
    }

    public void setDateETA(String dateETA) {
        this.dateETA = dateETA;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
