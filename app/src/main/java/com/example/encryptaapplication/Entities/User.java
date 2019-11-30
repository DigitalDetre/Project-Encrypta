package com.example.encryptaapplication.Entities;

public class User {
    private String email;
    private String image;
    private String name;
    private String username;


    public User() {

    }

    public User(String email, String image, String name, String username) {
        setEmail(email);
        setImage(image);
        setName(name);
        setUsername(username);
    }


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
