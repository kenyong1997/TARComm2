package com.turkfyp.tarcomm2.DatabaseObjects;

/**
 * Created by User-PC on 8/10/2017.
 */

public class User {

    private String userName;
    private String password;
    private String contactNumber;
    private String userEmail;
    private String latitude;
    private String longitude;
    private String status;




    public User(){


    }

    public User (String userName, String password, String contactNumber, String userEmail)
    {
        this.setUserName(userName);
        this.setPassword(password);
        this.setContactNumber(contactNumber);
        this.setUserEmail(userEmail);

    }

    public User (String userName, String password, String contactNumber, String userEmail, String latitude, String longitude, String status)
    {
        this.setUserName(userName);
        this.setPassword(password);
        this.setContactNumber(contactNumber);
        this.setUserEmail(userEmail);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setStatus(status);
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
