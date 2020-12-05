package com.inti.gifty;

class CartItem extends Item{

    private int quantity;
    private double amount;

    public CartItem(String id, String name, String shop, String image, double price, String desc, double rating, int quantity, double amount) {
        super(id, name, shop, image, price, desc, rating);
        this.quantity = quantity;
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
