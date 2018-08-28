package com.turkfyp.tarcomm2.DatabaseObjects;

/**
 * Created by User-PC on 7/10/2017.
 */

public class Item {

    private int itemID;
    private String itemSeller;
    private String itemName;
    private double itemPrice;

    private String itemDescription;
    private String itemCategory;
    private String dateAdded;
    private String imageURL;
    private String sellerContact;

    private String desiredLocation;

    public Item(String itemSeller, String itemName, double itemPrice, String itemDescription, String itemCategory, String dateAdded, String imageURL, String sellerContact, String desiredLocation) {
        this.setItemSeller(itemSeller);
        this.setItemName(itemName);
        this.setItemPrice(itemPrice);
        this.setItemDescription(itemDescription);
        this.setItemCategory(itemCategory);
        this.setDateAdded(dateAdded);
        this.setImageURL(imageURL);
        this.setSellerContact(sellerContact);
        this.setDesiredLocation(desiredLocation);
    }

    public Item()
    {
        this.setItemSeller("");
        this.setItemName("");
        this.setItemPrice(0.00);
        this.setItemDescription("");
        this.setItemCategory("");
        this.setDateAdded("");
        this.setImageURL("");
        this.setSellerContact("");
        this.setDesiredLocation("");

    }


    //GETTER AND SETTER
    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

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

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSellerContact() {
        return sellerContact;
    }

    public void setSellerContact(String sellerContact) {
        this.sellerContact = sellerContact;
    }


    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemSeller() {
        return itemSeller;
    }

    public void setItemSeller(String itemSeller) {
        this.itemSeller = itemSeller;
    }

    public String getDesiredLocation() {
        return desiredLocation;
    }

    public void setDesiredLocation(String desiredLocation) {
        this.desiredLocation = desiredLocation;
    }
}
