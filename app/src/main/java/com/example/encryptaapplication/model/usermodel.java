package com.example.encryptaapplication.model;

public class usermodel {
    String Name;
    String Username;
    String Profile;
    String Email;

    public usermodel(String name, String username, String profile, String email) {
        Name = name;
        Username = username;
        Profile = profile;
        Email = email;
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


