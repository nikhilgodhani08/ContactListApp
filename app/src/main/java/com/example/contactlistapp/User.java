package com.example.contactlistapp;

public class User {
    User(){}
    String name,number,pfurl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPfurl() {
        return pfurl;
    }

    public void setPfurl(String pfurl) {
        this.pfurl = pfurl;
    }

    public User(String name, String number, String pfurl) {
        this.name = name;
        this.number = number;
        this.pfurl = pfurl;
    }
}
