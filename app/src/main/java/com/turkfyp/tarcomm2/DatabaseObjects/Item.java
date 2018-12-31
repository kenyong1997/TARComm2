package com.turkfyp.tarcomm2.DatabaseObjects;

public class Item {

    private String itemCategory;
    private String itemName;
    private String itemDescription;
    private String imageURL;
    private String itemPrice;
    private String email;

    private String sellerName;
    private String sellerContact;
    private String itemLastModified;

    public Item() {}

    public Item(String itemCategory, String itemName, String itemDescription, String imageURL, String itemPrice, String email, String sellerName, String sellerContact, String itemLastModified) {
        this.itemCategory = itemCategory;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.imageURL = imageURL;
        this.itemPrice = itemPrice;
        this.email = email;
        this.sellerName = sellerName;
        this.sellerContact = sellerContact;
        this.itemLastModified = itemLastModified;
    }

    //GETTER AND SETTER
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
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

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSellerName() { return sellerName; }

    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerContact() { return sellerContact; }

    public void setSellerContact(String sellerContact) { this.sellerContact = sellerContact; }

    public String getItemLastModified() {
        return itemLastModified;
    }

    public void setItemLastModified(String itemLastModified) {
        this.itemLastModified = itemLastModified;
    }
}
