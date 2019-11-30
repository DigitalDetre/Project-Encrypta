package com.example.encryptaapplication.model;

public class usermodel {
    private String Name;
    private String Username;
    private String Profile;
    private String Email;
    private String ParentID;

    public String getParentID() {
        return ParentID;
    }

    public usermodel(){}
    public usermodel(String name, String username, String profile, String email, String ParentID) {
        Name = name;
        Username = username;
        Profile = profile;
        Email = email;
        this.ParentID=ParentID;
    }

    public String getName() {
        return Name;
    }

    public String getUsername() {
        return Username;
    }

    public String getProfile() {
        return Profile;
    }

    public String getEmail() {
        return Email;
    }
}


