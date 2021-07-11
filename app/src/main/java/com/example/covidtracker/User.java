package com.example.covidtracker;

public class User {
    private String username , email;
    private int age;

    public  User(){

    }
    public User(String username ,int age , String email ) {
        this.username = username;
        this.age = age;
        this.email = email;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
