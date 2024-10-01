package com.example.fishfolio;

public class Users {
    String name, gender, eMail, password;

    public Users(String name,String gender, String eMail, String password) {
        this.name = name;
        this.eMail = eMail;
        this.password = password;
        this.gender = gender;
    }


    public Users(String name, String gender, String eMail) {
        this.name = name;
        this.gender = gender;
        this.eMail = eMail;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String geteMail() {
        return eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
