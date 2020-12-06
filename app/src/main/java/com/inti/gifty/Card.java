package com.inti.gifty;

class Card {
    private String id;
    private String name;
    private String number;
    private String service;
    private String expiryDate;
    private String cvv;

    public Card(){};

    public Card(String id, String name, String number, String service, String expiryDate, String cvv) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.service = service;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
