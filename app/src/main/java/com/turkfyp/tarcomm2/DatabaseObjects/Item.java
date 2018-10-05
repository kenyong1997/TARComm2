package com.turkfyp.tarcomm2.DatabaseObjects;

/**
 * Created by User-PC on 7/10/2017.
 */

public class Item {


    private String itemCategory;
    private String itemName;
    private String itemDescription;
    private String imageURL;
    private double itemPrice;
    private String email;

    private String sellerName;
    private String sellerContact;

    public Item() {
    }

    public Item(String itemCategory, String itemName, String itemDescription, String imageURL, double itemPrice, String email, String sellerName, String sellerContact) {
        this.itemCategory = itemCategory;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.imageURL = imageURL;
        this.itemPrice = itemPrice;
        this.email = email;
        this.sellerName = sellerName;
        this.sellerContact = sellerContact;
    }

    //GETTER AND SETTER
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String itemSeller) {
        this.email = email;
    }

    public String getSellerName() { return sellerName; }

    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerContact() { return sellerContact; }

    public void setSellerContact(String sellerContact) { this.sellerContact = sellerContact; }
}
