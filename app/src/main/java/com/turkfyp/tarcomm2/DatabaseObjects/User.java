package com.turkfyp.tarcomm2.DatabaseObjects;

/**
 * Created by User-PC on 8/10/2017.
 */

public class User {

    private String email;
    private String password;
    private String fullname;
    private String gender;
    private String dateofbirth;
    private String contactno;
    private String latitude;
    private String longitude;
    private String status;
    private String profilepicURL;


    public User(){


    }

    public User(String email, String password, String fullname, String gender, String dateofbirth, String contactno, String profilepicURL) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
        this.contactno = contactno;
        this.profilepicURL = profilepicURL;
    }

    public User(String email, String password, String fullname, String gender, String dateofbirth, String contactno, String latitude, String longitude, String status, String profilepicURL) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
        this.contactno = contactno;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.profilepicURL = profilepicURL;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getFullname() { return fullname; }

    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getDateofbirth() { return dateofbirth; }

    public void setDateofbirth(String dateofbirth) { this.dateofbirth = dateofbirth; }

    public String getContactno() { return contactno; }

    public void setContactno(String contactno) { this.contactno = contactno; }

    public String getLatitude() { return latitude; }

    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }

    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getProfilepicURL() { return profilepicURL; }

    public void setProfilepicURL(String profilepicURL) { this.profilepicURL = profilepicURL; }
}
