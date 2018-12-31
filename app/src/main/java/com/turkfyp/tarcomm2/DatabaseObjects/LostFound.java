package com.turkfyp.tarcomm2.DatabaseObjects;

public class LostFound {

    private String category;
    private String lostItemName;
    private String lostItemDesc;
    private String lostItemURL;
    private String lostDate;
    private String email;
    private String contactName;
    private String contactNo;
    private String lastModified;

    public LostFound() {
    }

    public LostFound(String category, String lostItemName, String lostItemDesc, String lostItemURL, String lostDate, String email, String contactName, String contactNo, String lastModified) {
        this.category = category;
        this.lostItemName = lostItemName;
        this.lostItemDesc = lostItemDesc;
        this.lostItemURL = lostItemURL;
        this.lostDate = lostDate;
        this.email = email;
        this.contactName = contactName;
        this.contactNo = contactNo;
        this.lastModified = lastModified;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLostItemName() {
        return lostItemName;
    }

    public void setLostItemName(String lostItemName) {
        this.lostItemName = lostItemName;
    }

    public String getLostItemDesc() {
        return lostItemDesc;
    }

    public void setLostItemDesc(String lostItemDesc) {
        this.lostItemDesc = lostItemDesc;
    }

    public String getLostItemURL() {
        return lostItemURL;
    }

    public void setLostItemURL(String lostItemURL) {
        this.lostItemURL = lostItemURL;
    }

    public String getLostDate() { return lostDate; }

    public void setLostDate(String lostDate) { this.lostDate = lostDate; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
