package com.turkfyp.tarcomm2.DatabaseObjects;

public class Friend {

    private String userEmail, friendEmail, type, friendName, profilePicURL;

    public Friend() {
        this.userEmail = "";
        this.friendEmail = "";
        this.type = "";
        this.friendName = "";
        this.profilePicURL = "";
    }

    public Friend(String userEmail, String friendEmail, String type, String friendName, String profilePicURL) {
        this.userEmail = userEmail;
        this.friendEmail = friendEmail;
        this.type = type;
        this.friendName = friendName;
        this.profilePicURL = profilePicURL;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }
}
