package com.techelevator;

import java.math.*;

public class VendingItem {

    private String item;
    private BigDecimal price;
    private String type;
    private int quantity;
    private int quantitySold = 0;


    public void incrementQuantitySold() {
        quantitySold++;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    // updating this to pass in a string and convert it to BigDecimal here
    public VendingItem(String product, String price, String type, int quantity){
        this.item = product;
        this.price = new BigDecimal(price);
        this.type = type;
        this.quantity = quantity;
    }

    //@Override toString method
    @Override
    public String toString(){
        String returnStr = "";
        if(quantity==0){
            returnStr = "Out of Stock";
        }else {
            returnStr += item + " $" + price;
        }
        return returnStr;
    }
}