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
    private String faculty;
    private String course;
    private String biodata;
    private String lastModified;

    public User(){}

    public User(String email, String password, String fullname, String gender, String dateofbirth, String contactno, String profilepicURL, String faculty, String course, String biodata, String lastModified) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
        this.contactno = contactno;
        this.profilepicURL = profilepicURL;
        this.faculty = faculty;
        this.course = course;
        this.biodata = biodata;
        this.lastModified = lastModified;
    }

    public User(String email, String password, String fullname, String gender, String dateofbirth, String contactno, String latitude, String longitude, String status, String profilepicURL, String faculty, String course, String biodata, String lastModified) {
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
        this.faculty = faculty;
        this.course = course;
        this.biodata = biodata;
        this.lastModified = lastModified;
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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getBiodata() {
        return biodata;
    }

    public void setBiodata(String biodata) {
        this.biodata = biodata;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
