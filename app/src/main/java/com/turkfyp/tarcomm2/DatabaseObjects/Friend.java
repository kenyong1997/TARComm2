package com.turkfyp.tarcomm2.DatabaseObjects;

public class Friend {

    private String email1,email2,type;

    public Friend() {
        this.email1 = "";
        this.email2 = "";
        this.type = "";
    }
    public Friend(String email1, String email2, String type) {
        this.email1 = email1;
        this.email2 = email2;
        this.type = type;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
