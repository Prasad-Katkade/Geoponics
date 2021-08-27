package com.example.geoponics;

public class UserModel {
    public String username,email,phno,address,isFarmer;

    public UserModel() {
    }

    public UserModel(String username, String email, String phno, String address, String isFarmer) {
        this.username = username;
        this.email = email;
        this.phno = phno;
        this.address = address;
        this.isFarmer = isFarmer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIsFarmer() {
        return isFarmer;
    }

    public void setIsFarmer(String isFarmer) {
        this.isFarmer = isFarmer;
    }
}
